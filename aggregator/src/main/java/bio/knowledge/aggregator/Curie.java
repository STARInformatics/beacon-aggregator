/**
 * 
 */
package bio.knowledge.aggregator;

import java.util.Set;

import bio.knowledge.model.neo4j.Neo4jConceptCategory;

/**
 * @author Richard
 *
 */
public interface Curie {
	
	public final String CURIE_DELIMITER = ",";
	
	default public String curieSet(Set<Neo4jConceptCategory> types) {
		
		String curies = "";
		if(!types.isEmpty()) {
			for(Neo4jConceptCategory type : types) {
				if(type==null) continue; // probably a bug but code to ignore
				if(curies.isEmpty())
					curies = type.getId();
				else
					curies += CURIE_DELIMITER+type.getId();
			}
		}
		return curies;
	}
}
