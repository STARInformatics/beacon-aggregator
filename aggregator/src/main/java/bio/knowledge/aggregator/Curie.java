/**
 * 
 */
package bio.knowledge.aggregator;

import java.util.Set;

import bio.knowledge.model.ConceptTypeEntry;

/**
 * @author Richard
 *
 */
public interface Curie {
	
	public final String CURIE_DELIMITER = ",";
	
	default public String curieSet(Set<ConceptTypeEntry> types) {
		
		String curies = "";
		if(!types.isEmpty()) {
			for(ConceptTypeEntry type : types) {
				if(type==null) continue; // probably a bug but code to ignore
				if(curies.isEmpty())
					curies = type.getCurie();
				else
					curies += CURIE_DELIMITER+type.getCurie();
			}
		}
		return curies;
	}
}
