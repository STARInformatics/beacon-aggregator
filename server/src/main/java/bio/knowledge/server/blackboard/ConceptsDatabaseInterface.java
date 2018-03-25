/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.BeaconConceptWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.ConceptCliqueService;
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
import bio.knowledge.model.umls.Category;
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
	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private ConceptRepository  conceptRepository;
	@Autowired private ConceptCliqueService conceptCliqueService;
	@Autowired private ExactMatchesHandler exactMatchesHandler;

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#loadData(java.lang.Object, java.util.List, java.lang.Integer)
	 */
	@Override
	public void loadData(QuerySession<ConceptsQueryInterface> query, List<BeaconConcept> results, Integer beacon) {
		// TODO: Transform newly discovered BeaconConcept results into ServerConcepts and Load them into the database
		// TODO: We need to remember to build Concept Cliques along the way!!!!
		
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
		
		Neo4jConcept neo4jConcept ;
		if (!conceptRepository.exists(cliqueId, queryString)) {
			neo4jConcept = conceptRepository.getByClique(cliqueId);
		} else {
			neo4jConcept = new Neo4jConcept();
			neo4jConcept.setClique(conceptWrapper.getClique());
		}

		ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getType());
		
		neo4jConcept.setName(concept.getName());
		if(conceptType!=null) {
			List<ConceptTypeEntry> types = new ArrayList<ConceptTypeEntry>();
			types.add(conceptType);
			neo4jConcept.setTypes(types);
		}

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
		 *  TODO: accessed concepts should be tagged by clique
		 */
		
		// TODO: retrieve and load the results here!
		// Should be a simple database query at this point
		// subject only to whether or not the given beacons have data?
		// should the user be warned if they ask for beacons that had error 
		// or are incomplete, or should it silently fail for such beacons?
		//return getConceptsFromDatabase(
		//		keywords, conceptTypes, 
		//		pageNumber, pageSize,
		//		beacons, queryString
		//);

		String queryString = query.makeQueryString();
		
		ConceptsQueryInterface conceptQuery = query.getQuery();
		
		String[] keywordsArray = split(conceptQuery.getKeywords());
		String[] conceptTypesArray = split(conceptQuery.getConceptTypes());

		List<Neo4jConcept> neo4jConcepts = 
				conceptRepository.getConceptsByKeywordsAndType(
						keywordsArray, conceptTypesArray, queryString,
						conceptQuery.getPageNumber(), conceptQuery.getPageSize()
				);

		List<ServerConcept> serverConcepts = new ArrayList<ServerConcept>();
		for (Neo4jConcept neo4jConcept : neo4jConcepts) {

			ServerConcept serverConcept = new ServerConcept();
			serverConcept.setName(neo4jConcept.getName());
			serverConcept.setClique(neo4jConcept.getClique());
			serverConcept.setType(neo4jConcept.getType().getName());

			ConceptClique ecc = 
					// I'm not sure why using getClique2() helps us here, in terms of performance
					//exactMatchesHandler.getClique2(neo4jConcept.getClique());
					exactMatchesHandler.getClique(neo4jConcept.getClique());
			
			String str = Category.OBJC.toString();
			if (neo4jConcept.getType() == Category.OBJC) {
				str = "";
			}

			String type = conceptCliqueService.fixConceptType(ecc, str);

			serverConcept.setType(type);
			serverConcepts.add(serverConcept);
		}
		return serverConcepts;
	}
}