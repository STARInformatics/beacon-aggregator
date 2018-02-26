/**
 * 
 */
package bio.knowledge.ontology.mapping;

import java.util.HashMap;

/**
 * @author richard
 *
 */
public class BeaconBiolinkMappingIndex extends HashMap<String, BiolinkModelMapping> {

	private static final long serialVersionUID = -8922021938416438973L;
	
	private final UMLSBiolinkMapping     umlsBiolinkMapping     = new UMLSBiolinkMapping();
	private final WikiDataBiolinkMapping wikidataBiolinkMapping = new WikiDataBiolinkMapping();
	
	public BeaconBiolinkMappingIndex() {
		put("1",  umlsBiolinkMapping);
		put("2",  wikidataBiolinkMapping);
		put("5",  umlsBiolinkMapping);
		put("11", umlsBiolinkMapping);
	}

}
