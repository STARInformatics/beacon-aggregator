/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;

import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.ConceptsApi;
import bio.knowledge.client.api.EvidenceApi;
import bio.knowledge.client.api.ExactmatchesApi;
import bio.knowledge.client.api.StatementsApi;
import bio.knowledge.client.api.SummaryApi;
import bio.knowledge.client.impl.ApiClient;
import bio.knowledge.client.model.BeaconEvidence;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconSummary;

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
	public CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconConcept>>> getConcepts(String keywords,
			String semgroups,
			int pageNumber,
			int pageSize,
			List<String> beacons,
			String sessionId
	) {
		final String sg = semgroups;
		
		SupplierBuilder<BeaconConcept> builder = new SupplierBuilder<BeaconConcept>() {

			@Override
			public ListSupplier<BeaconConcept> build(ApiClient apiClient) {
				return new ListSupplier<BeaconConcept>() {

					@Override
					public List<BeaconConcept> getList() {
						ConceptsApi conceptsApi = new ConceptsApi(apiClient);
						
						try {
							List<BeaconConcept> responses = conceptsApi.getConcepts(
									urlEncode(keywords),
									urlEncode(sg),
									pageNumber,
									pageSize
							);
							
							return responses;
							
						} catch (Exception e) {
							logError(sessionId, apiClient, e);
							return new ArrayList<BeaconConcept>();
						}
					}
					
				};
			}
			
		};
		
		return queryForMap(builder, beacons, sessionId);
	}
	
	public CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconConceptWithDetails>>> getConceptDetails(
			List<String> c,
			List<String> beacons,
			String sessionId
	) {
		SupplierBuilder<BeaconConceptWithDetails> builder = new SupplierBuilder<BeaconConceptWithDetails>() {

			@Override
			public ListSupplier<BeaconConceptWithDetails> build(ApiClient apiClient) {
				return new ListSupplier<BeaconConceptWithDetails>() {

					@Override
					public List<BeaconConceptWithDetails> getList() {
						
						ConceptsApi conceptsApi = new ConceptsApi(apiClient);
						List<BeaconConceptWithDetails> responses = new ArrayList<>();
						
						for (String conceptId : c) {
							try {
								
								List<BeaconConceptWithDetails> conceptWithDetails = 
										conceptsApi.getConceptDetails(
										urlEncode(conceptId)
								);
								
								responses.addAll(conceptWithDetails);
								
							} catch (Exception e) {
								logError(sessionId, apiClient, e);
								break;
							}
						}
						
						return responses;
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, sessionId);
	}
	
	public CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconStatement>>> getStatements(
			List<String> c,
			String keywords,
			String semgroups,
			int pageNumber,
			int pageSize,
			List<String> beacons,
			String sessionId
	) {
		SupplierBuilder<BeaconStatement> builder = new SupplierBuilder<BeaconStatement>() {

			@Override
			public ListSupplier<BeaconStatement> build(ApiClient apiClient) {
				return new ListSupplier<BeaconStatement>() {

					@Override
					public List<BeaconStatement> getList() {
						StatementsApi statementsApi = new StatementsApi(apiClient);
						
						try {
							return statementsApi.getStatements(
									c, 
									pageNumber, 
									pageSize, 
									keywords, 
									semgroups
								);
							
						} catch (Exception e1) {
							
							logError(sessionId, apiClient, e1);
							List<BeaconStatement> statementList = new ArrayList<>();

							if (isInternalError(e1)) {
								// try asking about CURIEs individually
																
								for (String conceptId : c) {
									
									try {
										List<BeaconStatement> matches = 
												statementsApi.getStatements(
														list(conceptId), 
														pageNumber, 
														pageSize, 
														keywords, 
														semgroups
													);
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
	 * In our project, Evidences really play this role of evidence.
	 * @param beacons 
	 */
	public CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconEvidence>>> getEvidences(
			String statementId,
			String keywords,
			int pageNumber,
			int pageSize,
			List<String> beacons,
			String sessionId
	) {
		SupplierBuilder<BeaconEvidence> builder = new SupplierBuilder<BeaconEvidence>() {

			@Override
			public ListSupplier<BeaconEvidence> build(ApiClient apiClient) {
				return new ListSupplier<BeaconEvidence>() {

					@Override
					public List<BeaconEvidence> getList() {
						EvidenceApi evidenceApi = new EvidenceApi(apiClient);
						
						try {
							List<BeaconEvidence> responses = 
									evidenceApi.getEvidence(
										urlEncode(statementId),
										urlEncode(keywords),
										pageNumber,
										pageSize
								);
							
							return responses;
							
						} catch (Exception e) {
							logError(sessionId, apiClient, e);
							return new ArrayList<BeaconEvidence>();
						}
					}
					
				};
			}
			
		};
		return queryForMap(builder, beacons, sessionId);
	}

	public CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconSummary>>> linkedTypes(List<String> beacons, String sessionId) {
		SupplierBuilder<BeaconSummary> builder = new SupplierBuilder<BeaconSummary>() {

			@Override
			public ListSupplier<BeaconSummary> build(ApiClient apiClient) {
				return new ListSupplier<BeaconSummary>() {

					@Override
					public List<BeaconSummary> getList() {
						SummaryApi summaryApi = new SummaryApi(apiClient);
						
						try {
							return summaryApi.linkedTypes();
						} catch (ApiException e) {
							logError(sessionId, apiClient, e);
							return new ArrayList<BeaconSummary>();
						}
					}
					
				};
			}
			
		};
		
		return queryForMap(builder, beacons, sessionId);
	}
	
	public CompletableFuture<List<String>> getExactMatchesToConcept(String conceptId) {
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
							logError("Equivalent Concept Clique", apiClient, e1);
							return new ArrayList<>();
						}
					}
					
				};
			}
			
		};
		return queryForList(builder,"Equivalent Concept Clique");
	}
		
	public CompletableFuture<List<String>> getExactMatchesToConceptList( List<String> c ) {
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
							
							logError("Equivalent Concept Clique", apiClient, e1);
							
							List<String> curieList = new ArrayList<>();

							if (isInternalError(e1)) {
								// try asking about CURIEs individually
																
								for (String conceptId : c) {
									
									try {
										List<String> matches = exactmatchesApi.getExactMatchesToConcept(conceptId);
										curieList.addAll(matches);
									
									} catch (Exception e2) {
										
										logError("Equivalent Concept Clique", apiClient, e2);
										
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
		return queryForList(builder,"Equivalent Concept Clique");
	}
	
}