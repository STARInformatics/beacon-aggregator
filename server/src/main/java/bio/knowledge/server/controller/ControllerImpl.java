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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bio.knowledge.server.blackboard.Blackboard;
import bio.knowledge.server.blackboard.BlackboardException;
import bio.knowledge.server.blackboard.MetadataService;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptType;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerKnowledgeMap;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementsQuery;
import bio.knowledge.server.model.ServerStatementsQueryResult;
import bio.knowledge.server.model.ServerStatementsQueryStatus;

/**
 * This is the KBA Controller class containing the delegated handlers for the various API endpoints.
 * 
 * The main role of these handlers are:
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
public class ControllerImpl {

	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);

	@Autowired private Blackboard blackboard;
	@Autowired private MetadataService metadataService;
	
	/*
	 * @param i
	 * @return
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
	 * @return
	 */
	private String fixString(String str) {
		//return str != null ? str : null;
		return str != null ? str : "";
	}
	
	/*
	 * @param l
	 * @return
	 */
	private List<String> fixString(List<String> l) {
		if (l == null) return new ArrayList<>();
		
		for (int i = 0; i < l.size(); i++) {
			l.set(i, fixString(l.get(i)));
		}
		
		return l;
	}
	
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
		List<ServerKnowledgeBeacon> beacons = null;
		try {
			beacons = metadataService.getKnowledgeBeacons();
		} catch(BlackboardException bbe) {
			logError("Global",bbe);
			beacons = new ArrayList<ServerKnowledgeBeacon>();
		}
		return ResponseEntity.ok(beacons);
	}

	/**
	 * 
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity< List<ServerConceptType>> getConceptTypes(List<Integer> beacons) {
			
		beacons = fixIntegerList(beacons);
		
		List<ServerConceptType> responses = new ArrayList<ServerConceptType>();
		
		try {
			responses.addAll( metadataService.getConceptTypes( beacons ) );
		} catch (BlackboardException bbe) {
			logError("Global", bbe);
		}
		
		return ResponseEntity.ok(responses);		
    }
	
	/**
	 * 
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<List<ServerPredicate>> getPredicates(List<Integer> beacons) {
		
		beacons = fixIntegerList(beacons);
		
		List<ServerPredicate> responses = new ArrayList<ServerPredicate>();
		
		try {
			responses.addAll( metadataService.getPredicates( beacons ) );
		} catch (BlackboardException bbe) {
			logError("global", bbe);
		}

		return ResponseEntity.ok(responses);
	}

	/**
	 * 
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<List<ServerKnowledgeMap>> getKnowledgeMap(List<Integer> beacons) {

		beacons = fixIntegerList(beacons);

		List<ServerKnowledgeMap> responses = null;

		try {
			responses = metadataService.getKnowledgeMap( beacons);
		} catch (BlackboardException e) {
			logError("Global", e);
		}
		
		return ResponseEntity.ok(responses);
	}
	
	/**
	 * 
	 * @param queryId
	 * @return HTTP ResponseEntity of a List of ServerLogEntry entries associated with the specified queryId
	 */
	public ResponseEntity<List<ServerLogEntry>> getErrors(String queryId) {
		
		queryId = fixString(queryId);
		
		List<ServerLogEntry> responses = null;
		
		try {
			if(!queryId.isEmpty()) {
				responses = metadataService.getErrors(queryId);
			} else {
				throw new RuntimeException("Mandatory Session ID parameter was not provided?");
			}
			
		} catch (Exception e) {
			logError(queryId, e);
			responses = new ArrayList<ServerLogEntry>();
		}

		return ResponseEntity.ok(responses);
	}

/******************************** CONCEPT Endpoints *************************************/

	/**
	 * 
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<List<ServerConcept>> getConcepts(
			String keywords, String conceptTypes, Integer pageNumber,
			Integer pageSize, List<Integer> beacons, String queryId
	) {
		pageNumber   = fixInteger(pageNumber);		
		pageSize     = fixInteger(pageSize);
		keywords     = fixString(keywords);
		conceptTypes = fixString(conceptTypes);
		beacons      = fixIntegerList(beacons);
		queryId      = fixString(queryId);
		
		List<ServerConcept> responses = null;
		
		try {
			
			responses = blackboard.getConcepts(
									keywords, 
									conceptTypes, 
									pageNumber, 
									pageSize, 
									beacons, 
									queryId
						) ;
			
		} catch (BlackboardException bbe) {
			logError(queryId, bbe);
			responses = new ArrayList<ServerConcept>();
		}
		
		return ResponseEntity.ok(responses);
		
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
		} catch (BlackboardException bbe) {
			logError("Global", bbe);
		}
		
		return ResponseEntity.ok(cliqueId);
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

		} catch (BlackboardException bbe) {
			logError("Global", bbe);
		}
		
		return ResponseEntity.ok(conceptDetails);
	}

	
/******************************** STATEMENT Endpoints *************************************/

	/**
	 * 
	 * @param source
	 * @param relations
	 * @param target
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public ResponseEntity<List<ServerStatement>> getStatements(
			String source,
			String relations,
			String target,
			String keywords,
			String conceptTypes,
			Integer pageNumber, 
			Integer pageSize, 
			List<Integer> beacons, 
			String queryId
	) {
		
		source = fixString(source);
		relations = fixString(relations);
		target = fixString(target);
		keywords = fixString(keywords);
		conceptTypes = fixString(conceptTypes);
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		beacons = fixIntegerList(beacons);
		queryId = fixString(queryId);
		
		List<ServerStatement> responses = null;
		
		try {
			responses = 
					blackboard.getStatements(
							source,
							relations,
							target,
							keywords,
							conceptTypes,
							pageNumber, 
							pageSize, 
							beacons, 
							queryId
					);
			
		} catch (BlackboardException bbe) {
			logError(queryId, bbe);
			responses = new ArrayList<ServerStatement>();
		}
		
		return ResponseEntity.ok(responses);
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
		
		try {
			
			responses =
					blackboard.getEvidence(
							statementId,
							keywords,
							pageNumber,
							pageSize,
							beacons
					);
			
			
		} catch (BlackboardException bbe) {
			logError(statementId, bbe);
			responses = new ArrayList<ServerAnnotation>();
		}
		
		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<ServerConceptsQueryResult> getConcepts(String queryId, List<Integer> beacons,
			Integer pageNumber, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<ServerConceptsQueryStatus> getConceptsQueryStatus(String queryId, List<Integer> beacons) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<ServerConceptsQuery> postConceptsQuery(String keywords, String types, List<Integer> beacons) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<ServerStatementsQueryResult> getStatementsQuery(String queryId, List<Integer> beacons,
			Integer pageNumber, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<ServerStatementsQueryStatus> getStatementsQueryStatus(String queryId, List<Integer> beacons) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<ServerStatementsQuery> postStatementsQuery(String source, String relations, String target,
			String keywords, String types) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

