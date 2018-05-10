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
package bio.knowledge.server.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bio.knowledge.Util;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.server.blackboard.Blackboard;
import bio.knowledge.server.blackboard.BlackboardException;
import bio.knowledge.server.blackboard.MetadataService;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import bio.knowledge.server.model.ServerConceptTypes;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerKnowledgeMap;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerPredicates;
import bio.knowledge.server.model.ServerStatementsQuery;
import bio.knowledge.server.model.ServerStatementsQueryResult;
import bio.knowledge.server.model.ServerStatementsQueryStatus;

/**
 * This is the KBA Controller class containing the delegated handlers for the various API endpoints.
 * 
 * The main role of these handlers are:
 * 
 * 1) To coerce input parameters into acceptable values (including empty values)
 * 
 * 2) To call the back end metadata and blackboard services to return business model query results
 * 
 * 3) To reformat the business model data outputs (which is sometimes indexed by beacon index identifier) 
 *    into the required KBA "Server*" data transfer object output formats.
 * 
 * This class does NOT directly manage the back end (cache) blackboard nor directly call the beacons. 
 * Such activities are delegated to the 'blackboard' layer itself.
 * 
 * @author Richard Bruskiewich
 * @author Lance Hannestad
 * @author Meera Godden
 *
 */
@Service
public class ControllerImpl implements Util {

	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);

	@Autowired private Blackboard blackboard;
	@Autowired private MetadataService metadataService;
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	
	/*
	 * @param i
	 * @return 1 if i is null
	 */
	private Integer fixInteger(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	/*
	 * RMB: Feb 23, 2018 - seems like a NOP method and that an empty string would be "safer" 
	 * but I don't if this innocent change will introduce bugs into the system 
	 * (i.e. that null is expected and used in some parts of the code.
	 * 
	 * @param str
	 * @return "" if str is null
	 */
	private String fixString(String str) {
		//return str != null ? str : null;
		return str != null ? str : "";
	}
	
	/*
	 * 
	 * @param l
	 * @return new empty ArrayList if l is null
	 */
	private List<String> fixStringList(List<String> l) {
		if (l == null) l = new ArrayList<String>();
		return l;
	}
	
	/*
	 * 
	 * @param l
	 * @return new empty ArrayList if l is null
	 */
	private List<Integer> fixIntegerList(List<Integer> l) {
		if (l == null) l = new ArrayList<Integer>();
		return l;
	}
	
	/*
	 * @param request
	 * @return url used to make the request
	 */
	private String getUrl(HttpServletRequest request) {
		String query = request.getQueryString();
		query = (query == null)? "" : "?" + query;
		return request.getRequestURL() + query;
	}
	
	/*
	 * 
	 * @param queryId
	 * @param e
	 */
	private void logError(String queryId, Exception e) {
		
		if(queryId.isEmpty()) queryId = "Global";
				
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		String message = e.getMessage();
		if(message!=null) _logger.error(queryId+": "+message);
		
		metadataService.logError(queryId, null, getUrl(request), e.getMessage());
	}
	
/******************************** METADATA Endpoints *************************************/

	/**
	 *
	 * @return HTTP ResponseEntity of a List of ServerKnowledgeBeacon entries
	 */
	public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons() {
		try {
			List<ServerKnowledgeBeacon> beacons = metadataService.getKnowledgeBeacons();
			return ResponseEntity.ok(beacons);
			
		} catch(BlackboardException bbe) {
			logError("Global",bbe);
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * 
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity< List<ServerConceptTypes>> getConceptTypes(List<Integer> beacons) {
			
		beacons = fixIntegerList(beacons);
		
		try {
			List<ServerConceptTypes> responses = new ArrayList<ServerConceptTypes>();
			responses.addAll( metadataService.getConceptTypes( beacons ) );
			
			return ResponseEntity.ok(responses);	
			
		} catch (BlackboardException bbe) {
			logError("Global", bbe);
			return ResponseEntity.badRequest().build();
		}
    }
	
	/**
	 * 
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<List<ServerPredicates>> getPredicates(List<Integer> beacons) {
		
		beacons = fixIntegerList(beacons);
		
		try {
			List<ServerPredicates> responses = new ArrayList<ServerPredicates>();
			responses.addAll( metadataService.getPredicates( beacons ) );
			
			return ResponseEntity.ok(responses);
			
		} catch (BlackboardException bbe) {
			logError("global", bbe);
			return ResponseEntity.badRequest().build();
		}
		
	}

	/**
	 * 
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<List<ServerKnowledgeMap>> getKnowledgeMap(List<Integer> beacons) {

		beacons = fixIntegerList(beacons);

		try {
			List<ServerKnowledgeMap> responses = 
					metadataService.getKnowledgeMap(beacons);
			
			return ResponseEntity.ok(responses);
			
		} catch (BlackboardException e) {
			logError("Global", e);
			return ResponseEntity.badRequest().build();
		}
	}
	
	/**
	 * 
	 * @param queryId
	 * @return HTTP ResponseEntity of a List of ServerLogEntry entries associated with the specified queryId
	 */
	public ResponseEntity<List<ServerLogEntry>> getErrors(String queryId) {
		
		queryId = fixString(queryId);
		
		try {
			if(!queryId.isEmpty()) {
				
				List<ServerLogEntry> responses = 
						metadataService.getErrors(queryId);
				
				return ResponseEntity.ok(responses);
				
			} else {
				throw new RuntimeException("Mandatory Session ID parameter was not provided?");
			}
			
		} catch (Exception e) {
			logError(queryId, e);
			return ResponseEntity.badRequest().build();
		}
	}

/******************************** CONCEPT Endpoints *************************************/
	
	/**
	 * 
	 * @param keywords
	 * @param conceptTypes
	 * @param beacons
	 * @return
	 */
	public ResponseEntity<ServerConceptsQuery> 
								postConceptsQuery(
										String keywords, 
										List<String> conceptTypes, 
										List<Integer> beacons
	) {
		keywords     = fixString(keywords);
		conceptTypes = fixStringList(conceptTypes);
		beacons      = fixIntegerList(beacons);
		
		// Initiate asynchronous query here!
		try {
			
			ServerConceptsQuery query = 
					blackboard.initiateConceptsQuery(
								keywords, 
								conceptTypes,
								beacons
							) ;
			
			return ResponseEntity.ok(query);
			
		} catch (BlackboardException bbe) {
			logError("postConceptsQuery", bbe);
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * 
	 * @param queryId
	 * @param beacons
	 * @return
	 */
	public ResponseEntity<ServerConceptsQueryStatus>
					getConceptsQueryStatus(
								String queryId, 
								List<Integer> beacons
	) {
		if( blackboard.isActiveQuery(queryId) ) {
			
			beacons = fixIntegerList(beacons);
			
			try {

				ServerConceptsQueryStatus queryStatus = 
						blackboard.getConceptsQueryStatus(
									queryId,
									beacons
								) ;
				
				return ResponseEntity.ok(queryStatus);
				
			} catch (BlackboardException bbe) {
				logError("getConceptsQueryStatus", bbe);
				return ResponseEntity.badRequest().build();
			}

			
		} else
			return ResponseEntity.notFound().build();
	}
	
	/**
	 * 
	 * @param queryId
	 * @param beacons
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public ResponseEntity<ServerConceptsQueryResult> 
					getConcepts( 
							String queryId, 
							List<Integer> beacons,
							Integer pageNumber, 
							Integer pageSize
	) {
		if( blackboard.isActiveQuery(queryId) ) {
			
			pageNumber = fixInteger(pageNumber);		
			pageSize   = fixInteger(pageSize);
			beacons    = fixIntegerList(beacons);
			
			try {	
				// retrieve the data, assuming it is available
				ServerConceptsQueryResult result = 
						blackboard.retrieveConceptsQueryResults(
										queryId,
										pageNumber, 
										pageSize,
										beacons
									) ;
				
				return ResponseEntity.ok(result);
				
			} catch (BlackboardException bbe) {
				logError(queryId, bbe);
				return ResponseEntity.badRequest().build();
			}
			
		} else
			return ResponseEntity.notFound().build();
	}

	/**
	 * 
	 * @param identifier
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<ServerCliqueIdentifier> getClique(String identifier) {
		
		ServerCliqueIdentifier cliqueId = null;
		
		try {
			cliqueId = blackboard.getClique(identifier);
			
			if (cliqueId == null) {
				Optional<ConceptClique> optional = exactMatchesHandler.createConceptClique(identifier);
				
				if (optional.isPresent()) {
					ConceptClique clique = optional.get();
					
					cliqueId = new ServerCliqueIdentifier();
					
					cliqueId.setCliqueId(clique.getId());
					
				} else {
					throw new RuntimeException("Could not build concept clique");
				}
			}
			
			return ResponseEntity.ok(cliqueId);
			
		} catch (BlackboardException bbe) {
			logError("Global", bbe);
			return ResponseEntity.badRequest().build();
		}
		
	}

	/**
	 * 
	 * @param cliqueId
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<ServerConceptWithDetails> getConceptDetails(
			String cliqueId, 
			List<Integer>  beacons
	) {
		
		cliqueId  = fixString(cliqueId);
		beacons   = fixIntegerList(beacons);
		
		ServerConceptWithDetails conceptDetails = null;
		
		try {
			conceptDetails = 
					blackboard.getConceptDetails(
							cliqueId, 
							beacons
					);
			return ResponseEntity.ok(conceptDetails);
			
		} catch (BlackboardException bbe) {
			logError("Global", bbe);
			return ResponseEntity.badRequest().build();
		}
	}

	
/******************************** STATEMENT Endpoints *************************************/

	/**
	 * 
	 * @param source
	 * @param relations
	 * @param target
	 * @param keywords
	 * @param types
	 * @return
	 */
	public ResponseEntity<ServerStatementsQuery> 
					postStatementsQuery(
						String source, List<String> relations, String target,
						String keywords, List<String> conceptTypes, 
						List<Integer> beacons
	) {
		source       = fixString(source);
		relations    = fixStringList(relations);
		target       = fixString(target);
		keywords     = fixString(keywords);
		conceptTypes = fixStringList(conceptTypes);
		beacons      = fixIntegerList(beacons);
		
		// Initiate asynchronous query here!
		try {			
			ServerStatementsQuery query = blackboard.initiateStatementsQuery(
					source,
					relations,
					target,
					keywords,
					conceptTypes,
					beacons
			);

			return ResponseEntity.ok(query);
			
		} catch (BlackboardException bbe) {
			logError("postStatementsQuery", bbe);
			return ResponseEntity.badRequest().build();
		}
	}
	
	/**
	 * 
	 * @param queryId
	 * @param beacons
	 * @return
	 */
	public ResponseEntity<ServerStatementsQueryStatus> 
						getStatementsQueryStatus(
								String queryId, 
								List<Integer> beacons
	) {
		if( blackboard.isActiveQuery(queryId) ) {
			
			beacons = fixIntegerList(beacons);
			
			try {
				
				ServerStatementsQueryStatus queryStatus = 
						blackboard.getStatementsQueryStatus(
									queryId,
									beacons
								) ;						
	
				return ResponseEntity.ok(queryStatus);
				
			} catch (BlackboardException bbe) {
				logError("getStatementsQueryStatus", bbe);
				return ResponseEntity.badRequest().build();
			}
		} else
			return ResponseEntity.notFound().build();
	}

	/**
	 * 
	 * @param queryId
	 * @param beacons
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public ResponseEntity<ServerStatementsQueryResult> 
								getStatements(
										String queryId, 
										List<Integer> beacons,
										Integer pageNumber, 
										Integer pageSize
	) {
		if( blackboard.isActiveQuery(queryId) ) {
			
			beacons      = fixIntegerList(beacons);
			pageNumber   = fixInteger(pageNumber);		
			pageSize     = fixInteger(pageSize);
		
			try {	
				// retrieve the data, assuming it is available
				ServerStatementsQueryResult result = 
						blackboard.retrieveStatementsQueryResults(
										queryId,
										pageNumber, 
										pageSize,
										beacons
									) ;
				
				return ResponseEntity.ok(result);
				
			} catch (BlackboardException bbe) {
				logError(queryId, bbe);
				return ResponseEntity.badRequest().build();
			}
			
		} else
			return ResponseEntity.notFound().build();
	}
	
	/**
	 * 
	 * @param statementId
	 * @param keywords
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<List<ServerAnnotation>> getEvidence(
			String statementId, 
			String keywords, 
			Integer pageNumber, 
			Integer pageSize, 
			List<Integer> beacons
	) {

		pageNumber  = fixInteger(pageNumber);
		pageSize    = fixInteger(pageSize);
		keywords    = fixString(keywords);
		statementId = fixString(statementId);
		beacons     = fixIntegerList(beacons);
		
		List<ServerAnnotation> responses = null;
		
		List<String> keywordsList = Arrays.asList(keywords.split(" "));
		
		try {
			
			responses =
					blackboard.getEvidence(
							statementId,
							keywordsList,
							pageSize,
							beacons
					);
			return ResponseEntity.ok(responses);
			
		} catch (BlackboardException bbe) {
			logError(statementId, bbe);
			return ResponseEntity.badRequest().build();
		}
	}
	
}

