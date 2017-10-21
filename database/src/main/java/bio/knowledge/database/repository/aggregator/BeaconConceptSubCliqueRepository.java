/**
 * 
 */
package bio.knowledge.database.repository.aggregator;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.aggregator.BeaconConceptSubClique;

/**
 * @author Richard
 *
 */
@Repository
public interface BeaconConceptSubCliqueRepository extends GraphRepository<BeaconConceptSubClique> {

}
