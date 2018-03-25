/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.BeaconConceptWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.ConceptsQueryInterface;
import bio.knowledge.aggregator.DatabaseInterface;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerConcept;

/**
 * @author richard
 *
 */
@Component
public class ConceptsDatabaseInterface 
		implements DatabaseInterface<
						BeaconConcept,
						ServerConcept,
						ConceptsQueryInterface
					> 
{
	@Autowired private ConceptTypeService   conceptTypeService;
	@Autowired private ConceptRepository    conceptRepository;
	@Autowired private ExactMatchesHandler  exactMatchesHandler;

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#loadData(java.lang.Object, java.util.List, java.lang.Integer)
	 */
	@Override
	public void loadData(QuerySession<ConceptsQueryInterface> query, List<BeaconConcept> results, Integer beaconId) {

		for(BeaconConcept concept : results) {
			
			// Resolve concept type(s)
			// TODO: need to repair ConceptTypeService to be Biolink compliant!!
			String typeString = concept.getType();
			Set<ConceptTypeEntry> conceptTypes = 
					conceptTypeService.lookUp(beaconId,typeString);

			// Retrieve or create associated ConceptClique
			ConceptClique conceptClique = 
					exactMatchesHandler.getExactMatches(
							beaconId,
							concept.getId(),
							concept.getName(),
							conceptTypes
					);
			
			String cliqueId = conceptClique.getId();
			
			Neo4jConcept dbConcept = 
					conceptRepository.getByClique(cliqueId);
			
			if(dbConcept==null) {
				dbConcept = new Neo4jConcept();
				dbConcept.setClique(cliqueId);
			}
			
			dbConcept.setName(concept.getName());
			dbConcept.setTypes(conceptTypes);
			dbConcept.setSynonyms(concept.getSynonyms());
			dbConcept.setDefinition(concept.getDefinition());
			
			/* 
			 * TODO: Need to somehow better tag the harvested Concept by query and beacon provenance?
			 */
			String beaconQueryTag = beaconId+":"+query.makeQueryString();
			dbConcept.setQueryFoundWith(beaconQueryTag);

			conceptRepository.save(dbConcept);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#getDataPage(bio.knowledge.aggregator.QuerySession, java.util.List)
	 */
	@Override
	public List<ServerConcept> getDataPage(
				QuerySession<ConceptsQueryInterface> query, 
				List<Integer> beacons
	) {
		/*
		 *  TODO: also need to filter beacons here against default query list of beacons?
		 */
		
		// TODO: retrieve and load the results here!
		// Should be a simple database query at this point
		// subject only to whether or not the given beacons have data?
		// should the user be warned if they ask for beacons that had error 
		// or are incomplete, or should it silently fail for such beacons?

		String queryString = query.makeQueryString();
		
		ConceptsQueryInterface conceptQuery = query.getQuery();
		
		String[] keywordsArray = split(conceptQuery.getKeywords());
		
		String conceptTypes = conceptQuery.getConceptTypes();
		String[] conceptTypesArray;
		if(conceptTypes!=null && !conceptTypes.isEmpty())
			conceptTypesArray = split(conceptTypes);
		else
			conceptTypesArray = new String[0];

		/*
		 * TODO: Fix this database retrieval call to reflect actual database contents
		 * Maybe ignore queryString (and beacons) for now(?)
		 */
		List<Neo4jConcept> dbConceptList = 
				conceptRepository.getConceptsByKeywordsAndType(
						keywordsArray, conceptTypesArray, queryString,
						conceptQuery.getPageNumber(), conceptQuery.getPageSize()
				);

		List<ServerConcept> serverConcepts = new ArrayList<ServerConcept>();
		for (Neo4jConcept dbConcept : dbConceptList) {
			ServerConcept serverConcept = new ServerConcept();
			serverConcept.setName(dbConcept.getName());
			serverConcept.setClique(dbConcept.getClique());
			serverConcept.setType(dbConcept.getType().getName());
			serverConcepts.add(serverConcept);
		}
		
		return serverConcepts;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#cacheData(bio.knowledge.aggregator.KnowledgeBeacon, bio.knowledge.aggregator.BeaconItemWrapper, java.lang.String)
	 */
	@Override
	public boolean cacheData(
			KnowledgeBeacon kb, 
			BeaconItemWrapper<BeaconConcept> beaconItemWrapper, 
			String queryString
	) {
		BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
		BeaconConcept concept = conceptWrapper.getItem();

		String cliqueId = conceptWrapper.getClique();
		
		Boolean exists = conceptRepository.exists(cliqueId, queryString);
		
		Neo4jConcept neo4jConcept ;
		if (!exists) {
			neo4jConcept = conceptRepository.getByClique(cliqueId);
		} else {
			neo4jConcept = new Neo4jConcept();
			neo4jConcept.setClique(cliqueId);
		}

		ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getType());
		
		neo4jConcept.setName(concept.getName());
		if(conceptType!=null) {
			Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();
			types.add(conceptType);
			neo4jConcept.setTypes(types);
		}

		neo4jConcept.setQueryFoundWith(queryString);
		neo4jConcept.setSynonyms(concept.getSynonyms());
		neo4jConcept.setDefinition(concept.getDefinition());

		conceptRepository.save(neo4jConcept);
		
		if (!exists) {
			return true;
		} else {
			return false;
		}
	}
}