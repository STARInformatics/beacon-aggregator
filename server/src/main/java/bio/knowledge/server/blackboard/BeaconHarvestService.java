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
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.aggregator.BeaconConceptWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.Harvester;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.Harvester.BeaconInterface;
import bio.knowledge.aggregator.Harvester.DatabaseInterface;
import bio.knowledge.aggregator.Harvester.RelevanceTester;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.QueryTracker;
import bio.knowledge.aggregator.Timer;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.model.BioNameSpace;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.umls.Category;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptBeaconEntry;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementSubject;

@Service
public class BeaconHarvestService implements SystemTimeOut {
	
	private static Logger _logger = LoggerFactory.getLogger(BeaconHarvestService.class);

	@Autowired private KnowledgeBeaconRegistry registry;
	
	@Autowired private KnowledgeBeaconService kbs;

	@Autowired private ExactMatchesHandler exactMatchesHandler;

	@Override
	public int countAllBeacons() {
		return registry.countAllBeacons();
	}
	
	private final String KEYWORD_DELIMINATOR = " ";
	
	@Autowired private QueryTracker<BeaconConcept> queryTracker;
	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private ConceptRepository  conceptRepository;
	@Autowired private TaskExecutor executor;
	
	protected Integer fixInteger(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	protected String fixString(String str) {
		return str != null ? str : "";
	}
	
	protected List<String> fixString(List<String> l) {
		if (l == null) return new ArrayList<>();
		
		for (int i = 0; i < l.size(); i++) {
			l.set(i, fixString(l.get(i)));
		}
		
		return l;
	}

/******************************** CONCEPT Data Access *************************************/
	
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
	public CompletableFuture<List<BeaconConcept>> initiateConceptHarvest(
			String keywords,
			String conceptTypes,
			Integer pageNumber,
			Integer pageSize,
			List<String> beacons,
			String sessionId
	) {
		if (beacons == null) {
			beacons = new ArrayList<String>();
		}
		
		Harvester<BeaconConcept, BeaconConcept> harvester = 
				new Harvester<BeaconConcept, BeaconConcept>(
						buildBeaconInterface(keywords, conceptTypes, beacons, sessionId),
						buildDatabaseInterface(),
						buildRelevanceTester(keywords, conceptTypes),
						executor,
						queryTracker
				);
		
		return harvester.initiateHarvest(keywords, conceptTypes, pageNumber, pageSize);
	}
	
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
	public List<ServerConcept> harvestConcepts(
			String keywords,
			String conceptTypes,
			Integer pageNumber,
			Integer pageSize,
			List<String> beacons,
			String sessionId
	) {
		List<ServerConcept> serverConcepts = new ArrayList<ServerConcept>();
		
		List<BeaconConcept> beaconConcepts = null ;
		
		CompletableFuture<List<BeaconConcept>> f = 
						    			initiateConceptHarvest(
						    				keywords,
						    				conceptTypes,
						    				pageNumber,
						    				pageSize,
						    				beacons,
						    				sessionId
						    			);
		try {
			
			beaconConcepts = f.get(
					KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,
					KnowledgeBeaconService.BEACON_TIMEOUT_UNIT
			);
		
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			
			e.printStackTrace();
			
			beaconConcepts = new ArrayList<BeaconConcept>();
		}
		
		for (BeaconConcept concept : beaconConcepts) {
			ServerConcept translation = Translator.translate(concept);
			serverConcepts.add(translation);
		}
		
		return serverConcepts;
	}
	

	public ServerConceptWithDetails harvestConceptsWithDetails(String cliqueId, List<String> beacons,
			String sessionId) {

		ServerConceptWithDetails conceptDetails = null;
		
		try {
		
		ConceptClique clique = exactMatchesHandler.getClique(cliqueId);
		
		if(clique==null) 
		throw new RuntimeException("getConceptDetails(): '"+cliqueId+"' could not be found?") ;
		
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
		conceptDetails.setType(clique.getConceptType());
		conceptDetails.setAliases(clique.getConceptIds());
		
		List<ServerConceptBeaconEntry> entries = conceptDetails.getEntries();
		
		CompletableFuture<
		Map<KnowledgeBeaconImpl, 
		List<BeaconConceptWithDetails>>
		> future = kbs.getConceptDetails(clique, beacons, sessionId);
		
		Map<
		KnowledgeBeaconImpl, 
		List<BeaconConceptWithDetails>
		> conceptDetailsByBeacon = waitFor(
				future,
				weightedTimeout(beacons,1)
			);  // Scale timeout proportionately to the number of beacons only?
		
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
		throw new BlackboardException(e);
		}
		
		return conceptDetails;
	}

/******************************** STATEMENTS Data Access *************************************/


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
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param sessionId 
	 * @param sessionId
	 * @return
	 */
	public List<ServerStatement> harvestStatements(
			String source, String relations, String target, 
			String keywords, String conceptTypes, 
			Integer pageNumber, Integer pageSize,
			List<String> beacons, String sessionId
	) {
		
		
		ConceptClique sourceClique = exactMatchesHandler.getClique(source);
		if(sourceClique==null) {
			throw new RuntimeException("Blackboard.getStatements(): source clique '"+source+"' could not be found?") ;
		}

		ConceptClique targetClique = null;
		if(!target.isEmpty()) {
			targetClique = exactMatchesHandler.getClique(target);
			if(targetClique==null) {
				throw new RuntimeException("Blackboard.getStatements(): target clique '"+target+"' could not be found?") ;
			}
		}
		
		CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconStatement>>> future = 
				kbs.getStatements( sourceClique, relations, targetClique, keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId );
		
		Map<
			KnowledgeBeaconImpl, 
			List<BeaconStatement>
		> beaconStatements = waitFor(future,weightedTimeout(beacons, pageSize));
		
		for (KnowledgeBeaconImpl beacon : beaconStatements.keySet()) {
			
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
				
				ServerStatementObject object = translation.getObject();
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

	
	return responses;
	}
	
	private RelevanceTester<BeaconConcept> buildRelevanceTester(String keywords, String conceptTypes) {
		return new RelevanceTester<BeaconConcept>() {

			@Override
			public boolean isItemRelevant(BeaconItemWrapper<BeaconConcept> beaconItemWrapper) {
				BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
				BeaconConcept concept = conceptWrapper.getItem();
				
				String[] keywordsArray = keywords.split(KEYWORD_DELIMINATOR);
				
				if (conceptTypes != null && !conceptTypes.toLowerCase().contains(concept.getSemanticGroup().toLowerCase())) {
					return false;
				}
				
				for (String keyword : keywordsArray) {
					if (concept.getName().toLowerCase().contains(keyword.toLowerCase())) {
						return true;
					}
				}
				
				return false;
			}
			
		};
	}

	private BeaconInterface<BeaconConcept> buildBeaconInterface(String keywords, String conceptTypes, List<String> beacons, String sessionId) {
		return new BeaconInterface<BeaconConcept>() {

			@Override
			public Map<KnowledgeBeaconImpl, List<BeaconItemWrapper<BeaconConcept>>> getDataFromBeacons(Integer pageNumber,
					Integer pageSize) throws InterruptedException, ExecutionException, TimeoutException {
				Timer.setTime("Search concept: " + keywords);
				CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconItemWrapper<BeaconConcept>>>>
					future = kbs.getConcepts(keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId);
				return future.get(
						KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,
						KnowledgeBeaconService.BEACON_TIMEOUT_UNIT
				);
			}
		};
	}
	
	// TODO: The purpose and nature of this class needs to be reviewed
	private DatabaseInterface<BeaconConcept, BeaconConcept> buildDatabaseInterface() {
		
		return new DatabaseInterface<BeaconConcept, BeaconConcept>() {

			@Override
			public boolean cacheData(KnowledgeBeaconImpl kb, BeaconItemWrapper<BeaconConcept> beaconItemWrapper, String queryString) {
				BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
				BeaconConcept concept = conceptWrapper.getItem();
				
				ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getSemanticGroup());
				Neo4jConcept neo4jConcept = new Neo4jConcept();
				
				List<ConceptTypeEntry> types = new ArrayList<ConceptTypeEntry>();
				types.add(conceptType);
				
				neo4jConcept.setClique(conceptWrapper.getClique());
				neo4jConcept.setName(concept.getName());
				neo4jConcept.setTypes(types);
				neo4jConcept.setQueryFoundWith(queryString);
				neo4jConcept.setSynonyms(concept.getSynonyms());
				neo4jConcept.setDefinition(concept.getDefinition());
				
				if (!conceptRepository.exists(neo4jConcept.getClique(), queryString)) {
					conceptRepository.save(neo4jConcept);
					return true;
				} else {
					return false;
				}
			}

			@Override
			public List<BeaconConcept> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize) {
				// TODO: I'm not sure if this action is relevant at this level of the system
				//return ConceptHarvestService.this.getDataPage(keywords, conceptTypes, pageNumber, pageSize);
				return new ArrayList<BeaconConcept>();
			}
		};
	}

	public List<ServerAnnotation> harvestEvidence(String statementId, String keywords, Integer pageNumber,
			Integer pageSize, List<String> beacons, String sessionId) {

		List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();

		try {
			
			CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconAnnotation>>> future = 
					kbs.getEvidence(statementId, keywords, pageNumber, pageSize, beacons, sessionId);
			
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconAnnotation>
			> evidence = waitFor(future,weightedTimeout(beacons, pageSize));
			
			for (KnowledgeBeacon beacon : evidence.keySet()) {
				for (BeaconAnnotation reference : evidence.get(beacon)) {
					ServerAnnotation translation = ModelConverter.convert(reference, ServerAnnotation.class);
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
			
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
	}

	return responses;
}
