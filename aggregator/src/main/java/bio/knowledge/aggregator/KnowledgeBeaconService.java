package bio.knowledge.aggregator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;

import bio.knowledge.client.impl.ApiClient;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.ConceptsApi;
import bio.knowledge.client.api.EvidenceApi;
import bio.knowledge.client.api.ExactmatchesApi;
import bio.knowledge.client.api.StatementsApi;
import bio.knowledge.client.api.SummaryApi;
import bio.knowledge.client.model.InlineResponse200;
import bio.knowledge.client.model.InlineResponse2001;
import bio.knowledge.client.model.InlineResponse2002;
import bio.knowledge.client.model.InlineResponse2003;
import bio.knowledge.client.model.InlineResponse2004;

/**
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
public class KnowledgeBeaconService extends GenericKnowledgeService {
		
	/**
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
	
	private void logError(String sessionId, ApiClient apiClient, Exception e) {
		
		String message = e.getMessage();
		if (e instanceof JsonSyntaxException) {
		        message += " PROBLEM WITH DESERIALIZING SERVER RESPONSE";
		}

		System.err.println(message);
		logError(sessionId, apiClient.getBeaconId(), apiClient.getQuery(), message);
	}
	
	/**
	 * Gets a list of concepts satisfying a query with the given parameters.
	 * @param keywords
	 * @param semgroups
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons 
	 * @return a {@code CompletableFuture} of all the concepts from all the
	 *         knowledge sources in the {@code KnowledgeBeaconRegistry} that
	 *         satisfy a query with the given parameters.
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<InlineResponse2002>>> getConcepts(String keywords,
			String semgroups,
			int pageNumber,
			int pageSize,
			List<String> beacons,
			String sessionId
	) {
		final String sg = semgroups;
		
		SupplierBuilder<InlineResponse2002> builder = new SupplierBuilder<InlineResponse2002>() {

			@Override
			public ListSupplier<InlineResponse2002> build(ApiClient apiClient) {
				return new ListSupplier<InlineResponse2002>() {

					@Override
					public List<InlineResponse2002> getList() {
						ConceptsApi conceptsApi = new ConceptsApi(apiClient);
						
						try {
							List<InlineResponse2002> responses = conceptsApi.getConcepts(
									urlEncode(keywords),
									urlEncode(sg),
									pageNumber,
									pageSize
							);
							
							return responses;
							
						} catch (Exception e) {
							logError(sessionId, apiClient, e);
							return new ArrayList<InlineResponse2002>();
						}
					}
					
				};
			}
			
		};
		
		return queryForMap(builder, beacons, sessionId);
	}
	
	public CompletableFuture<Map<KnowledgeBeacon, List<InlineResponse2001>>> getConceptDetails(
			List<String> c,
			List<String> beacons,
			String sessionId
	) {
		SupplierBuilder<InlineResponse2001> builder = new SupplierBuilder<InlineResponse2001>() {

			@Override
			public ListSupplier<InlineResponse2001> build(ApiClient apiClient) {
				return new ListSupplier<InlineResponse2001>() {

					@Override
					public List<InlineResponse2001> getList() {
						
						ConceptsApi conceptsApi = new ConceptsApi(apiClient);
						List<InlineResponse2001> responses = new ArrayList<>();
						
						for (String conceptId : c) {
							try {
								
								List<InlineResponse2001> concept = conceptsApi.getConceptDetails(
										urlEncode(conceptId)
								);
								
								responses.addAll(concept);
								
							} catch (Exception e) {
								logError(sessionId, apiClient, e);
								if (! isInternalError(e)) break;
							}
						}
						
						return responses;
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, sessionId);
	}
	
	public CompletableFuture<Map<KnowledgeBeacon, List<InlineResponse2003>>> getStatements(
			List<String> c,
			String keywords,
			String semgroups,
			int pageNumber,
			int pageSize,
			List<String> beacons,
			String sessionId
	) {
		SupplierBuilder<InlineResponse2003> builder = new SupplierBuilder<InlineResponse2003>() {

			@Override
			public ListSupplier<InlineResponse2003> build(ApiClient apiClient) {
				return new ListSupplier<InlineResponse2003>() {

					@Override
					public List<InlineResponse2003> getList() {
						StatementsApi statementsApi = new StatementsApi(apiClient);
						
						try {
							return statementsApi.getStatements(c, pageNumber, pageSize, keywords, semgroups);
							
						} catch (Exception e1) {
							
							logError(sessionId, apiClient, e1);
							List<InlineResponse2003> statementList = new ArrayList<>();

							if (isInternalError(e1)) {
								// try asking about CURIEs individually
																
								for (String conceptId : c) {
									
									try {
										List<InlineResponse2003> matches = statementsApi.getStatements(list(conceptId), pageNumber, pageSize, keywords, semgroups);
										statementList.addAll(matches);
									
									} catch (Exception e2) {
										
										logError(sessionId, apiClient, e2);
										
										if (!isInternalError(e2)) {
											// there is some other problem
											break;
										}
									}
								}
								
							}
							
							return statementList;
						}
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, sessionId);
	}
	
	/**
	 * In our project, annotations really play this role of evidence.
	 * @param beacons 
	 */
	public CompletableFuture<Map<KnowledgeBeacon, List<InlineResponse2004>>> getEvidences(
			String statementId,
			String keywords,
			int pageNumber,
			int pageSize,
			List<String> beacons,
			String sessionId
	) {
		SupplierBuilder<InlineResponse2004> builder = new SupplierBuilder<InlineResponse2004>() {

			@Override
			public ListSupplier<InlineResponse2004> build(ApiClient apiClient) {
				return new ListSupplier<InlineResponse2004>() {

					@Override
					public List<InlineResponse2004> getList() {
						EvidenceApi evidenceApi = new EvidenceApi(apiClient);
						
						try {
							List<InlineResponse2004> responses = evidenceApi.getEvidence(
									urlEncode(statementId),
									urlEncode(keywords),
									pageNumber,
									pageSize
							);
							
							return responses;
							
						} catch (Exception e) {
							logError(sessionId, apiClient, e);
							return new ArrayList<InlineResponse2004>();
						}
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, sessionId);
	}

	public CompletableFuture<Map<KnowledgeBeacon, List<InlineResponse200>>> linkedTypes(List<String> beacons, String sessionId) {
		SupplierBuilder<InlineResponse200> builder = new SupplierBuilder<InlineResponse200>() {

			@Override
			public ListSupplier<InlineResponse200> build(ApiClient apiClient) {
				return new ListSupplier<InlineResponse200>() {

					@Override
					public List<InlineResponse200> getList() {
						SummaryApi summaryApi = new SummaryApi(apiClient);
						
						try {
							return summaryApi.linkedTypes();
						} catch (ApiException e) {
							logError(sessionId, apiClient, e);
							return new ArrayList<InlineResponse200>();
						}
					}
					
				};
			}
			
		};
		
		return queryForMap(builder, beacons, sessionId);
	}
	
	public CompletableFuture<List<String>> getExactMatchesToConcept(String conceptId, String sessionId) {
		SupplierBuilder<String> builder = new SupplierBuilder<String>() {

			@Override
			public ListSupplier<String> build(ApiClient apiClient) {
				return new ListSupplier<String>() {

					@Override
					public List<String> getList() {
						
						ExactmatchesApi exactmatchesApi = new ExactmatchesApi(apiClient);
												
						try {
							return exactmatchesApi.getExactMatchesToConcept(conceptId);
								
						} catch (Exception e1) {
							logError(sessionId, apiClient, e1);
							return new ArrayList<>();
						}
					}
					
				};
			}
			
		};
		return queryForList(builder, sessionId);
	}
		
	public CompletableFuture<List<String>> getExactMatchesToConceptList(List<String> c, String sessionId) {
		SupplierBuilder<String> builder = new SupplierBuilder<String>() {

			@Override
			public ListSupplier<String> build(ApiClient apiClient) {
				return new ListSupplier<String>() {

					@Override
					public List<String> getList() {
						
						ExactmatchesApi exactmatchesApi = new ExactmatchesApi(apiClient);
												
						try {
							return exactmatchesApi.getExactMatchesToConceptList(c);
								
						} catch (Exception e1) {
							
							logError(sessionId, apiClient, e1);
							List<String> curieList = new ArrayList<>();

							if (isInternalError(e1)) {
								// try asking about CURIEs individually
																
								for (String conceptId : c) {
									
									try {
										List<String> matches = exactmatchesApi.getExactMatchesToConcept(conceptId);
										curieList.addAll(matches);
									
									} catch (Exception e2) {
										
										logError(sessionId, apiClient, e2);
										
										if (!isInternalError(e2)) {
											// there is some other problem
											break;
										}
									}
								}
								
							}
							
							return curieList;
						}
					}
					
				};
			}
			
		};
		return queryForList(builder, sessionId);
	}
	
}