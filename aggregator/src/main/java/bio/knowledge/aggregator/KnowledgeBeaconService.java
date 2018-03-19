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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.OkHttpClient;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.Util;
import bio.knowledge.aggregator.ecc.ExactMatchesHandler_ecc;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.ConceptsApi;
import bio.knowledge.client.api.MetadataApi;
import bio.knowledge.client.api.StatementsApi;
import bio.knowledge.client.impl.ApiClient;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptType;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconKnowledgeMapStatement;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;

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
 *  @author Meera Godden
 *  
 *  		Most queries return a map associating beacons to their results.
 *  		Queries that use exactmatches try to handle internal errors in beacons
 *  		by trying to give the beacon fewer exactmatches at a time.
 *
 */
@Service
public class KnowledgeBeaconService implements Util, SystemTimeOut {

	private static Logger _logger = LoggerFactory.getLogger(KnowledgeBeaconService.class);

	// This works because {@code GenericKnowledgeService} is extended by {@code
	// KnowledgeBeaconService}, which is a Spring service.
	@Autowired KnowledgeBeaconRegistry registry;

	@Override
	public int countAllBeacons() {
		return registry.countAllBeacons();
	}
	
	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private ExactMatchesHandler_ecc exactMatchesHandler;
	
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
	public List<LogEntry> getErrors(String queryId) {
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
						_logger.debug(c.toString());
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
	
	/*
	 * Periods sometimes drop out of queries if they are not URL encoded. This
	 * is <b>not</b> a complete URL encoding. I have only encoded those few
	 * characters that might be problematic. We may have to revisit this in
	 * the future, and implement a proper encoder.
	 */
	private String urlEncode(String string) {
//		if (string != null) {
//			return string.replace(".", "%2E").replace(" ", "%20").replace(":", "%3A");
//		} else {
//			return null;
//		}
		return string;
	}
	
	private <T> List<T> list(T item) {
		List<T> list = new ArrayList<>();
		list.add(item);
		return list;
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

		if(message!=null) _logger.error(message);
		
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
	public static final int CONCEPTS_QUERY_TIMEOUT_WEIGHTING     = 10000;
	public static final int EXACTMATCHES_QUERY_TIMEOUT_WEIGHTING = 40000; 
	public static final int STATEMENTS_QUERY_TIMEOUT_WEIGHTING   = 60000; 
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

		_logger.debug(apiName+": ApiClient connection timeout is currently set to  '"+new Integer(apiClient.getConnectTimeout())+"' milliseconds");

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

		_logger.debug(apiName+": HTTP client socket read timeout is currently set to '"+new Long(httpClient.getReadTimeout())+"' milliseconds");
		
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

	/******************************************* Data Accessors *********************************************/

	/**
	 * Gets a list of concepts satisfying a query with the given parameters.
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons 
	 * @return a {@code CompletableFuture} of all the concepts from all the
	 *         knowledge sources in the {@code KnowledgeBeaconRegistry} that
	 *         satisfy a query with the given parameters.
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<BeaconItemWrapper<BeaconConcept>>>> getConcepts(
			String keywords,
			String conceptTypes,
			int pageNumber,
			int pageSize,
			List<Integer> beacons,
			String queryId
	) {
		final String sg = conceptTypes;
		
		SupplierBuilder<BeaconItemWrapper<BeaconConcept>> builder = new SupplierBuilder<BeaconItemWrapper<BeaconConcept>>() {

			@Override
			public ListSupplier<BeaconItemWrapper<BeaconConcept>> build(KnowledgeBeacon beacon) {
				return new ListSupplier<BeaconItemWrapper<BeaconConcept>>() {

					@Override
					public List<BeaconItemWrapper<BeaconConcept>> getList() {
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						
						Integer beaconId = beacon.getId();
						
						_logger.debug("kbs.getConcepts(): accessing beacon '"+beaconId+"'");
						
						ConceptsApi conceptsApi = 
								new ConceptsApi(
										timedApiClient(
												beacon.getName()+".getConcepts",
												beaconImpl.getApiClient(),
												CONCEPTS_QUERY_TIMEOUT_WEIGHTING,
												beacons,
												pageSize
										)
									);

						try {
							Timer.setTime("concept: " + beaconId);
							
							List<BeaconConcept> responses = 
									conceptsApi.getConcepts(
											urlEncode(keywords),
											urlEncode(sg),
											pageNumber,
											pageSize
									);
							
							Timer.printTime("concept: " + beaconId);
							
							@SuppressWarnings("unchecked")
							CompletableFuture<BeaconItemWrapper<BeaconConcept>>[] futures = new CompletableFuture[responses.size()];
							
							int i = 0;
							
							for (BeaconConcept concept : responses) {
								
								futures[i++] = CompletableFuture.supplyAsync(
										() -> {
											String conceptType = concept.getType();
											
											List<ConceptTypeEntry> types = 
													conceptTypeService.lookUpByIdentifier(conceptType);
											
											
																			
											ConceptClique ecc = 
													exactMatchesHandler.getExactMatches(
																beacon,
																concept.getId(),
																concept.getName(),
																types
															);
											
											BeaconConceptWrapper beaconConceptWrapper = new BeaconConceptWrapper();
											beaconConceptWrapper.setItem(concept);
											beaconConceptWrapper.setClique(ecc.getId());

											return beaconConceptWrapper;
										}
								);
								
							}
							
							CompletableFuture<List<BeaconItemWrapper<BeaconConcept>>> future = CompletableFuture.allOf(futures).thenApply(v -> {
								return combineFutureResults(futures);
							}).exceptionally(error -> {
								return combineFutureResults(futures);
							});
							
							Timer.setTime("ecc: " + beaconId);
							
							List<BeaconItemWrapper<BeaconConcept>> concepts = future.get(
									KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,
									KnowledgeBeaconService.BEACON_TIMEOUT_UNIT
							);
							
							Timer.printTime("ecc: " + beaconId);
							
							_logger.debug("kbs.getConcepts(): '"+concepts.size()+"' results found for beacon '"+beaconId+"'");
							
							return concepts;
							
						} catch (Exception e) {
							
							_logger.error("kbs.getConcepts() ERROR: accessing beacon '"+beaconId+"', Exception thrown: "+e.getMessage());
							
							logError(queryId, beaconImpl.getApiClient(), e);
							
							return new ArrayList<BeaconItemWrapper<BeaconConcept>>();
						}
					}
					
				};
			}
			
		};
		
		return queryForMap(builder, beacons, queryId);
	}
	
	private List<BeaconItemWrapper<BeaconConcept>> combineFutureResults(CompletableFuture<BeaconItemWrapper<BeaconConcept>>[] futures) {
		List<BeaconItemWrapper<BeaconConcept>> concepts = new ArrayList<BeaconItemWrapper<BeaconConcept>>();
		
		for (CompletableFuture<BeaconItemWrapper<BeaconConcept>> f : futures) {
			if (!f.isCompletedExceptionally()) {
				BeaconItemWrapper<BeaconConcept> concept = f.join();
				if (concept != null) {
					concepts.add(concept);
				}
			}
		}
		
		return concepts;
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
						
						/*
						 *  TODO: the Garbanzo Beacon is non-selective in returning WikiData predicates
						 *  therefore, we ignore it here (Hack!)
						 */
						if(beaconImpl.getId()==2)
							return new ArrayList<BeaconPredicate>();
						
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
							return new ArrayList<BeaconKnowledgeMapStatement>();
						}
					}
				};
			}
			
		};
		
		return queryForMap(builder, new ArrayList<Integer>() , "getAllPredicates");
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
					    		   		ConceptClique clique, 
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
											CONCEPTS_QUERY_TIMEOUT_WEIGHTING,
											beacons
									)
								);
						
						List<BeaconConceptWithDetails> results = new ArrayList<>();
						
						for ( String id : conceptIds ) {
							try {
								
								id = urlEncode(id);
								
								List<BeaconConceptWithDetails> conceptWithDetails = 
																	conceptsApi.getConceptDetails( id );
								
								results.addAll(conceptWithDetails);
								
							} catch (Exception e) {
								
								logError(beaconTag, beaconApi, e);
								break;
							}
						}
						_logger.debug("getConceptDetails() accessing beacon '"+results.size()+
								  "' results found for beacon '"+beaconId+"'");
						return results;
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
	public CompletableFuture<List<String>> getExactMatchesToConcept(String conceptId) {
		SupplierBuilder<String> builder = new SupplierBuilder<String>() {

			@Override
			public ListSupplier<String> build(KnowledgeBeacon beacon) {
				return new ListSupplier<String>() {

					@Override
					public List<String> getList() {
						
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
							
							List<String> exactMatches = conceptsApi.getExactMatchesToConcept(conceptId);
							
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
	public CompletableFuture<Map<KnowledgeBeacon, List<String>>> 
				getExactMatchesToConceptList( List<String> conceptIds, List<Integer> beacons ) {
		
		SupplierBuilder<String> builder = new SupplierBuilder<String>() {

			@Override
			public ListSupplier<String> build(KnowledgeBeacon beacon) {
				
				return new ListSupplier<String>() {

					@Override
					public List<String> getList() { 
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						
						List<String> curieList = new ArrayList<>();

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
										List<String> matches = exactmatchesApi.getExactMatchesToConcept(id);
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

	/**
	 * 
	 * @param sourceClique
	 * @param relations
	 * @param targetClique
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public CompletableFuture<
								Map<
									KnowledgeBeacon, 
									List<BeaconStatement>
									>
							> getStatements(
									
									ConceptClique sourceClique,
									String relations, 
									ConceptClique targetClique,
									String keywords,
									String conceptTypes,
									int pageNumber,
									int pageSize,
									List<Integer> beacons,
									String queryId
									
								) {
		
		SupplierBuilder<BeaconStatement> builder = 
				new SupplierBuilder<BeaconStatement>() {

			@Override
			public ListSupplier<BeaconStatement> build(KnowledgeBeacon beacon) {
				
				return new ListSupplier<BeaconStatement>() {

					@Override
					public List<BeaconStatement> getList() {
						
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						
						List<BeaconStatement> statementList = new ArrayList<>();
						
						// Retrieve the beacon specific subclique list of concept identifiers...
						Integer beaconId = beacon.getId();
						
						_logger.debug("getStatements() accessing beacon '"+beaconId+"'");
						
						List<String> sourceConceptIds ;
						
						if(sourceClique.hasConceptIds(beaconId)) {
							/*
							 * Safer for now to take all the known concept identifiers here  
							 * TODO: try to figure out why the beacon-specific concept list - e.g. from Garbanzo - doesn't always retrieve results? Should perhaps only send beacon-specific list in the future?
							 */
							sourceConceptIds = sourceClique.getConceptIds(beaconId);
							
							_logger.debug("Calling getStatements() with source concept identifiers '"+String.join(",",sourceConceptIds)+"'");
							
						} else { //.. don't look any further if the list is empty...
							_logger.debug("Returning from getStatements() ... no concept ids available?");
							return new ArrayList<BeaconStatement>();
						}
						
						List<String> targetConceptIds = null ;
						
						if(targetClique != null && targetClique.hasConceptIds(beaconId)) {
							/*
							 * Safer for now to take all the known concept identifiers here  
							 * TODO: try to figure out why the beacon-specific concept list - e.g. from Garbanzo - doesn't always retrieve results? Should perhaps only send beacon-specific list in the future?
							 */
							targetConceptIds = targetClique.getConceptIds();
							
							_logger.debug("Calling getStatements() with target concept identifiers '"+String.join(",",targetConceptIds)+"'");
							
						} else {
							_logger.debug("Calling getStatements() without any target concept identifiers?");
						}
						
						String beaconTag = beacon.getName()+".getConceptDetails";
						ApiClient beaconApi = beaconImpl.getApiClient();
						
						StatementsApi statementsApi = 
								new StatementsApi(
										timedApiClient(
												beaconTag,
												beaconApi,
												STATEMENTS_QUERY_TIMEOUT_WEIGHTING,
												beacons,
												pageSize
										)
									);
						try {
							statementList = 
									statementsApi.getStatements(
														sourceConceptIds, 
														relations,
														targetConceptIds,
														keywords, 
														conceptTypes,
														pageNumber, 
														pageSize
													);
							_logger.debug("getStatements() '"+statementList.size()+"' results found for beacon '"+beaconId+"'");
								
						} catch (Exception e1) {
							
							logError(queryId, beaconApi, e1);

							if (isInternalError(e1)) {
								
								// try asking about CURIEs individually
																
								for (String sourceConceptId : sourceClique.getConceptIds(beaconId)) {
									
									try {
										
										statementList = 
												statementsApi.getStatements(
														list(sourceConceptId), 
														relations, 
														targetConceptIds, 
														keywords, 
														conceptTypes,
														pageNumber, 
														pageSize
													);
									
									} catch (Exception e2) {
										
										logError(queryId, beaconApi, e2);
										
										if (!isInternalError(e2)) {
											// there is some other problem
											break;
										}
									}
								}
							}
							
							_logger.debug("getStatements() accessing beacon '"+statementList.size()+
									  "' results found for beacon '"+beaconId+"'");
						}
						
						return statementList;
					}
				};
			}
		};
		return queryForMap(builder, beacons, queryId);
	}
	
	/**
	 * In our project, Evidences really play this role of evidence.
	 * @param beacons 
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<BeaconAnnotation>>> getEvidence(
			String statementId,
			String keywords,
			int pageNumber,
			int pageSize,
			List<Integer> beacons
	) {
		SupplierBuilder<BeaconAnnotation> builder = new SupplierBuilder<BeaconAnnotation>() {

			@Override
			public ListSupplier<BeaconAnnotation> build(KnowledgeBeacon beacon) {
				return new ListSupplier<BeaconAnnotation>() {

					@Override
					public List<BeaconAnnotation> getList() {
						KnowledgeBeaconImpl beaconImpl = (KnowledgeBeaconImpl)beacon;
						StatementsApi statementsApi = 
								new StatementsApi(
										timedApiClient(
												beacon.getName()+".getEvidence",
												beaconImpl.getApiClient(),
												EVIDENCE_QUERY_TIMEOUT_WEIGHTING,
												beacons,
												pageSize
										)
									);
						try {
							List<BeaconAnnotation> evidence = 
									statementsApi.getEvidence(
										urlEncode(statementId),
										urlEncode(keywords),
										pageNumber,
										pageSize
								);
							
							return evidence;
							
						} catch (Exception e) {
							logError(statementId, beaconImpl.getApiClient(), e);
							return new ArrayList<BeaconAnnotation>();
						}
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, statementId);
	}

	/**
	 * 
	 * @return
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<BeaconConceptType>>> getConceptTypes() {
		
		SupplierBuilder<BeaconConceptType> builder = new SupplierBuilder<BeaconConceptType>() {

			@Override
			public ListSupplier<BeaconConceptType> build(KnowledgeBeacon beacon) {
				return new ListSupplier<BeaconConceptType>() {

					@Override
					public List<BeaconConceptType> getList() {
						
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
							List<BeaconConceptType> types =  metadataApi.getConceptTypes();
							return types;
						} catch (ApiException e) {
							logError("Global", beaconImpl.getApiClient(), e);
							return new ArrayList<BeaconConceptType>();
						}
					}
					
				};
			}
		};
		return queryForMap(builder, new ArrayList<Integer>(), "Global");
	}
}