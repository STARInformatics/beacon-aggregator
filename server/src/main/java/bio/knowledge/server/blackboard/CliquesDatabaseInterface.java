package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.CliquesQueryInterface;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerClique;

@Component
public class CliquesDatabaseInterface 
	extends CoreDatabaseInterface<CliquesQueryInterface, Neo4jConceptClique, ServerClique> {
	
	private static Logger _logger = LoggerFactory.getLogger(CliquesDatabaseInterface.class);

	@Autowired private ExactMatchesHandler exactMatchesHandler;
	@Autowired private ConceptCliqueRepository cliqueRepository;

	/**
	 * Currently does nothing since harvestAndSaveData both gets and loads the results
	 */
	@Override
	public void loadData(QuerySession<CliquesQueryInterface> query, List<Neo4jConceptClique> results,
			Integer beaconId) {
		
	}
	
	public List<Neo4jConceptClique> harvestAndSaveData(List<String> identifiers) {
		List<Neo4jConceptClique> results = exactMatchesHandler.createAndGetConceptCliques(identifiers);
		
		return results;
	}
	
	@Override
	public List<ServerClique> getDataPage(QuerySession<CliquesQueryInterface> query, List<Integer> beacons) {
		List<String> identifiers = query.getQuery().getKeywords();
		
		List<ServerClique> cliqueIds = new ArrayList<>();

		for (String identifier : identifiers) {
			ServerClique cliqueId = new ServerClique();
			cliqueId.setId(identifier);
			
			Neo4jConceptClique clique = cliqueRepository.getConceptCliqueByConceptId(identifier);
			if (clique != null) {
				cliqueId.setCliqueId(clique.getId());
				cliqueIds.add(cliqueId);
			} else {
				_logger.warn("no clique found for id: " + identifier);
			}
			
		}
		
		return cliqueIds;
	}

	/**
	 * Returns the number of cliques with query identifiers that are already in the database;
	 */
	@Override
	public Integer getDataCount(QuerySession<CliquesQueryInterface> query, int beaconId) {
		List<String> identifiers = query.getQuery().getKeywords();
		return cliqueRepository.getConceptCliques(identifiers).size();
	}


}
