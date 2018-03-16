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
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.DatabaseInterface;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.server.model.ServerConcept;

/**
 * @author richard
 *
 */
@Component
public class ConceptsDatabaseInterface implements DatabaseInterface<BeaconConcept,ServerConcept> {
	
	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private ConceptRepository    conceptRepository;

	@Override
	public boolean cacheData(
			KnowledgeBeacon kb, 
			BeaconItemWrapper<BeaconConcept> beaconItemWrapper, 
			String queryString
	) {
		BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
		BeaconConcept concept = conceptWrapper.getItem();

		ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getType());
		Neo4jConcept neo4jConcept = new Neo4jConcept();
		
		neo4jConcept.setClique(conceptWrapper.getClique());
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

	@Override
	public List<ServerConcept> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize, String queryString) {
		return getConceptsFromDatabase(
				keywords, conceptTypes, 
				pageNumber, pageSize,
				beacons, queryString
		);
	}
}