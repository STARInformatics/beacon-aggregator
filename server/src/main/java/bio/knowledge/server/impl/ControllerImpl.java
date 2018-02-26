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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.aggregator.BeaconKnowledgeMap;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.ConceptTypeUtil;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.LogEntry;
import bio.knowledge.aggregator.blackboard.Blackboard;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.model.BioNameSpace;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.umls.Category;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptBeaconEntry;
import bio.knowledge.server.model.ServerConceptType;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerKnowledgeMap;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementSubject;

@Service
public class ControllerImpl implements SystemTimeOut, ConceptTypeUtil {

	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);

	@Autowired private Blackboard blackboard;
		
	@Override
	public int countAllBeacons() {
		return blackboard.countAllBeacons();
	}
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;

	@Autowired private ConceptTypeService conceptTypeService;

	@Autowired private MetadataCache metadataCache;
	
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
	 * @param sessionId
	 * @param e
	 */
	private void logError(String sessionId, Exception e) {
		
		if(sessionId.isEmpty()) sessionId = "Global";
				
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		String message = e.getMessage();
		if(message!=null) _logger.error(sessionId+": "+message);
		
		blackboard.logError(sessionId, "aggregator", getUrl(request), e.getMessage());
	}
	
/******************************** METADATA Endpoints *************************************/

	/**
	 * 
	 * @return HTTP ResponseEntity of a List of ServerKnowledgeBeacon entries
	 */
	public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons() {
		
		List<ServerKnowledgeBeacon> responses = new ArrayList<>();
		
		try {
			
			List<KnowledgeBeacon> beacons = metadataCache.getKnowledgeBeacons();
			
			for (KnowledgeBeacon beacon : beacons) {
				responses.add(ModelConverter.convert(beacon, ServerKnowledgeBeacon.class));
			}
			
		} catch (Exception e) {
			logError("Global", e);
		}
				
		return ResponseEntity.ok(responses);
	}

	/**
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity< List<ServerConceptType>> getConceptTypes(List<String> beacons,String sessionId) {
			
		beacons = fixString(beacons);
		sessionId = fixString(sessionId);
		
		List<ServerConceptType> responses = new ArrayList<ServerConceptType>();
		
		try {
				
			responses.addAll( metadataCache.getConceptTypes( beacons, sessionId ) );
			
		} catch (Exception e) {
			logError(sessionId, e);
		}
		
		return ResponseEntity.ok(responses);		
    }
	
	/**
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity<List<ServerPredicate>> getPredicates(List<String> beacons, String sessionId) {
		
		beacons = fixString(beacons);
		sessionId = fixString(sessionId);
		
		List<ServerPredicate> responses = new ArrayList<ServerPredicate>();
		
		try {
				
			responses.addAll( metadataCache.getPredicates( beacons, sessionId ) );
			
		} catch (Exception e) {
			logError("global", e);
		}

		return ResponseEntity.ok(responses);
	}

	/**
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity<List<ServerKnowledgeMap>> getKnowledgeMap(List<String> beacons, String sessionId) {

		beacons = fixString(beacons);
		sessionId = fixString(sessionId);

		List<ServerKnowledgeMap> responses = new ArrayList<ServerKnowledgeMap>();
		
		try {
			Map<
				KnowledgeBeacon, 
				List<BeaconKnowledgeMap>
			> kmaps = blackboard.getKnowledgeMap(beacons, sessionId);
	
			for (KnowledgeBeacon beacon : kmaps.keySet()) {
				
				for (BeaconKnowledgeMap knowledgeMap : kmaps.get(beacon)) {
					
					ServerKnowledgeMap translation = Translator.translate( knowledgeMap );
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
		} catch (Exception e) {
			logError(sessionId, e);
		}
		
		return ResponseEntity.ok(responses);
	}
	
	/**
	 * 
	 * @param sessionId
	 * @return HTTP ResponseEntity of a List of ServerLogEntry entries associated with the specified sessionId
	 */
	public ResponseEntity<List<ServerLogEntry>> getErrors(String sessionId) {
		
		sessionId = fixString(sessionId);
		
		List<ServerLogEntry> responses = new ArrayList<>();
		
		try {
			if(!sessionId.isEmpty()) {
		
				List<LogEntry> entries = blackboard.getErrors(sessionId);
				
				for (LogEntry entry : entries) {
					if (entry != null) {
						responses.add(ModelConverter.convert(entry, ServerLogEntry.class));
					}
				}
			} else {
				throw new RuntimeException("Mandatory Session ID parameter was not provided?");
			}
			
		} catch (Exception e) {
			logError(sessionId, e);
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
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity<List<ServerConcept>> getConcepts(
			String keywords, String conceptTypes, Integer pageNumber,
			Integer pageSize, List<String> beacons, String sessionId
	) {
		pageNumber   = fixInteger(pageNumber);
		pageSize     = fixInteger(pageSize);
		keywords     = fixString(keywords);
		conceptTypes = fixString(conceptTypes);
		beacons      = fixString(beacons);
		sessionId    = fixString(sessionId);
		
		List<ServerConcept> responses = new ArrayList<ServerConcept>();
		
		try {
			List<BeaconConcept> concepts = 
					blackboard.getConcepts(keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId);

			for (BeaconConcept concept : concepts) {
				ServerConcept translation = Translator.translate(concept);
				responses.add(translation);
			}
			
		} catch (Exception e) {
			logError(sessionId, e);
		}
		
		return ResponseEntity.ok(responses);
		
	}

	/**
	 * 
	 * @param identifier
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity<ServerCliqueIdentifier> getClique(String identifier, String sessionId) {
		
		ServerCliqueIdentifier cliqueId = null;
		
		try {
			
			ConceptClique clique = 
					exactMatchesHandler.getConceptClique(new String[] { identifier });
			
			if(clique!=null) {
				cliqueId = new ServerCliqueIdentifier();
				cliqueId.setCliqueId(clique.getId());
			}
		
		} catch (Exception e) {
			logError(sessionId, e);
		}
		
		return ResponseEntity.ok(cliqueId);
	}

	/**
	 * 
	 * @param cliqueId
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity<ServerConceptWithDetails> getConceptDetails(
			String cliqueId, 
			List<String> 
			beacons, 
			String sessionId
	) {
		
		cliqueId  = fixString(cliqueId);
		beacons   = fixString(beacons);
		sessionId = fixString(sessionId);
		
		ServerConceptWithDetails conceptDetails = null;
		
		try {

			ConceptClique ecc = exactMatchesHandler.getClique(cliqueId);

			if(ecc==null) 
				throw new RuntimeException("getConceptDetails(): '"+cliqueId+"' could not be found?") ;

			Map<
				KnowledgeBeacon, 
				List<BeaconConceptWithDetails>
			
			> conceptDetailsByBeacon = blackboard.getConceptDetails(ecc, beacons, sessionId);
			
			conceptDetails = new ServerConceptWithDetails();
			
			conceptDetails.setClique(cliqueId);
			
			/* 
			 * Defer name setting below; 
			 * clique name seems to be the 
			 * same as the cliqueId right now... 
			 * not sure if that is correct?
			 * 
			 * conceptDetails.setName(ecc.getName()); 
			 */
			conceptDetails.setType(ecc.getConceptType());
			conceptDetails.setAliases(ecc.getConceptIds());
			
			List<ServerConceptBeaconEntry> entries = conceptDetails.getEntries();
			
			for (KnowledgeBeacon beacon : conceptDetailsByBeacon.keySet()) {
				
				for (BeaconConceptWithDetails response : conceptDetailsByBeacon.get(beacon)) {
					
					/*
					 * Simple heuristic to set the name to something sensible.
					 * Since beacon-to-beacon names may diverge, may not always
					 * give the "best" name (if such a thing exists...)
					 */
					if( conceptDetails.getName() == null )
						conceptDetails.setName(response.getName());
					
					ServerConceptBeaconEntry entry = Translator.translate(response);
					entry.setBeacon(beacon.getId());
					entries.add(entry);
				}
			}
			
		} catch (Exception e) {
			logError(sessionId, e);
		}
		
		return ResponseEntity.ok(conceptDetails);
	}

	
/******************************** STATEMENT Endpoints *************************************/

	/*
	 * @param conceptId
	 * @param conceptName
	 * @param identifiers
	 * @return
	 */
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
	 * @param source
	 * @param relations
	 * @param target
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param sessionId
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
			List<String> beacons, 
			String sessionId
	) {
		
		source = fixString(source);
		relations = fixString(relations);
		target = fixString(target);
		keywords = fixString(keywords);
		conceptTypes = fixString(conceptTypes);
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		beacons = fixString(beacons);
		sessionId = fixString(sessionId);
		
		List<ServerStatement> responses = new ArrayList<ServerStatement>();
		
		try {
			
			if(source.isEmpty()) {
				throw new RuntimeException("ControllerImpl.getStatements(): empty source clique string encountered?") ;
			}
			
			ConceptClique sourceClique = exactMatchesHandler.getClique(source);
			if(sourceClique==null) {
				throw new RuntimeException("ControllerImpl.getStatements(): source clique '"+source+"' could not be found?") ;
			}

			ConceptClique targetClique = null;
			if(!target.isEmpty()) {
				targetClique = exactMatchesHandler.getClique(target);
				if(targetClique==null) {
					throw new RuntimeException("ControllerImpl.getStatements(): target clique '"+target+"' could not be found?") ;
				}
			}
			
			Map<
				KnowledgeBeacon, 
				List<BeaconStatement>
			> beaconStatements = 
						blackboard.getStatements(
								sourceClique, relations, targetClique, 
								keywords, conceptTypes, 
								pageNumber, pageSize, 
								beacons, sessionId
						) ;

			for (KnowledgeBeacon beacon : beaconStatements.keySet()) {
				
				String beaconId = beacon.getId();
				
				_logger.debug("ctrl.getStatements(): processing beacon '"+beaconId+"'...");
			
				for ( BeaconStatement response : beaconStatements.get(beacon)) {
					
					/*
					 * Sanity check: to get around the fact that some beacons 
					 * (like Biolink) will sometimes send back statements
					 *  with a null *%$@?!?!!! subject or object 
					 */
					if( response.getSubject()==null || response.getObject() == null ) continue;
										
					ServerStatement translation = Translator.translate(response);
					translation.setBeacon(beaconId);
					
					// Heuristic: need to somehow tag the equivalent concept here?
					ServerStatementSubject subject  = translation.getSubject();
					String subjectId = subject.getId();
					String subjectName = subject.getName();
					
					/*
					 * The existing beacons may not send the semantic group 
					 * back as a CURIE, thus coerce it accordingly
					 */
					String subjectTypeId = subject.getType();
					
					List<ConceptTypeEntry> subjectTypes = 
							conceptTypeService.lookUpByIdentifier(subjectTypeId);
					
					subject.setType(curieList(subjectTypes));
					
					ConceptClique subjectEcc = 
							exactMatchesHandler.getExactMatches(
													beacon,
													subjectId,
													subjectName,
													subjectTypes
												);
					
					bio.knowledge.server.model.ServerStatementObject object = translation.getObject();
					String objectId = object.getId();
					String objectName = object.getName();
					
					/*
					 * The existing beacons may not send the semantic group 
					 * back as a CURIE, thus coerce it accordingly
					 */
					String objectTypeId = object.getType();
					
					List<ConceptTypeEntry> objectTypes = 
							conceptTypeService.lookUpByIdentifier(objectTypeId);

					object.setType(curieList(objectTypes));
					
					ConceptClique objectEcc = 
							exactMatchesHandler.getExactMatches(
													beacon,
													objectId,
													objectName,
													objectTypes
												);
					
					/*
					 * Need to refresh the ecc clique in case either 
					 * subject or object id was discovered to belong 
					 * to it during the exact matches operations above?
					 */
					sourceClique = exactMatchesHandler.getClique(source);
					
					List<String> conceptIds = sourceClique.getConceptIds(beaconId);
					
					_logger.debug("ctrl.getStatements(): processing statement '"+translation.getId()
								+ " from beacon '"+beaconId + "' "
								+ "with subject id '"+subjectId + "' "
								+ "and object id '"+objectId+"'"
								+ " matched against conceptIds: '"+String.join(",",conceptIds)+"'"
							);
					
					if( matchToList( subjectId, subjectName, conceptIds ) ) {
						
						subject.setClique(sourceClique.getId());
						/*
						 * Temporary workaround for beacons not yet 
						 * setting their statement subject semantic groups?
						 */
						String ssg = subject.getType();
						if( ( ssg==null || ssg.isEmpty() || ssg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && sourceClique != null )
							subject.setType(sourceClique.getConceptType());
						
						object.setClique(objectEcc.getId());
						/*
						 * Temporary workaround for beacons not yet 
						 * setting their statement object semantic groups?
						 */
						String osg = object.getType();
						if( ( osg==null || osg.isEmpty() || osg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && objectEcc != null )
							object.setType(objectEcc.getConceptType());
						
					} else if( matchToList( objectId, objectName, conceptIds ) ) {
						
						object.setClique(sourceClique.getId()) ;
						/*
						 * Temporary workaround for beacons not yet 
						 * setting their statement object semantic groups?
						 */
						String objectConceptType = object.getType();
						if( ( objectConceptType==null ||
							  objectConceptType.isEmpty() || 
							  objectConceptType.equals(Category.DEFAULT_SEMANTIC_GROUP)) && sourceClique != null
						)
							object.setType(sourceClique.getConceptType());
						
						subject.setClique(subjectEcc.getId());
						/*
						 * Temporary workaround for beacons not yet 
						 * setting their statement subject semantic groups?
						 */
						String subjectConceptType = subject.getType();
						if( ( subjectConceptType==null || 
							  subjectConceptType.isEmpty() || 
							  subjectConceptType.equals(Category.DEFAULT_SEMANTIC_GROUP)) && subjectEcc != null 
						)
							object.setType(subjectEcc.getConceptType());	
						
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
						subject.getType() == null) {
							
							subject.setType(
										BioNameSpace.defaultConceptType( subject.getClique() ).getCurie()
									);
					}
					
					if( object.getClique() != null && 
						object.getType() == null) {
							
							object.setType(
										BioNameSpace.defaultConceptType( object.getClique() ).getCurie()
									);
					}
					
					responses.add(translation);
				}
			}
			
			if( ! relations.isEmpty() ) {
				final String relationFilter = relations;
				responses = responses.stream()
						.filter(
							s -> s.getPredicate().getId().equals(relationFilter) ? true : false 
				).collect(Collectors.toList());
			}
			
		} catch (Exception e) {
			logError(sessionId, e);
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
	 * @param sessionId
	 * @return
	 */
	public ResponseEntity<List<ServerAnnotation>> getEvidence(
			String statementId, 
			String keywords, 
			Integer pageNumber, 
			Integer pageSize, 
			List<String> beacons, 
			String sessionId
	) {

		pageNumber  = fixInteger(pageNumber);
		pageSize    = fixInteger(pageSize);
		keywords    = fixString(keywords);
		statementId = fixString(statementId);
		beacons     = fixString(beacons);
		sessionId   = fixString(sessionId);
		
		List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();
		
		try {
			Map<
				KnowledgeBeacon,
				List<BeaconAnnotation>
			>
				evidence = blackboard.getEvidence(
									statementId,
									keywords,
									pageNumber,
									pageSize,
									beacons,
									sessionId
							);
	
			for (KnowledgeBeacon beacon : evidence.keySet()) {
				for (BeaconAnnotation reference : evidence.get(beacon)) {
					ServerAnnotation translation = ModelConverter.convert(reference, ServerAnnotation.class);
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
			
		} catch (Exception e) {
			logError(sessionId, e);
		}
		
		return ResponseEntity.ok(responses);
	}
	
}

