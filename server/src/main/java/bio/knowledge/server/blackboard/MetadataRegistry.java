/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import bio.knowledge.server.model.ServerConceptType;
import bio.knowledge.server.model.ServerPredicate;

/**
 * @author richard
 *
 */
@Component
public class MetadataRegistry {
	
	private Map<String,ServerConceptType> conceptTypes = new HashMap<String,ServerConceptType>();

	Map<String,ServerConceptType> getConceptTypes() {
		return conceptTypes;
	}
	
	private Map<String,ServerPredicate> predicates = new HashMap<String,ServerPredicate>();

	Map<String,ServerPredicate> getPredicates() {
		return predicates;
	}
}
