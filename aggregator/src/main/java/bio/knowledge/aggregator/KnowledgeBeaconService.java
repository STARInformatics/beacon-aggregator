/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.aggregator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.OkHttpClient;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.Util;
import bio.knowledge.aggregator.ontology.Ontology;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.ConceptsApi;
import bio.knowledge.client.api.MetadataApi;
import bio.knowledge.client.api.StatementsApi;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptCategory;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconKnowledgeMapStatement;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconStatementWithDetails;
import bio.knowledge.client.model.ExactMatchResponse;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;

/**
 * 
 * #author Richard Bruskiewich
 *         I am updating the beacon to handle Knowledge Maps.
 *         
 * @author Lance Hannestad
 * 
 *         It may seem wasteful to instantiate a new {@code ConceptApi} (or
 *         other API classes) within each {@code ListSupplier<T>}, but in fact
 *         it is necessary because we're asynchronously setting their ApiClient
 *         objects (which encapsulate the URI to be queried) in
 *         {@code GenericDataService}.
 *         <br><br>
 *         The methods in this class are ugly and confusing.. But it's somewhat
 *         unavoidable. Take a look at how they are used in
 *         {@code GenericKnowledgeService}. A SupplierBuilder builds a
 *         ListSupplier which extends a Supplier, which is used to generate
 *         CompletableFutures.
 *         
 *  @author Richard Bruskiewich (2018)
 *  @author Lance Hannestad (2017-18)
 *  @author Meera Godden (2017)
 *  
 *  		Most queries return a map associating beacons to their results.
 *  		Queries that use exactmatches try to handle internal errors in beacons
 *  		by trying to give the beacon fewer exactmatches at a time.
 *
 */
@Service
public class KnowledgeBeaconService implements Util, SystemTimeOut {

	private static Logger _logger = LoggerFactory.getLogger(KnowledgeBeaconService.class);
	
	@Autowired private QueryTrackerRepository  trackerRepository;

	// This works because {@code GenericKnowledgeService} is extended by {@code
	// KnowledgeBeaconService}, which is a Spring service.
	@Autowired KnowledgeBeaconRegistry registry;

	@Override
	public int countAllBeacons() {
		return registry.countAllBeacons();
	}
	
	@Autowired Ontology ontology;
	
	private Map<String, List<LogEntry>> errorLog = new HashMap<>();
	
	private void clearError(String queryId) {
		if (nullOrEmpty(queryId)) return;
		errorLog.put(queryId, new ArrayList<>());
	}
	
	/**
	 * 
	 * @param queryId
	 * @param beacon
	 * @param query
	 * @param message
	 */
	public void logError(String queryId, Integer beacon, String query, String message) {
		
		if (nullOrEmpty(queryId)||nullOrEmpty(message)) return;
		
		LogEntry entry = new LogEntry(beacon, query, message);
		errorLog.putIfAbsent(queryId, new ArrayList<>());
		errorLog.get(queryId).add(entry);
	}
	
	/**
	 * 
	 * @param queryId
	 * @return
	 */
	public List<LogEntry> getErrorLog(String queryId) {
		return errorLog.getOrDefault(queryId, new ArrayList<>());
	}
	
	/*
	 * Creates a {@code CompletableFuture} that completes when every beacon has completed.
	 * Currently, old {@code errorLog} entries are cleared at the before the querying starts.
	 * 
	 * @param builder
	 * @param sources
	 * @param queryId
	 * @return
	 */
	private <T> CompletableFuture<List<T>>[] query(SupplierBuilder<T> builder, List<Integer> sources, String queryId) {
		clearError(queryId);
		
		List<CompletableFuture<List<T>>> futures = new ArrayList<CompletableFuture<List<T>>>();
				
		for (KnowledgeBeacon beacon : registry.filterKnowledgeBeaconsById(sources)) {
			if (beacon.isEnabled()) {
				ListSupplier<T> supplier = builder.build(beacon);
				CompletableFuture<List<T>> future = CompletableFuture.supplyAsync(supplier);
				futures.add(future);
			}
		}
		
		@SuppressWarnings("unchecked")
		CompletableFuture<List<T>>[] futureArray = futures.toArray(new CompletableFuture[futures.size()]);
		return futureArray;
	}

	/**
	 * 
	 * @param builder
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	protected <T> CompletableFuture<Map<KnowledgeBeacon, List<T>>> queryForMap(SupplierBuilder<T> builder, List<Integer> beacons, String queryId) {
		CompletableFuture<Map<KnowledgeBeacon, List<T>>> combinedFuture = combineFuturesIntoMap(registry.filterKnowledgeBeaconsById(beacons), query(builder, beacons, queryId));
		return combinedFuture;
	}
	
	/**
	 * 
	 * @param builder
	 * @param queryId
	 * @return
	 */
	protected <T> CompletableFuture<List<T>> queryForList(SupplierBuilder<T> builder, String queryId) {
		CompletableFuture<List<T>> combinedFuture = combineFuturesIntoList(query(builder, new ArrayList<>(), queryId));
		return combinedFuture;
	}

	/*
	 * Here we take all of the CompletableFuture objects in futures, and combine
	 * them into a single CompletableFuture object. This combined future is of
	 * type Void, so we need thenApply() to get the proper sort of
	 * CompletableFuture. Also this combinedFuture completes exceptionally if
	 * any of the items in {@code futures} completes exceptionally. Because of
	 * this, we also need to tell it what to do if it completes exceptionally,
	 * which is done with exceptionally().
	 * 
	 * @param <T>
	 * @param futures
	 * @return
	 */
	private <T> CompletableFuture<List<T>> combineFuturesIntoList(CompletableFuture<List<T>>[] futures) {
		return CompletableFuture.allOf(futures).thenApply(x -> {

			List<T> combinedResults = new ArrayList<T>();

			for (CompletableFuture<List<T>> f : futures) {
				List<T> results = f.join();
				if (results != null) {
					for (T c : results) {
						_logger.debug("allOf() DEBUG: "+c.toString());
					}
					combinedResults.addAll(results);
				}
			}

			return combinedResults;
		}).exceptionally((error) -> {
			List<T> combinedResults = new ArrayList<T>();

			for (CompletableFuture<List<T>> f : futures) {
				if (!f.isCompletedExceptionally()) {
					List<T> results = f.join();
					if (results != null) {
						combinedResults.addAll(results);
					}
				}
			}
			return combinedResults;
		});
	}
	
	private <T> CompletableFuture<Map<KnowledgeBeacon, List<T>>> combineFuturesIntoMap(List<KnowledgeBeacon> beacons, CompletableFuture<List<T>>[] futures) {
		return CompletableFuture.allOf(futures).thenApply(x -> {
		
			Map<KnowledgeBeacon, List<T>> combinedResults = new HashMap<>();

			for (int i = 0; i < futures.length; i++) {
				
				KnowledgeBeacon beacon = beacons.get(i);
				CompletableFuture<List<T>> f = futures[i];
				
				List<T> results = f.join();
				if (results != null) {
					combinedResults.put(beacon, results);
				}
			}
			
			return combinedResults;
		}).exceptionally((error) -> {
			
			Map<KnowledgeBeacon, List<T>> combinedResults = new HashMap<>();

			for (int i = 0; i < futures.length; i++) {
				
				KnowledgeBeacon beacon = beacons.get(i);
				CompletableFuture<List<T>> f = futures[i];
				
				if (!f.isCompletedExceptionally()) {
					List<T> results = f.join();
					if (results != null) {
						combinedResults.put(beacon, results);
					}
				}
			}
			return combinedResults;
		});
	}

	/**
	 * Wraps {@code wraps Supplier<List<T>>}, used for the sake of generic
	 * queries in {@code GenericKnowledgeService}. The {@code get()} method
	 * <b>must</b> return a List. It may not return {@code null} or throw an exception
	 * (so that nothing is returned). The list that it returns is concatenated
	 * with the lists returned by other suppliers, and so if there is no data to
	 * return simply return an empty list.
	 * 
	 * @author Lance Hannestad
	 *
	 * @param <T>
	 */
	public abstract class ListSupplier<T> implements Supplier<List<T>> {
		
		/**
		 * The {@code get()} method <b>must</b> return a List, otherwise the
		 * CompletableFuture combining in {@code combineFutures()}. will not
		 * work. To ensure that get() will never return null, I have wrapped it
		 * another method that will be overridden by extended classes. Now even
		 * if the author of those extended classes makes a mistake and allows
		 * for {@code null} to be returned or exceptions be thrown, it should
		 * get caught here and not harm the combining of completable futures.
		 */
		@Override
		public List<T> get() {
			try {
				List<T> result = getList();
				if (result != null) {
					return result;
				} else {
					return new ArrayList<T>();
				}
			} catch (Exception e) {
				return new ArrayList<T>();
			}
		}
		
		public abstract List<T> getList();
	}
	
	/**
	 * A class that builds custom ListSupplier objects, for the use of
	 * generating CompletableFutures within {@code GenericKnowledgeService.query()}.
	 * 
	 * @author Lance Hannestad
	 *
	 * @param <T>
	 */
	public abstract class SupplierBuilder<T> {
		public abstract ListSupplier<T> build(KnowledgeBeacon beacon);
	}
	
	/**
	 * Semi-arbitrary way of checking whether the beacon was unable to handle the particular input it was given.
	 * 
	 * @param e
	 * @return
	 */
	private boolean isInternalError(Exception e) {
		return e.getMessage().toUpperCase().equals("INTERNAL SERVER ERROR");
	}
	
	private void logError(String queryId, ApiClient apiClient, Exception e) {
		
		String message = e.getMessage();
		
		if (e instanceof JsonSyntaxException) {
		        message += " PROBLEM WITH DESERIALIZING SERVER RESPONSE";
		}

		if(message!=null) {
			String msgInContext = "QueryId["+queryId+"],BeaconId["+apiClient.getBeaconId()+"]: "+message;
			_logger.error(msgInContext);
		}
		
		logError(queryId, apiClient.getBeaconId(), apiClient.getQuery(), message);
	}
	
	/******************************************* Timeout Utility Methods *********************************************/
	
	private static int extraTime = 0;
	
	/**
	 * 
	 * @param amount
	 */
	public void increaseExtraTime(int amount) {
		extraTime += amount;
	}
	
	/**
	 * 
	 */
	public void resetExtraTime() {
		extraTime = 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getExtraTime() {
		return extraTime;
	}
	
	/*
	 *  ApiClient timeout weightings here are in milliseconds
	 *  These are used below alongside beacon number and pagesizes 
	 *  to set some reasonable timeouts for various queries
	 */
	public static final int DEFAULT_TIMEOUT_WEIGHTING            = 5000;
	public static final int CONCEPTS_QUERY_TIMEOUT_WEIGHTING     = 60;
	public static final int EXACTMATCHES_QUERY_TIMEOUT_WEIGHTING = 600000; //10 minutes
	public static final int STATEMENTS_QUERY_TIMEOUT_WEIGHTING   = 240; 
	public static final int EVIDENCE_QUERY_TIMEOUT_WEIGHTING     = 40000; 
	public static final int TYPES_QUERY_TIMEOUT_WEIGHTING        = 20000; 
	
	/**
	 * 
	 * @param timeOutWeighting
	 * @param beacons
	 * @param pageSize
	 * @return
	 */
	public int apiWeightedTimeout( Integer timeOutWeighting, List<Integer> beacons, Integer pageSize ) {
		int numberOfBeacons = beacons!=null ? beacons.size() : registry.countAllBeacons() ;
		_logger.debug("apiWeightedTimeout parameters: timeout weight: "+ timeOutWeighting + ", # beacons: "+ numberOfBeacons +", data page size: "+ pageSize);
		return timeOutWeighting*(int)weightedTimeout(beacons,pageSize);
	}
	
	/**
	 * 
	 * @param timeOutWeighting
	 * @param pageSize
	 * @return
	 */
	public int apiWeightedTimeout(Integer timeOutWeighting, Integer pageSize) {
		return apiWeightedTimeout(timeOutWeighting, null, pageSize );
	}
	
	/**
	 * 
	 * @param timeOutWeighting
	 * @return
	 */
	public int apiWeightedTimeout(Integer timeOutWeighting) {
		return apiWeightedTimeout(timeOutWeighting, 0);
	}
	
	/**
	 * 
	 * @return
	 */
	public int apiWeightedTimeout() {
		return apiWeightedTimeout(DEFAULT_TIMEOUT_WEIGHTING);
	}
	
	private ApiClient timedApiClient( 
			String apiName, 
			ApiClient apiClient, 
			Integer timeOutWeighting, 
			List<Integer> beacons, 
			Integer pageSize 
	) {
		
		// Adjust ApiClient connection timeout
		apiClient.setConnectTimeout(  
				apiWeightedTimeout(
						timeOutWeighting, 
						beacons, 
						pageSize
				)
		);

		_logger.debug(apiName+": ApiClient connection timeout is currently set to  '"+String.valueOf(apiClient.getConnectTimeout())+"' milliseconds");

		// Adjust ApiClient read timeout
		OkHttpClient httpClient = apiClient.getHttpClient();
		httpClient.setReadTimeout( 
				apiWeightedTimeout(
						timeOutWeighting, 
						beacons, 
						pageSize
				), 
				TimeUnit.MILLISECONDS
		);

		_logger.debug(apiName+": HTTP client socket read timeout is currently set to '" + httpClient.getReadTimeout() + "' milliseconds");
		
		return apiClient;
	}

	private ApiClient timedApiClient( 
			String apiName, 
			ApiClient apiClient, 
			Integer timeOutWeighting, 
			List<Integer> beacons
	) {
		return timedApiClient(
				apiName,
				apiClient,
				timeOutWeighting,
				beacons,
				0
		);
	}

	private ApiClient timedApiClient( 
			String apiName, 
			ApiClient apiClient, 
			Integer timeOutWeighting, 
			Integer pageSize 
	) {
		return timedApiClient(
				apiName,
				apiClient,
				timeOutWeighting,
				null,
				pageSize
		);
	}

	private ApiClient timedApiClient( 
			String apiName, 
			ApiClient apiClient, 
			Integer timeOutWeighting
	) {
		return timedApiClient(
				apiName,
				apiClient,
				timeOutWeighting,
				0
		);
	}

	private ApiClient timedApiClient(String apiName, ApiClient apiClient) {
		return timedApiClient(
				apiName,
				apiClient,
				DEFAULT_TIMEOUT_WEIGHTING
		);
	}

	/******************************** CONCEPT Data Access *************************************/

	/**
	 * New simplified Knowledge Beacon Concept by keywords accessor.
	 * 
	 * @param keywords
	 * @param conceptTypes
	 * @param size
	 * @param beacon
	 * @return
	 */
	public List<BeaconConcept> getConcepts(
			List<String> keywords,
			List<String> conceptTypes,
			Integer size,
			Integer beacon
	) {
		KnowledgeBeaconImpl beaconImpl = registry.getBeaconById(beacon);
		
		ConceptsApi conceptsApi = 
				new ConceptsApi(
						timedApiClient(
								"Beacon Id: "+beacon.toString()+".getConcepts",
								beaconImpl.getApiClient(),
								CONCEPTS_QUERY_TIMEOUT_WEIGHTING*(Math.max(500, size))
						)
					);
		
		List<BeaconConcept> responses;
		
		try {
			responses = conceptsApi.getConcepts(
					keywords,
					conceptTypes,
					size
			);
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
		
		return responses;
	}

	/**
	 * 
	 * @return
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<BeaconPredicate>>> getAllPredicates() {
		SupplierBuilder<BeaconPredicate> builder = new SupplierBuilder<BeaconPredicate>() {

			@Override
			public ListSupplier<BeaconPredicate> build(KnowledgeBeacon beacon) {
				
				return new ListSupplier<BeaconPredicate>() {

					@Override
					public List<BeaconPredicate> getList() {
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						
						MetadataApi predicateApi =
								new MetadataApi(
									timedApiClient(
											beacon.getName()+".getAllPredicates",
											beaconImpl.getApiClient()
									)
								);
						
						try {
							
							return predicateApi.getPredicates();
							
						} catch (ApiException e) {
							
							logError("getAllPredicates", beaconImpl.getApiClient(), e);
							
							return new ArrayList<BeaconPredicate>();
						}
					}
				};
			}
			
		};
		
		return queryForMap(builder, new ArrayList<Integer>() , "getAllPredicates");
	}
	
	public CompletableFuture<
			Map<
				KnowledgeBeacon, 
				List<BeaconKnowledgeMapStatement>
			>
		> getAllKnowledgeMaps( List<Integer> beacons ) {
		
		SupplierBuilder<BeaconKnowledgeMapStatement> builder = new SupplierBuilder<BeaconKnowledgeMapStatement>() {

			@Override
			public ListSupplier<BeaconKnowledgeMapStatement> build(KnowledgeBeacon beacon) {
				
				return new ListSupplier<BeaconKnowledgeMapStatement>() {

					@Override
					public List<BeaconKnowledgeMapStatement> getList() {
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						
						MetadataApi metadataApi =
								new MetadataApi(
									timedApiClient(
											beacon.getName()+".getAllKnowledgeMaps",
											beaconImpl.getApiClient()
									)
								);
						
						try {
							return metadataApi.getKnowledgeMap();
							
						} catch (ApiException e) {
							logError("getAllKnowledgeMaps", beaconImpl.getApiClient(), e);
							return new ArrayList<>();
						}
					}
				};
			}
			
		};
		
		return queryForMap(builder, new ArrayList<>() , "getAllPredicates");
	}
	
	/**
	 * 
	 * @param clique
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public CompletableFuture<
								Map< 
									KnowledgeBeacon, 
								    List<BeaconConceptWithDetails>
								   >
					       > getConceptDetails(
					    		   		Neo4jConceptClique clique, 
									List<Integer> beacons
					    		 ) 
	{
		
		SupplierBuilder<BeaconConceptWithDetails> builder = 
				new SupplierBuilder<BeaconConceptWithDetails>() { 

			@Override
			public ListSupplier<BeaconConceptWithDetails> build(KnowledgeBeacon beacon) {
				
				return new ListSupplier<BeaconConceptWithDetails>() {

					@Override
					public List<BeaconConceptWithDetails> getList() {
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						
						// Retrieve the beacon specific subclique list of concept identifiers...
						Integer beaconId = beacon.getId();
						
						_logger.debug("getConceptDetails() accessing beacon '"+beaconId+"'");
						
						List<String> conceptIds;
						
						if(clique.hasConceptIds(beaconId)) {
							/*
							 * Safer for now to take all the known concept identifiers here  
							 * TODO: try to figure out why the beacon-specific concept list
							 * - e.g. from Garbanzo - doesn't always retrieve results?
							 */
							conceptIds = clique.getConceptIds();
							_logger.debug("Calling getConceptDetails() with concept details '"+String.join(",",conceptIds)+"'");
							
						} else { //.. don't look any further if the list is empty...
							
							_logger.debug("Returning from getConceptDetails() ... no concept ids available?");
							return new ArrayList<BeaconConceptWithDetails>();
						}
						
						String beaconTag = beacon.getName()+".getConceptDetails";
						ApiClient beaconApi = beaconImpl.getApiClient();
						
						ConceptsApi conceptsApi = 
								new ConceptsApi(
									timedApiClient(
											beaconTag,
											beaconApi,
											CONCEPTS_QUERY_TIMEOUT_WEIGHTING*10,
											beacons
									)
								);
						
						List<BeaconConceptWithDetails> results = new ArrayList<>();
						
						for ( String id : conceptIds ) {
							try {
								
								BeaconConceptWithDetails conceptWithDetails = conceptsApi.getConceptDetails( id );
								if (conceptWithDetails != null) {
									results.add(conceptWithDetails);
								}
								
							} catch (Exception e) {
								logError(beaconTag, beaconApi, e);
							}
						}

						_logger.debug("getConceptDetails() accessing beacon '"+results.size()+
								  "' results found for beacon '"+beaconId+"'");
						return new HashSet<BeaconConceptWithDetails>(results).stream().collect(Collectors.toList());
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, clique.getName());
	}

	/**
	 * 
	 * @param conceptId
	 * @return
	 */
	public CompletableFuture<List<ExactMatchResponse>> getExactMatchesToConcept(String conceptId) {
		SupplierBuilder<ExactMatchResponse> builder = new SupplierBuilder<ExactMatchResponse>() {

			@Override
			public ListSupplier<ExactMatchResponse> build(KnowledgeBeacon beacon) {
				return new ListSupplier<ExactMatchResponse>() {

					@Override
					public List<ExactMatchResponse> getList() {
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
												
						ConceptsApi conceptsApi =
								new ConceptsApi(
										timedApiClient(
												beacon.getName()+".getExactMatchesToConcept",
												beaconImpl.getApiClient(),
												EXACTMATCHES_QUERY_TIMEOUT_WEIGHTING
										)
									);
						try {
							
							List<ExactMatchResponse> exactMatches = conceptsApi.getExactMatchesToConceptList(Arrays.asList(conceptId));
							
							return exactMatches;
								
						} catch (Exception e1) {
							
							logError("Equivalent Concept Clique", beaconImpl.getApiClient(), e1);
							
							return new ArrayList<>();
						}
					}
					
				};
			}
			
		};
		
		return queryForList(builder,"Equivalent Concept Clique");
	}
		
	/**
	 * 
	 * @param conceptIds
	 * @param beacons
	 * @return
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<ExactMatchResponse>>> 
				getExactMatchesToConceptList( List<String> conceptIds, List<Integer> beacons ) {
		
		SupplierBuilder<ExactMatchResponse> builder = new SupplierBuilder<ExactMatchResponse>() {

			@Override
			public ListSupplier<ExactMatchResponse> build(KnowledgeBeacon beacon) {
				
				return new ListSupplier<ExactMatchResponse>() {

					@Override
					public List<ExactMatchResponse> getList() { 
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						
						List<ExactMatchResponse> curieList = new ArrayList<ExactMatchResponse>();

						ConceptsApi exactmatchesApi = 
								new ConceptsApi(
										timedApiClient(
												beacon.getName()+".getExactMatchesToConceptList",
												beaconImpl.getApiClient(),
												EXACTMATCHES_QUERY_TIMEOUT_WEIGHTING,
												beacons
										)
									);
						
						try {
							
							curieList = exactmatchesApi.getExactMatchesToConceptList(conceptIds);
								
						} catch (Exception e1) {
							
							_logger.debug("KBS.getExactMatchesToConceptList() exception from ExactMatchesApi call: "+e1.toString());
							
							logError("Equivalent Concept Clique", beaconImpl.getApiClient(), e1);

							if (isInternalError(e1)) {
								// try asking about CURIEs individually
																
								for (String id : conceptIds) {
									
									try {
										List<ExactMatchResponse> matches = exactmatchesApi.getExactMatchesToConceptList(Arrays.asList(id));
										curieList.addAll(matches);
									
									} catch (Exception e2) {
										
										logError("Equivalent Concept Clique", beaconImpl.getApiClient(), e2);
										
										if (!isInternalError(e2)) {
											// there is some other problem
											break;
										}
									}
								}
							}
						}
						
						return curieList;
					}
					
				};
			}
		};
		
		return queryForMap(builder, beacons, "Equivalent Concept Clique");
	}
	
	/******************************** STATEMENTS Data Access *************************************/
	
	/**
	 * 
	 * @param source
	 * @param relations
	 * @param target
	 * @param keywords
	 * @param categories
	 * @param pageNumber
	 * @param size
	 * @param beacon
	 * @return
	 */
	public List<BeaconStatement> getStatements(
			Neo4jConceptClique sourceClique, 
			String edgeLabel,
			String relation,
			Neo4jConceptClique targetClique, 
			List<String> keywords,
			List<String> categories, 
			Integer size, 
			Integer beacon
	) {
		KnowledgeBeaconImpl beaconImpl = registry.getBeaconById(beacon);
		
		StatementsApi statementsApi = 
				new StatementsApi(
						timedApiClient(
								"Beacon Id: "+beacon.toString()+".getStatements",
								beaconImpl.getApiClient(),
								STATEMENTS_QUERY_TIMEOUT_WEIGHTING*(Math.max(500, size))
						)
					);
		
		List<BeaconStatement> responses;
		
		try {
			responses = statementsApi.getStatements(
					sourceClique.getConceptIds(),
					edgeLabel,
					relation,
					targetClique != null ? targetClique.getConceptIds() : null,
					keywords,
					categories,
					size
			);

		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
		
		return responses;
	}
	
	/**
	 * In our project, Evidences really play this role of evidence.
	 * @param beacons 
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<BeaconStatementWithDetails>>> getEvidence(
			String statementId,
			List<String> keywords,
			int size,
			List<Integer> beacons
	) {
//		List<KnowledgeBeacon> beaconList = registry.filterKnowledgeBeaconsById(beacons);
//		
//		for (KnowledgeBeacon beacon : beaconList) {
//			ApiClient apiClient = new ApiClient(beacon.getId(), beacon.getUrl());
//			
//			StatementsApi statementsApi = new StatementsApi(timedApiClient(
//					beacon.getName()+".getEvidence",
//					apiClient,
//					EVIDENCE_QUERY_TIMEOUT_WEIGHTING,
//					beacons,
//					size
//			));
//			
//			BeaconStatementWithDetails details = statementsApi.getStatementDetails(statementId, keywords, size);
//		}
//		
		SupplierBuilder<BeaconStatementWithDetails> builder = new SupplierBuilder<BeaconStatementWithDetails>() {

			@Override
			public ListSupplier<BeaconStatementWithDetails> build(KnowledgeBeacon beacon) {
				return new ListSupplier<BeaconStatementWithDetails>() {

					@Override
					public List<BeaconStatementWithDetails> getList() {
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						StatementsApi statementsApi = 
								new StatementsApi(
										timedApiClient(
												beacon.getName()+".getEvidence",
												beaconImpl.getApiClient(),
												EVIDENCE_QUERY_TIMEOUT_WEIGHTING,
												beacons,
												size
										)
									);
						try {
							BeaconStatementWithDetails details = statementsApi.getStatementDetails(
									statementId,
									keywords,
									size
							);
							
							ArrayList<BeaconStatementWithDetails> detailsList = new ArrayList<BeaconStatementWithDetails>();
							detailsList.add(details);
							
							return detailsList;
							
						} catch (Exception e) {
							logError(statementId, beaconImpl.getApiClient(), e);
							return new ArrayList<BeaconStatementWithDetails>();
						}
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, statementId);
	}
	
	public CompletableFuture<List<BeaconStatementWithDetails>> getStatementDetails(
			String statementId,
			List<String> keywords,
			Integer size
	) {
		
		SupplierBuilder<BeaconStatementWithDetails> builder = new SupplierBuilder<BeaconStatementWithDetails>() {

			@Override
			public ListSupplier<BeaconStatementWithDetails> build(KnowledgeBeacon beacon) {
				return new ListSupplier<BeaconStatementWithDetails>() {

					@Override
					public List<BeaconStatementWithDetails> getList() {
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						StatementsApi statementsApi = 
								new StatementsApi(
										timedApiClient(
												beacon.getName()+".getEvidence",
												beaconImpl.getApiClient(),
												EVIDENCE_QUERY_TIMEOUT_WEIGHTING,
												size
										)
									);
						try {
							BeaconStatementWithDetails details = statementsApi.getStatementDetails(
									statementId,
									keywords,
									size
							);
							
							ArrayList<BeaconStatementWithDetails> detailsList = new ArrayList<BeaconStatementWithDetails>();
							if (details != null) {
								detailsList.add(details);
							}
							
							return detailsList;
							
						} catch (Exception e) {
							logError(statementId, beaconImpl.getApiClient(), e);
							return new ArrayList<BeaconStatementWithDetails>();
						}
					}
					
				};
			}
			
		};
		return queryForList(builder, statementId);
	}

	/**
	 * 
	 * @return
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<BeaconConceptCategory>>> getConceptTypes() {
		
		SupplierBuilder<BeaconConceptCategory> builder = new SupplierBuilder<BeaconConceptCategory>() {

			@Override
			public ListSupplier<BeaconConceptCategory> build(KnowledgeBeacon beacon) {
				return new ListSupplier<BeaconConceptCategory>() {

					@Override
					public List<BeaconConceptCategory> getList() {
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;

						MetadataApi metadataApi = 
								new MetadataApi(
										timedApiClient(
												beacon.getName()+".ConceptTypes",
												beaconImpl.getApiClient(),
												TYPES_QUERY_TIMEOUT_WEIGHTING
										)
									);
						try {
							List<BeaconConceptCategory> categories =  metadataApi.getConceptCategories();
							return categories;
						} catch (ApiException e) {
							logError("Global", beaconImpl.getApiClient(), e);
							return new ArrayList<BeaconConceptCategory>();
						}
					}
					
				};
			}
		};
		return queryForMap(builder, new ArrayList<Integer>(), "Global");
	}

}