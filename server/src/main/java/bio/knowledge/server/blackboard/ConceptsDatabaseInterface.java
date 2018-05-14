/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.Util;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.ConceptsQueryInterface;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.aggregator.BeaconCitationRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerConcept;

/**
 * @author richard
 *
 */
@Component
public class ConceptsDatabaseInterface 
		extends CoreDatabaseInterface<
					ConceptsQueryInterface,
					BeaconConcept,
					ServerConcept
				> 
		implements Util
{
	private static Logger _logger = LoggerFactory.getLogger(ConceptsDatabaseInterface.class);
	
	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private ConceptRepository conceptRepository;
	@Autowired private BeaconRepository beaconRepository;
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	@Autowired private BeaconCitationRepository beaconCitationRepository;
	
	/*
	 * MINOR ANXIETY ABOUT THIS PARTICULAR DATA ACCESS: 
	 * IS THERE ANY POSSIBILITY OF TWO THREADS 
	 * ACCESSING THE DATABASE IN INCONSISTENTLY? 
	 */	 
	 
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#loadData(java.lang.Object, java.util.List, java.lang.Integer)
	 * 
	 * Loads data into blackboard database
	 * For each concept found:
	 * - searches for its type and adds to set (of 1?) - defaults to BiolinkTerm.NAMED_THING if can't find Biolink type
	 * - retrieve or create associated ConceptClique
	 * - create Concept node on the database and fill with data (name, synonym, definition, types)
	 * 
	 */
	@Override
	public void loadData(QuerySession<ConceptsQueryInterface> query, List<BeaconConcept> results, Integer beaconId) {

		Neo4jKnowledgeBeacon beacon = beaconRepository.getBeacon(beaconId);

		for(BeaconConcept concept : results) {
			
			try {	
				
				// Resolve concept type(s)
				String typeString = concept.getCategory();
				Set<ConceptTypeEntry> conceptTypes = new HashSet<ConceptTypeEntry>();
				if( ! nullOrEmpty(typeString) ) {
					ConceptTypeEntry type = conceptTypeService.lookUp(beaconId,typeString);
					conceptTypes.add(type);
				}
				
				// Retrieve or create associated ConceptClique
				ConceptClique conceptClique = exactMatchesHandler.getExactMatches(
						beaconId,
						concept.getId(),
						concept.getName(),
						conceptTypes
				);
				
				// Retrieve Neo4jConcept by clique if exists, or create new Neo4jConcept
				String cliqueId = conceptClique.getId();
				
				Neo4jConcept neo4jConcept = conceptRepository.getByClique(cliqueId);
				
				Set<ConceptTypeEntry> types ;
				if(neo4jConcept != null) {
					types = conceptTypeService.getConceptTypesByClique(cliqueId);
				} else {
					neo4jConcept = new Neo4jConcept();
					neo4jConcept.setClique(cliqueId);
					types = neo4jConcept.getTypes();
				}
				
				types.addAll(conceptTypes);
				
				neo4jConcept.setName(concept.getName());
				neo4jConcept.setSynonyms(concept.getSynonyms());
				neo4jConcept.setDefinition(concept.getDefinition());
				
				/*
				 *  Keep track of this concept entry 
				 *  with the current QueryTracker.
				 *  Unfortunately, we don't yet track 
				 *  beacon-specific data associations
				 */
				neo4jConcept.addQuery(query.getQueryTracker());
				
				/*
				 * Add this beacon to the set of beacons 
				 * which have cited this concept
				 */
				Neo4jBeaconCitation citation = 
						beaconCitationRepository.findByBeaconAndObjectId(
													beacon.getBeaconId(),
													concept.getId()
												);
				if(citation==null) {
					citation = new Neo4jBeaconCitation(beacon,concept.getId());
					citation = beaconCitationRepository.save(citation);
				}
				neo4jConcept.addBeaconCitation(citation);

				// Save the new or updated Concept object
				conceptRepository.save(neo4jConcept);
				
			} catch(Exception e) {
				// I won't kill this loop here
				_logger.error(e.getMessage());
			}
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
		
		List<ServerConcept> serverConcepts = new ArrayList<ServerConcept>();
		
		try {
			/*
			 *  TODO: also need to filter beacons here against harvested list of beacons?
			 *  
			 *  QueryTracker queryTracker = query.getQueryTracker();
			 *  // check which beacon data is wanted here?
			 */
			
			// TODO: retrieve and load the results here!
			// Should be a simple database query at this point
			// subject only to whether or not the given beacons have data?
			// should the user be warned if they ask for beacons that had error 
			// or are incomplete, or should it silently fail for such beacons?
	
			//String queryString = query.makeQueryString();
			
			ConceptsQueryInterface conceptQuery = query.getQuery();
			
			String[] keywordsArray = split(conceptQuery.getKeywords());
			
			List<String> conceptTypes = conceptQuery.getConceptCategories();

			if(conceptTypes==null)
				conceptTypes = new ArrayList<String>();
	
			/*
			 * TODO: Fix this database retrieval call to reflect actual database contents
			 * Maybe ignore queryString (and beacons) for now(?)
			 */
			List<Neo4jConcept> dbConceptList = 
					conceptRepository.getConceptsByKeywordsAndType(
							keywordsArray, conceptTypes.toArray(new String[0]),
							conceptQuery.getPageNumber(), conceptQuery.getPageSize()
					);

			for (Neo4jConcept dbConcept : dbConceptList) {
				ServerConcept serverConcept = new ServerConcept();
				serverConcept.setName(dbConcept.getName());
				
				String cliqueId = dbConcept.getClique();
				serverConcept.setClique(cliqueId);
				
				// Collect the concept types
				Set<ConceptTypeEntry> types = conceptTypeService.getConceptTypesByClique(cliqueId);
				serverConcept.setType(ConceptTypeService.getString(types));
				
				serverConcepts.add(serverConcept);
			}
		} catch(Exception e) {
			// I won't kill this loop here
			_logger.error(e.getMessage());
		}
		
		return serverConcepts;
	}

	@Override
	public Integer getDataCount(QuerySession<ConceptsQueryInterface> query, int beaconId) {
		return conceptRepository.countQueryResults(query.makeQueryString(), beaconId);
	}
}
