/**
 * 
 */
package bio.knowledge.ontology.mapping;

import java.util.HashMap;
import java.util.Optional;

/**
 * @author richard
 *
 */
public class BeaconBiolinkMappingIndex extends HashMap<String, BiolinkModelMapping> {

	private static final long serialVersionUID = -8922021938416438973L;
	
	private final DirectBiolinkMapping directBiolinkMapping     = new DirectBiolinkMapping();
	private final UMLSBiolinkMapping     umlsBiolinkMapping     = new UMLSBiolinkMapping();
	private final WikiDataBiolinkMapping wikidataBiolinkMapping = new WikiDataBiolinkMapping();
	
	public BeaconBiolinkMappingIndex() {
		
		// By Beacon Id (used as a String key)
		put("1",  directBiolinkMapping);
		put("2",  wikidataBiolinkMapping);
		put("5",  umlsBiolinkMapping);
		put("11", umlsBiolinkMapping);

	
		put(NameSpace.BIOLINK.getPrefix(),  directBiolinkMapping);
		put(NameSpace.WIKIDATA.getPrefix(), wikidataBiolinkMapping);
		put(NameSpace.UMLSSG.getPrefix(),   umlsBiolinkMapping);
		put(NameSpace.BIOPAX.getPrefix(),   umlsBiolinkMapping);
		put(NameSpace.NDEXBIO.getPrefix(),  umlsBiolinkMapping);
	}

	private static BeaconBiolinkMappingIndex beaconMappingIndex = new BeaconBiolinkMappingIndex();
	
	/**
	 * 
	 * @param namespace
	 * @param termId
	 * @return
	 */
	public static Optional<String> getMapping (String namespace, String termId ) {
		if(beaconMappingIndex.containsKey(namespace)) {
			BiolinkModelMapping bmm = beaconMappingIndex.get(namespace);
			if(bmm.containsKey(termId)) {
				return Optional.of(bmm.get(termId));
			}
		}
		return Optional.empty();
	}

}
