/**
 * 
 */
package bio.knowledge.aggregator;

import java.util.List;

import bio.knowledge.model.ConceptTypeEntry;

/**
 * @author Richard
 *
 */
public interface ConceptTypeUtil {
	
	public final String CURIE_DELIMITER = ",";
	
	default public String curieList(List<ConceptTypeEntry> types) {
		
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
