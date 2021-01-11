/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import bio.knowledge.server.model.ServerConceptCategory;
import bio.knowledge.server.model.ServerPredicate;

/**
 * @author richard
 *
 */
@Component
public class MetadataRegistry {
	
	private Map<String,ServerConceptCategory> conceptCategories = new HashMap<String,ServerConceptCategory>();

	Map<String,ServerConceptCategory> getConceptCategoriesMap() {
		return conceptCategories;
	}
	
	private Map<String,ServerPredicate> predicates = new HashMap<String,ServerPredicate>();

	Map<String,ServerPredicate> getPredicatesMap() {
		return predicates;
	}
}
