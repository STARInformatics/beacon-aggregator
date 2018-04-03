/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import bio.knowledge.server.model.ServerConceptTypes;
import bio.knowledge.server.model.ServerPredicates;

/**
 * @author richard
 *
 */
@Component
public class MetadataRegistry {
	
	private Map<String,ServerConceptTypes> conceptTypes = new HashMap<String,ServerConceptTypes>();

	Map<String,ServerConceptTypes> getConceptTypesMap() {
		return conceptTypes;
	}
	
	private Map<String,ServerPredicates> predicates = new HashMap<String,ServerPredicates>();

	Map<String,ServerPredicates> getPredicatesMap() {
		return predicates;
	}
}
