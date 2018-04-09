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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.Util;
import bio.knowledge.aggregator.Curie;
import bio.knowledge.aggregator.harvest.QueryUtil;
import bio.knowledge.database.repository.AnnotationRepository;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.EvidenceRepository;
import bio.knowledge.database.repository.ReferenceRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.Annotation;
import bio.knowledge.model.EvidenceCode;
import bio.knowledge.model.Neo4jConceptDetail;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.neo4j.Neo4jAnnotation;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jEvidence;
import bio.knowledge.model.neo4j.Neo4jReference;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerCliqueIdentifier;
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
	
	@Autowired private QueryRegistry queryRegistry;
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	
	@Autowired private BeaconHarvestService beaconHarvestService;
	
	@Autowired private ConceptRepository conceptRepository;
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
			String keywords, 
			List<String> conceptTypes, 
			List<Integer> beacons
	) throws BlackboardException {
		
		try {
			// Create new Query Registry entry
			ConceptsQuery query = (ConceptsQuery)
					queryRegistry.createQuery( QueryRegistry.QueryType.CONCEPTS );
	
			// Initiate and return the query
			ServerConceptsQuery scq = 
					query.getQuery( keywords, conceptTypes, beacons );

			return scq;
		
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
	public ServerConceptsQueryStatus 
					getConceptsQueryStatus(
							String queryId, 
							List<Integer> beacons
	) throws BlackboardException {
		try {
			
			ConceptsQuery query = 
					(ConceptsQuery) queryRegistry.lookupQuery(queryId);
			
			ServerConceptsQueryStatus queryStatus = query.getQueryStatus(beacons);
			
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
	public ServerConceptsQueryResult 
					retrieveConceptsQueryResults(
							String queryId, 
							Integer pageNumber, 
							Integer pageSize,
							List<Integer> beacons
	) throws BlackboardException {
		
		try {
			
			ConceptsQuery query = 
					(ConceptsQuery) queryRegistry.lookupQuery(queryId);
			
			ServerConceptsQueryResult results = query.getQueryResults(pageNumber,pageSize,beacons);
			
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
			
			ConceptClique clique = 
					exactMatchesHandler.getConceptCliqueFromDb(new String[] { identifier });
			
			if(clique!=null) {
				cliqueId = new ServerCliqueIdentifier();
				cliqueId.setCliqueId(clique.getId());
			}
		
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return cliqueId;
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
				throw new RuntimeException("cliqueId for given beacon not in database");
			}
			
	    	if (concept.getEntries().isEmpty()) {
	    		
	    		concept = beaconHarvestService.harvestConceptsWithDetails(
	    					cliqueId,
	    	    			beacons
	    	    );

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
		
		neo4jConcept.setClique(concept.getClique());
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
		
		conceptRepository.save(neo4jConcept);
	}

	private ServerConceptWithDetails getConceptsWithDetailsFromDatabase(String cliqueId, List<Integer> beacons) {
		
		/*
		 * TODO: the 'getByClique()' needs to be fleshed out out to return a fully detailed object
		 */
		Neo4jConcept neo4jConcept = 
				conceptRepository.getByClique(cliqueId);
		
		if(neo4jConcept == null) return  null;
		
		ServerConceptWithDetails concept = new ServerConceptWithDetails();
		
		// TODO: fix BeaconConcept to include a proper clique?
		concept.setClique(neo4jConcept.getClique());
		
		concept.setName(neo4jConcept.getName());
		
		// TODO: fix BeaconConcept to track data type?
		concept.setType(neo4jConcept.getType().getLabel());
		
		List<ServerConceptWithDetailsBeaconEntry> entries = new ArrayList<ServerConceptWithDetailsBeaconEntry>();
		
		Map<Neo4jKnowledgeBeacon, List<ServerConceptDetail>> detailsMap = new HashMap<Neo4jKnowledgeBeacon, List<ServerConceptDetail>>();
		
		for (Neo4jConceptDetail neo4jConceptDetail : neo4jConcept.iterDetails()) {
			ServerConceptDetail serverConceptDetail = new ServerConceptDetail();
			serverConceptDetail.setTag(neo4jConceptDetail.getKey());
			serverConceptDetail.setValue(neo4jConceptDetail.getValue());
			
			if (detailsMap.containsKey(neo4jConceptDetail.getSourceBeacon())) {
				detailsMap.get(neo4jConceptDetail.getSourceBeacon()).add(serverConceptDetail);
			} else {
				List<ServerConceptDetail> l = new ArrayList<ServerConceptDetail>();
				l.add(serverConceptDetail);
				detailsMap.put(neo4jConceptDetail.getSourceBeacon(), l);
			}
		}
		
		for (Neo4jKnowledgeBeacon knowledgeBeacon : detailsMap.keySet()) {
			ServerConceptWithDetailsBeaconEntry entry = new ServerConceptWithDetailsBeaconEntry();
			
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
			String keywords,
			List<String> conceptTypes, 
			List<Integer> beacons
	) throws BlackboardException {
		
		try {
			// Create new query instance
			StatementsQuery query = (StatementsQuery)
					queryRegistry.createQuery( QueryRegistry.QueryType.STATEMENTS );
	
			ServerStatementsQuery ssq = 
					query.getQuery(
							source,relations,target,
							keywords,conceptTypes,
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
	 * @param pageSize
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public List<ServerAnnotation>  getEvidence(
					String statementId,
					String keywords,
					Integer pageNumber,
					Integer pageSize,
					List<Integer> beacons
	) throws BlackboardException {
		
		List<ServerAnnotation> annotations = new ArrayList<ServerAnnotation>();
		
		try {
			/*
			 * Look for existing concepts cached within 
			 * the blackboard (Neo4j) database
			 */
			annotations = 
					getEvidenceFromDatabase(
										statementId, keywords,
										pageNumber, pageSize,
										beacons
					);
			/*
			 *  If none found, consult the the Beacon network
			 */
	    	if (annotations.isEmpty()) {
	    		
	    		// Harvest the beacons...
	    		annotations = 
	    				beaconHarvestService.harvestEvidence(
				    					statementId, keywords,
			    	    				pageNumber, pageSize,
			    	    				beacons
	    	    			);
	    		
	    		// ...load the new data...
	    		addEvidenceToDatabase(statementId, annotations);
	    		
	    		// then try again to retrieve it!
	    		annotations = 
						getEvidenceFromDatabase(
											statementId, keywords,
											pageNumber, pageSize,
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
		Integer count = 0;
		for(ServerAnnotation serverAnnotation : serverAnnotations) {
			
			Neo4jReference reference = new Neo4jReference() ;
			// populate reference here...
			referenceRepository.save(reference);
			
			Neo4jAnnotation annotation = new Neo4jAnnotation( 
				serverAnnotation.getId(), 
				serverAnnotation.getLabel(),
				Annotation.Type.Title, // TODO: Source type = not really sure here... The Knowledge Beacon API doesn't really return this
				EvidenceCode.lookUp(serverAnnotation.getType()), // EvidenceCode
				reference 
		    );
			
			// Lazy hack: I store the beaconId as the userId "source" of the annotation (for now)
			annotation.setUserId(Integer.toUnsignedString(serverAnnotation.getBeacon()));
			
			annotationRepository.save(annotation);
			annotations.add(annotation);
			
			count++;
		}
		
		entry.setCount(count);
		
		evidenceRepository.save(entry);
	}

	private List<ServerAnnotation> getEvidenceFromDatabase(
			String statementId, 
			String keywords, 
			Integer pageNumber, Integer pageSize, 
			List<Integer> beacons
	) {
		//String queryString = makeQueryString("evidence", statementId, keywords);
		
		String[] keywordArray = keywords != null ? keywords.split(" ") : null;
		
		pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 1;
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		/*
		 * This method assumes that the Evidence is 
		 * already in the database, which is probably isn't (yet)!
		 */
		List<Map<String, Object>> evidence = 
				evidenceRepository.getEvidenceByIdAndKeywords(
						statementId, keywordArray,
						pageNumber,pageSize
				);
		
		List<ServerAnnotation> annotations = new ArrayList<ServerAnnotation>();
		
		for(Map<String,Object> eMap : evidence) {
			
			ServerAnnotation citation = new ServerAnnotation();
			Neo4jAnnotation annotation = (Neo4jAnnotation)eMap.get("annotation");

			citation.setId(annotation.getUserId());
			citation.setLabel(annotation.getName());
			citation.setType(annotation.getEvidenceCode().getLabel());

			Integer year  = (Integer)eMap.get("year");
			Integer month = (Integer)eMap.get("month");
			Integer day   = (Integer)eMap.get("day");
			Calendar date = new Calendar.Builder().setDate(year, month, day).build();

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			citation.setDate(dateFormat.format(date.getTime()));
			
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
