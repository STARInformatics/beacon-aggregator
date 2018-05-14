/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import bio.knowledge.server.model.ServerConceptCategories;
import bio.knowledge.server.model.ServerPredicates;

/**
 * @author richard
 *
 */
@Component
public class MetadataRegistry {
	
	private Map<String,ServerConceptCategories> conceptTypes = new HashMap<String,ServerConceptCategories>();

	Map<String,ServerConceptCategories> getConceptTypesMap() {
		return conceptTypes;
	}
	
	private Map<String,ServerPredicates> predicates = new HashMap<String,ServerPredicates>();

	Map<String,ServerPredicates> getPredicatesMap() {
		return predicates;
	}
}
