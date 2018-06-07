package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.CliquesQueryInterface;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerClique;

@Component
public class CliquesDatabaseInterface 
	extends CoreDatabaseInterface<CliquesQueryInterface, Neo4jConceptClique, ServerClique> {
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	@Autowired private ConceptCliqueRepository cliqueRepository;

	/**
	 * Currently does nothing since harvestAndSaveData both gets and loads the results
	 */
	@Override
	public void loadData(QuerySession<CliquesQueryInterface> query, List<Neo4jConceptClique> results,
			Integer beaconId) {
		// TODO: uncouple finding and saving in cliques methods
		
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
			} else {
				cliqueId.setCliqueId(Blackboard.NO_CLIQUE_FOUND_WARNING);
			}
			
			cliqueIds.add(cliqueId);
		}
		
		return cliqueIds;
	}

	@Override
	public Integer getDataCount(QuerySession<CliquesQueryInterface> query, int beaconId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Neo4jConceptClique> harvestAndSaveData(List<String> identifiers) {
		List<Neo4jConceptClique> results = new ArrayList<>();
		for (String identifier : identifiers) {
			Neo4jConceptClique clique = 
					exactMatchesHandler.getConceptCliqueFromDb(new String[] { identifier });
			
			if (clique == null) {
				Optional<Neo4jConceptClique> optional = 
						exactMatchesHandler.compileConceptCliqueFromBeacons(
								identifier,identifier,BiolinkTerm.NAMED_THING.getLabel()
						);
				if (optional.isPresent()) {
					clique = optional.get();
				}
			}
			
			if (clique != null) {
				results.add(clique);
			}
			
		}
		
		return results;
	}

}
