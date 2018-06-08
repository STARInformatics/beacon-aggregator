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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.Util;
import bio.knowledge.aggregator.ConceptCategoryService;
import bio.knowledge.aggregator.Curie;
import bio.knowledge.aggregator.harvest.QueryUtil;
import bio.knowledge.database.repository.AnnotationRepository;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.EvidenceRepository;
import bio.knowledge.database.repository.ReferenceRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.Annotation;
import bio.knowledge.model.EvidenceCode;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;
import bio.knowledge.model.neo4j.Neo4jAnnotation;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jConceptDetail;
import bio.knowledge.model.neo4j.Neo4jEvidence;
import bio.knowledge.model.neo4j.Neo4jReference;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import bio.knowledge.server.model.ServerCliquesQuery;
import bio.knowledge.server.model.ServerCliquesQueryResult;
import bio.knowledge.server.model.ServerCliquesQueryStatus;
import bio.knowledge.server.model.ServerConceptDetail;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptWithDetailsBeaconEntry;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;
import bio.knowledge.server.model.ServerStatementsQuery;
import bio.knowledge.server.model.ServerStatementsQueryResult;
import bio.knowledge.server.model.ServerStatementsQueryStatus;

/**
 * This class manages the KBA Blackboard which is, in essence, 
 * accessing a graph database of cached retrieved concepts and relationships.
 * 
 * If requested concepts and relationship statements are not yet detected
 * in the graph database, then a query is triggered to harvest such data 
 * from onto the Knowledge Beacon network.
 * 
 * @author richard
 *
 */
@Service
public class Blackboard implements Curie, QueryUtil, Util {
	
	public static final String NO_CLIQUE_FOUND_WARNING = "WARN: Could not build clique - are you sure inputId exists in at least one of the beacons?";

	@Autowired private QueryRegistry queryRegistry;
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	
	@Autowired private BeaconHarvestService beaconHarvestService;
	
	@Autowired private ConceptRepository conceptRepository;
	@Autowired private ConceptCategoryService conceptTypeService;
	
	@Autowired private EvidenceRepository evidenceRepository;
	@Autowired private AnnotationRepository annotationRepository;
	@Autowired private ReferenceRepository referenceRepository;
	@Autowired private BeaconRepository beaconRepository;

	/**
	 * 
	 * @param queryId
	 * @return
	 */
	public boolean isActiveQuery(String queryId) {
		return queryRegistry.isActiveQuery(queryId);
	}
	
/******************************** CONCEPT Data Access *************************************/
	
	/**
	 * 
	 * @param queryId
	 * @param keywords
	 * @param conceptTypes
	 * @param beacons
	 * @throws BlackboardException
	 */
	public ServerConceptsQuery initiateConceptsQuery(
			List<String> keywords, 
			List<String> conceptTypes, 
			List<Integer> beacons
	) throws BlackboardException {
		
		try {
			ConceptsQuery query = (ConceptsQuery) queryRegistry.createQuery(QueryRegistry.QueryType.CONCEPTS);
			ServerConceptsQuery scq = query.getQuery(keywords, conceptTypes, beacons);
			return scq;
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
	}


	/**
	 * 
	 * @param queryId
	 * @param beacons
	 * @return
	 */
	public ServerConceptsQueryStatus 
					getConceptsQueryStatus(
							String queryId, 
							List<Integer> beacons
	) throws BlackboardException {
		try {
			ConceptsQuery query = (ConceptsQuery) queryRegistry.lookupQuery(queryId);
			ServerConceptsQueryStatus queryStatus = query.getQueryStatus(beacons);
			return queryStatus;

		} catch (Exception e) {
			throw new BlackboardException(e);
		}
	}

	/**
	 * 
	 * @param queryId
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @return
	 * @throws BlackboardException
	 */
	public ServerConceptsQueryResult 
					retrieveConceptsQueryResults(
							String queryId, 
							Integer pageNumber, 
							Integer pageSize,
							List<Integer> beacons
	) throws BlackboardException {
		try {
			ConceptsQuery query = (ConceptsQuery) queryRegistry.lookupQuery(queryId);
			ServerConceptsQueryResult results = query.getQueryResults(pageNumber,pageSize,beacons);
			return results;
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
	}
	
	
	public ServerCliquesQuery initiateCliquesQuery(List<String> identifiers, List<Integer> beacons) 
		throws BlackboardException {
		try {
			CliquesQuery query = (CliquesQuery) queryRegistry.createQuery( QueryRegistry.QueryType.CLIQUES);
			ServerCliquesQuery scq = query.getQuery(identifiers, beacons);
			return scq;
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
	}
	
	public ServerCliquesQueryStatus getCliquesQueryStatus(String queryId)
		throws BlackboardException {
		try {
			CliquesQuery query = (CliquesQuery) queryRegistry.lookupQuery(queryId);
			ServerCliquesQueryStatus queryStatus = query.getQueryStatus();
			return queryStatus;
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
	}
	
	public ServerCliquesQueryResult retrieveCliquesQueryResults (String queryId) 
		throws BlackboardException {
		try {
			CliquesQuery query = (CliquesQuery) queryRegistry.lookupQuery(queryId);
			ServerCliquesQueryResult results = query.getQueryResults();
			return results;
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
	}
		


	/**
	 * 
	 * @param identifier
	 * @param queryId
	 * @return
	 */
	public ServerCliqueIdentifier getClique( String identifier ) throws BlackboardException {
		
		ServerCliqueIdentifier cliqueId = null;
		
		try {
			
			Neo4jConceptClique clique = 
					exactMatchesHandler.getConceptCliqueFromDb(new String[] { identifier });
			
			if(clique!=null) {
				cliqueId = new ServerCliqueIdentifier();
				cliqueId.setCliqueId(clique.getId());
				cliqueId.setInputId(identifier);
			}
		
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return cliqueId;
	}
	
	/**
	 * Builds clique and returns its cliqueId or Optional.empty() if doesn't exist
	 * @param identifier we want to build a clique from
	 * @return a cliqueId of clique built from finding exactmatches on beacons or warning message in the cliqueId if clique
	 * could not be built 
	 */
	public Optional<ServerCliqueIdentifier> buildCliqueFromBeaconsOrCreateErrorResponse(String identifier) {
		Optional<Neo4jConceptClique> optional = 
				exactMatchesHandler.compileConceptCliqueFromBeacons(
						identifier,identifier,BiolinkTerm.NAMED_THING.getLabel()
				);
		
		ServerCliqueIdentifier cliqueId = new ServerCliqueIdentifier();
		cliqueId.setInputId(identifier);
		
		if (optional.isPresent()) {
			Neo4jConceptClique clique = optional.get();
			cliqueId.setCliqueId(clique.getId());

			return Optional.of(cliqueId);
		} else {
			return Optional.empty();
		}
		
	}
	
	/**
	 * 
	 * @param clique
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public  ServerConceptWithDetails getConceptDetails(
			String cliqueId, 
			List<Integer> beacons
	) throws BlackboardException {
	
		ServerConceptWithDetails concept = null;
		
		try {
			concept = getConceptsWithDetailsFromDatabase(
							cliqueId,
							beacons
			);
			
			if (concept == null) {
				Neo4jConceptClique clique = exactMatchesHandler.getClique(cliqueId);

				if(clique==null) 
					throw new RuntimeException("harvestConceptsBeaconDetails(): clique with ID '"+cliqueId+"' could not be found?") ;
				
				concept = new ServerConceptWithDetails();
				concept.setClique(cliqueId);
				concept.setType(clique.getConceptCategory());
				concept.setAliases(clique.getConceptIds());
				concept.setName(clique.getSuperName());
			}
			
	    	if (concept.getEntries().isEmpty()) {
	    		
	    		concept = beaconHarvestService.harvestConceptsBeaconDetails(concept, beacons);

	    		addConceptsWithDetailsToDatabase(concept);

	    	} 		
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return concept;
	}

	private void addConceptsWithDetailsToDatabase(ServerConceptWithDetails concept) {
			
		Neo4jConcept neo4jConcept = conceptRepository.getByClique(concept.getClique());
		
		if (neo4jConcept == null) {
			neo4jConcept = new Neo4jConcept();
		}
		
		Neo4jConceptClique clique = exactMatchesHandler.getClique(concept.getClique());
		neo4jConcept.setClique(clique);
		
		neo4jConcept.setName(concept.getName());
		
		for (ServerConceptWithDetailsBeaconEntry e : concept.getEntries()) {
			
			for (ServerConceptDetail d : e.getDetails()) {
				
				Neo4jConceptDetail detail = new Neo4jConceptDetail();
				
				detail.setKey(d.getTag());
				detail.setValue(d.getValue());
				
				Neo4jKnowledgeBeacon neo4jBeacon = beaconRepository.getBeacon(e.getBeacon());
				
				if (neo4jBeacon == null) {
					neo4jBeacon = new Neo4jKnowledgeBeacon();
					neo4jBeacon.setBeaconId(e.getBeacon());
				}
				
				detail.setSourceBeacon(neo4jBeacon);
				
				neo4jConcept.addDetail(detail);
			}
		}
		
		/*  TODO: Fix concept type setting
		String type = concept.getType();
		if(type!=null) {
			String[] typenames = type.split("\\s");
			List<String> types = new ArrayList<String>();
			// TODO lookup and load types by typenames here?
			entry.setTypes(types);
		}
		*/
		
		neo4jConcept = conceptRepository.save(neo4jConcept);
	}

	private ServerConceptWithDetails getConceptsWithDetailsFromDatabase(String cliqueId, List<Integer> beacons) {
		
		Neo4jConceptClique clique = exactMatchesHandler.getClique(cliqueId);
		if(clique==null) return null; // non-existent concept clique being requested
		
		List<String> aliases = clique.getConceptIds();

		Neo4jConcept neo4jConcept = 
				conceptRepository.getByClique(cliqueId);
		
		if(neo4jConcept == null) return  null; // concept not found?
		
		ServerConceptWithDetails concept = new ServerConceptWithDetails();
		
		concept.setClique(neo4jConcept.getClique().getId());
		concept.setAliases(aliases);
		concept.setName(neo4jConcept.getName());
		
		Neo4jConceptCategory type = neo4jConcept.getType();
		
		if(type == null) {
			type = conceptTypeService.lookUpByIdentifier(clique.getConceptCategory());
		}
	
		concept.setType(type.getName());
		
		List<ServerConceptWithDetailsBeaconEntry> entries = 
				new ArrayList<ServerConceptWithDetailsBeaconEntry>();
		
		Map<Neo4jKnowledgeBeacon, List<ServerConceptDetail>> detailsMap = 
				new HashMap<Neo4jKnowledgeBeacon, List<ServerConceptDetail>>();
		
		for (Neo4jConceptDetail neo4jConceptDetail : neo4jConcept.iterDetails()) {
			
			ServerConceptDetail serverConceptDetail = new ServerConceptDetail();
			serverConceptDetail.setTag(neo4jConceptDetail.getKey());
			serverConceptDetail.setValue(neo4jConceptDetail.getValue());
			
			if (detailsMap.containsKey(neo4jConceptDetail.getSourceBeacon())) {
				detailsMap.get(neo4jConceptDetail.getSourceBeacon()).add(serverConceptDetail);
			} else {
				List<ServerConceptDetail> detailsByBeacon = new ArrayList<ServerConceptDetail>();
				detailsByBeacon.add(serverConceptDetail);
				detailsMap.put(neo4jConceptDetail.getSourceBeacon(), detailsByBeacon);
			}
		}
		
		for (Neo4jKnowledgeBeacon knowledgeBeacon : detailsMap.keySet()) {
			
			ServerConceptWithDetailsBeaconEntry entry = 
					new ServerConceptWithDetailsBeaconEntry();
			
			entry.setBeacon(knowledgeBeacon.getBeaconId());
			entry.setDetails(detailsMap.get(knowledgeBeacon));
		}
		
		concept.setEntries(entries);
		
		return concept;
	}

/******************************** STATEMENTS Data Access *************************************/

	/**
	 * 
	 * @param queryId
	 * @param source
	 * @param relations
	 * @param target
	 * @param keywords
	 * @param conceptTypes
	 * @param beacons
	 */
	public ServerStatementsQuery initiateStatementsQuery(
			String source, 
			List<String> relations, 
			String target, 
			List<String> keywords,
			List<String> conceptTypes, 
			List<Integer> beacons
	) throws BlackboardException {
		
		try {
			// Create new query instance
			StatementsQuery query = (StatementsQuery)
					queryRegistry.createQuery( QueryRegistry.QueryType.STATEMENTS );
	
			ServerStatementsQuery ssq = 
					query.getQuery(
							source, relations, target,
							keywords, conceptTypes,
							beacons
					);
			
			return ssq;
		
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
	}
	

	/**
	 * 
	 * @param queryId
	 * @param beacons
	 * @return
	 */
	public ServerStatementsQueryStatus 
					getStatementsQueryStatus(
							String queryId, 
							List<Integer> beacons
	) throws BlackboardException {

		try {
			StatementsQuery query = 
					(StatementsQuery) queryRegistry.lookupQuery(queryId);

			ServerStatementsQueryStatus queryStatus = query.getQueryStatus(beacons);

			return queryStatus;

		} catch(Exception e) {
			throw new BlackboardException(e);
		}

	}


	/**
	 * 
	 * @param queryId
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @return
	 * @throws BlackboardException
	 */
	public ServerStatementsQueryResult 
					retrieveStatementsQueryResults(
							String queryId, 
							Integer pageNumber,
							Integer pageSize, 
							List<Integer> beacons
							
	) throws BlackboardException {
		
		try {
			StatementsQuery query = 
					(StatementsQuery) queryRegistry.lookupQuery(queryId);
			
			// Create result wrapper
			ServerStatementsQueryResult results = 
					query.getQueryResults(pageNumber,pageSize,beacons);
			
			return results;
			
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
	}

	
/******************************** EVIDENCE Data Access *************************************/

	/**
	 * 
	 * @param statementId
	 * @param keywords
	 * @param pageNumber
	 * @param size
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public List<ServerAnnotation>  getEvidence(
					String statementId,
					List<String> keywords,
					Integer size,
					List<Integer> beacons
	) throws BlackboardException {
		
		List<ServerAnnotation> annotations = new ArrayList<ServerAnnotation>();
		
		try {
			/*
			 * Look for existing concepts cached within 
			 * the blackboard (Neo4j) database
			 */
			annotations = getEvidenceFromDatabase(
					statementId,
					keywords,
					size,
					beacons
			);
			/*
			 *  If none found, consult the the Beacon network
			 */
	    	if (annotations.isEmpty()) {
	    		
	    		annotations = beaconHarvestService.harvestEvidence(
	    				statementId,
	    				keywords,
	    				size,
	    				beacons
	    		);
	    		
	    		addEvidenceToDatabase(statementId, annotations);
	    		
	    		annotations = getEvidenceFromDatabase(
	    				statementId,
	    				keywords,
	    				size,
	    				beacons
	    		);
	    	}

		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return annotations;
	}

	/*
	 * This method saves Evidence to the local Neo4j cache database
	 * TODO: we need to carefully review the current data models for Evidence
	 */
	private void addEvidenceToDatabase(String statementId, List<ServerAnnotation> serverAnnotations) {
		
		Neo4jEvidence entry = evidenceRepository.findByEvidenceId(statementId);

		if(entry==null) {
			/*
			 * This is probably best considered a fatal error (for now) 
			 * until we get more experience with a wider range of knowledge sources
			 */
			throw new RuntimeException("Null Evidence entry for statementId: '"+statementId+"'?");
		}
		
		Set<Annotation> annotations = entry.getAnnotations();
		for(ServerAnnotation sa : serverAnnotations) {
			
			Neo4jReference reference = new Neo4jReference() ;
			reference.setId(sa.getId());
			reference.setName(sa.getLabel());
			reference.parseDatePublished(sa.getDate());
			reference = referenceRepository.save(reference);
			
			Neo4jAnnotation annotation = new Neo4jAnnotation( 
				sa.getId(), 
				sa.getLabel(),
				// TODO: Source type? The Knowledge Beacon API doesn't really return this Semantic Medline legacy tag
				Annotation.Type.Title, 
				EvidenceCode.lookUp(sa.getEvidenceCode()), // EvidenceCode
				reference 
		    );
			
			// Lazy hack: I store the beaconId as the userId "source" of the annotation (for now)
			annotation.setUserId(Integer.toUnsignedString(sa.getBeacon()));
			
			annotationRepository.save(annotation);
			
			annotations.add(annotation);
			
			entry.incrementCount();
		}
		
		evidenceRepository.save(entry);
	}

	private List<ServerAnnotation> getEvidenceFromDatabase(
			String statementId, 
			List<String> keywords, 
			Integer pageSize,
			List<Integer> beacons
	) {		
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		/*
		 * This method assumes that the Evidence is 
		 * already in the database, which is probably isn't (yet)!
		 */
		List<Map<String, Object>> evidence = evidenceRepository.getEvidenceByIdAndKeywords(
				statementId,
				keywords,
				pageSize
		);
		
		List<ServerAnnotation> annotations = new ArrayList<ServerAnnotation>();
		
		for(Map<String,Object> eMap : evidence) {
			
			ServerAnnotation citation = new ServerAnnotation();
			Neo4jAnnotation annotation = (Neo4jAnnotation)eMap.get("annotation");

			citation.setId(annotation.getUserId());
			citation.setLabel(annotation.getName());
			citation.setEvidenceCode(annotation.getEvidenceCode().getLabel());

			Long year  = (Long)eMap.get("year");
			Long month = (Long)eMap.get("month");
			Long day   = (Long)eMap.get("day");
			
			LocalDate date = LocalDate.of(
								year!=null?year.intValue():0, 
								month!=null?month.intValue():0, 
								day!=null?day.intValue():0
							);
			String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
			citation.setDate(dateString);
			
			Integer beaconId;
			try {
				beaconId = Integer.parseInt(annotation.getUserId());
			} catch (NumberFormatException nfe) {
				beaconId = 0;
			}
			citation.setBeacon(beaconId); 
			
			annotations.add(citation);
		}
		
		return annotations;
	}

	
	
}
