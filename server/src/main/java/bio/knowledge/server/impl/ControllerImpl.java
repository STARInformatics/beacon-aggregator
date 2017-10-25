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
package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptDetail;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconSummary;
import bio.knowledge.model.BioNameSpace;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptDetail;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementSubject;
import bio.knowledge.server.model.ServerSummary;

@Service
public class ControllerImpl {

	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);

	Map<String, HashSet<String>> cache = new HashMap<String, HashSet<String>>();
	
	@Autowired private KnowledgeBeaconRegistry registry;

	@Autowired private KnowledgeBeaconService kbs;
		
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	
	@Autowired private PredicatesRegistry predicatesRegistry;

	private Integer fixInteger(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	private String fixString(String str) {
		return str != null ? str : "";
	}
	
	private List<String> fixString(List<String> l) {
		if (l == null) return new ArrayList<>();
		
		for (int i = 0; i < l.size(); i++) {
			l.set(i, fixString(l.get(i)));
		}
		
		return l;
	}

	
	/**
	 * 
	 * @param request
	 * @return url used to make the request
	 */
	private String getUrl(HttpServletRequest request) {
		String query = request.getQueryString();
		query = (query == null)? "" : "?" + query;
		return request.getRequestURL() + query;
	}
	
	private void logError(String sessionId, Exception e) {
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		String message = e.getMessage();
		if(message!=null) _logger.error(sessionId+": "+message);
		
		kbs.logError(sessionId, "aggregator", getUrl(request), e.getMessage());
	}
	
	
	private <T> Map<KnowledgeBeaconImpl, List<T>> waitFor(CompletableFuture<Map<KnowledgeBeaconImpl, List<T>>> future) {
		return waitFor(
				future,
				// Scale the timeout proportionately to the number of beacons?
				registry.countAllBeacons()*KnowledgeBeaconService.BEACON_TIMEOUT_DURATION
		) ; 
	}
	 
	/*
	 * Waits {@code TIMEOUT} {@code TIMEUNIT} for the future to complete, throwing a runtime exception otherwise.
	 * @param future
	 * @return
	 */
	private <T> Map<KnowledgeBeaconImpl, List<T>> 
		waitFor(
				CompletableFuture<Map<KnowledgeBeaconImpl, List<T>>> future,
				long timeout
		) {
		try {
			return future.get(timeout, KnowledgeBeaconService.BEACON_TIMEOUT_UNIT);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons() {
		
		List<ServerKnowledgeBeacon> beacons = new ArrayList<>();
		for (Object beacon : registry.getKnowledgeBeacons()) {
			beacons.add(ModelConverter.convert(beacon, ServerKnowledgeBeacon.class));
		}
		
		return ResponseEntity.ok(beacons);
	}

	public ResponseEntity<List<ServerLogEntry>> getErrors(String sessionId) {
		sessionId = fixString(sessionId);

		List<ServerLogEntry> responses = new ArrayList<>();
		for (Object entry : kbs.getErrors(sessionId)) {
			if (entry != null) {
				responses.add(ModelConverter.convert(entry, ServerLogEntry.class));
			}
		}

		return ResponseEntity.ok(responses);
	}

	
	public ResponseEntity<List<ServerConcept>> getConcepts(String keywords, String semanticGroups, Integer pageNumber,
			Integer pageSize, List<String> beacons, String sessionId) {
		try {
			
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			semanticGroups = fixString(semanticGroups);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
	
			CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconConcept>>>
				future = kbs.getConcepts(keywords, semanticGroups, pageNumber, pageSize, beacons, sessionId);
	
			List<ServerConcept> responses = new ArrayList<ServerConcept>();
			
			Map<KnowledgeBeaconImpl, List<BeaconConcept>> map = 
					waitFor(
							future,
							kbs.weightedTimeout(beacons,pageSize)
					);
			
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				
				for (Object response : map.get(beacon)) {
					
					ServerConcept translation = ModelConverter.convert(response, ServerConcept.class);

					ConceptClique ecc = 
							exactMatchesHandler.getExactMatches(
										beacon,
										translation.getId(),
										translation.getName()
									);
					translation.setClique(ecc.getId());
					translation.setAliases(ecc.getConceptIds());
					
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
				
			return ResponseEntity.ok(responses);
		
		} catch (Exception e) {
			logError(sessionId, e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	public ResponseEntity<List<ServerPredicate>> getPredicates() {
		try {
			CompletableFuture<
				Map<KnowledgeBeaconImpl, 
				List<BeaconPredicate>>
			> future = kbs.getAllPredicates();

			Map<KnowledgeBeaconImpl, List<BeaconPredicate>> map = waitFor( future );

			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (BeaconPredicate response : map.get(beacon)) {
					/*
					 *  No "conversion" here, but response 
					 *  handled by the indexPredicate function
					 */
					predicatesRegistry.indexPredicate(response,beacon.getId());
				}
			}
			
			List<ServerPredicate> responses = 
					new ArrayList<ServerPredicate>(predicatesRegistry.values());
			
			return ResponseEntity.ok(responses);

		} catch (Exception e) {
			logError("Predicates", e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	public ResponseEntity<List<ServerConceptWithDetails>> getConceptDetails(
			String cliqueId, 
			List<String> 
			beacons, 
			String sessionId
	) {
		try {
		
			cliqueId = fixString(cliqueId);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);

			ConceptClique ecc = exactMatchesHandler.getClique(cliqueId);
			if(ecc==null) throw new RuntimeException("getConceptDetails(): '"+cliqueId+"' could not be found?") ;

			CompletableFuture<
								Map<KnowledgeBeaconImpl, 
								List<BeaconConceptWithDetails>>
							> future = kbs.getConceptDetails(ecc, beacons, sessionId);
	
			List<ServerConceptWithDetails> responses = 
					new ArrayList<ServerConceptWithDetails>();
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconConceptWithDetails>
			> map = waitFor(
					future,
					kbs.weightedTimeout(beacons,1)
			);  // Scale timeout proportionately to the number of beacons only?
					
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					
					ServerConceptWithDetails translation = 
							ModelConverter.convert(response, ServerConceptWithDetails.class);
					
					translation.setClique(ecc.getId());
					translation.setAliases(ecc.getConceptIds());
					
					/*
					 * Try to retrieve any details returned...
					 *  Note that the details returned by Translator 
					 *  are still the original beacon version of the Java object!
					 */
					@SuppressWarnings({ "unchecked", "rawtypes" })
					List<BeaconConceptDetail> details = 
								(List<BeaconConceptDetail>)(List)translation.getDetails();
					
					List<ServerConceptDetail> translatedDetails = 
													new ArrayList<ServerConceptDetail>();
					
					for(BeaconConceptDetail detail : details) {
						ServerConceptDetail sdt = new ServerConceptDetail();
						String tag   = detail.getTag();
						String value = detail.getValue();
						sdt.setTag(tag);
						sdt.setValue(value);
						translatedDetails.add(sdt);
						_logger.debug("getConceptDetails() details - Tag: '"+tag+
								      "' = Value: '"+value.toString()+"'");
					}
					
					translation.setDetails(translatedDetails);
					
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
	
			return ResponseEntity.ok(responses);
			
		} catch (Exception e) {
			logError(sessionId, e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	private Boolean matchToList(String conceptId, String conceptName, List<String> identifiers ) {
		
		String idPattern = "(?i:"+conceptId+")";
		
		/*
		 *  Special test for the presence of 
		 *  Human Gene Nomenclature Consortium (and geneCards) symbols.
		 *  Case insensitive match to non-human species symbols
		 *  which may have difference letter case?
		 */
		String hgncSymbolPattern = "HGNC.SYMBOL:(?i:"+conceptName.toUpperCase()+")";
		String genecardsPattern = "GENECARDS:(?i:"+conceptName.toUpperCase()+")";
		String umlsPattern = "UMLS:(?i:"+conceptName.toUpperCase()+")";
		
		for(String id : identifiers) {
			
			if(id.matches(idPattern)) 
				return true;
			
			if(id.matches(hgncSymbolPattern)) 
				return true;
			
			if(id.matches(genecardsPattern)) 
				return true;
			
			if(id.matches(umlsPattern)) 
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param cliqueId
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param semanticGroups
	 * @param relations
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity<List<ServerStatement>> getStatements(
			String cliqueId, 
			Integer pageNumber, 
			Integer pageSize,
			String keywords,
			String semanticGroups,
			String relations, 
			List<String> beacons, 
			String sessionId
	) {
		try {
			
			cliqueId = fixString(cliqueId);
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			semanticGroups = fixString(semanticGroups);
			relations = fixString(relations);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
			
			ConceptClique ecc = exactMatchesHandler.getClique(cliqueId);
			if(ecc==null) throw new RuntimeException("getStatements(): '"+cliqueId+"' could not be found?") ;

			CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconStatement>>> future = 
					kbs.getStatements(ecc, keywords, semanticGroups, relations, pageNumber, pageSize, beacons, sessionId);
			
			List<ServerStatement> responses = new ArrayList<ServerStatement>();
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconStatement>
			> map = waitFor(future,kbs.weightedTimeout(beacons, pageSize));

			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				
				String beaconId = beacon.getId();
				
				_logger.debug("ctrl.getStatements(): processing beacon '"+beaconId+"'...");
			
				for (Object response : map.get(beacon)) {
										
					ServerStatement translation = ModelConverter.convert(response, ServerStatement.class);
					translation.setBeacon(beaconId);
					
					// Heuristic: need to somehow tag the equivalent concept here?
					ServerStatementSubject subject  = translation.getSubject();
					String subjectId = subject.getId();
					String subjectName = subject.getName();
					ConceptClique subjectEcc = exactMatchesHandler.getExactMatches(beacon,subjectId,subjectName);
					
					bio.knowledge.server.model.ServerStatementObject object = translation.getObject();
					String objectId = object.getId();
					String objectName = object.getName();
					ConceptClique objectEcc = exactMatchesHandler.getExactMatches(beacon,objectId,objectName);
					
					// need to refresh the ecc clique in case either subject or object id was discovered to belong to it during the exact matches operations above?
					ecc = exactMatchesHandler.getClique(cliqueId);
					
					List<String> conceptIds = ecc.getConceptIds(beaconId);
					
					_logger.debug("ctrl.getStatements(): processing statement '"+translation.getId()
								+ "from beacon '"+beaconId + "' "
								+ "with subject id '"+subjectId + "' "
								+ "and object id '"+objectId+"'"
								+ " matched against conceptIds: '"+String.join(",",conceptIds)+"'"
							);
					
					if( matchToList( subjectId, subjectName, conceptIds ) ) {
						
						subject.setClique(ecc.getId());
						object.setClique(objectEcc.getId());
						
					} else if( matchToList( objectId, objectName, conceptIds ) ) {
						
						object.setClique(ecc.getId()) ; 						
						subject.setClique(subjectEcc.getId());
						
					} else {
						
						_logger.warn("ctrl.getStatements() WARNING: "
								+ "clique is unknown (null) "
								+ "for statement '"+translation.getId()
								+ "from beacon '"+beaconId
								+"' with subject '"+subject.getName()
								+"' ["+subjectId+"]"
								+ " and object '"+object.getName()
								+"' ["+objectId+"]"
								+ " matched against conceptIds: '"+
								String.join(",",conceptIds)+"'"
						);
						continue;
					}
					
					/*
					 *  Heuristic workaround for beacons which have not yet properly 
					 *  implemented the tagging of semantic groups of concepts,
					 *  to try to set their semantic group type
					 */
					if( subject.getClique() != null && 
						subject.getSemanticGroup() == null) {
							
							subject.setSemanticGroup(
										BioNameSpace.defaultSemanticGroup( subject.getClique() )
									);
					}
					
					if( object.getClique() != null && 
						object.getSemanticGroup() == null) {
							
							object.setSemanticGroup(
										BioNameSpace.defaultSemanticGroup( object.getClique() )
									);
					}
					responses.add(translation);
				}
			}
			
			/* doesn't seem to work properly?
			 * 

			if( ! relations.isEmpty() ) {
				final String relationFilter = relations;
				responses = responses.stream()
						.filter(
								s->s.getPredicate().getName().equals(relationFilter) ? true : false 
				).collect(Collectors.toList());
			}
			
			*/
			
			return ResponseEntity.ok(responses);
		} catch (Exception e) {
			
			logError(sessionId, e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	public ResponseEntity<List<ServerAnnotation>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize, List<String> beacons, String sessionId) {
		try {
		
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			statementId = fixString(statementId);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
			
			CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconAnnotation>>> future = 
					kbs.getEvidence(statementId, keywords, pageNumber, pageSize, beacons, sessionId);
			
			List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconAnnotation>
			> map = waitFor(future,kbs.weightedTimeout(beacons, pageSize));
					
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					ServerAnnotation translation = ModelConverter.convert(response, ServerAnnotation.class);
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
			
			return ResponseEntity.ok(responses);
			
		} catch (Exception e) {
			logError(sessionId, e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	public ResponseEntity<List<ServerSummary>> linkedTypes(List<String> beacons, String sessionId) {
		try {
			
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
		
			CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconSummary>>>
				future = kbs.linkedTypes(beacons, sessionId);
			
			List<ServerSummary> responses = new ArrayList<ServerSummary>();
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconSummary>
			> map = waitFor(future);
			
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (Object summary : map.get(beacon)) {
					ServerSummary translation = ModelConverter.convert(summary, ServerSummary.class);
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
	
			return ResponseEntity.ok(responses);
		
		} catch (Exception e) {
			logError(sessionId, e);
			return ResponseEntity.ok(new ArrayList<>());
		}
    }
	
}
