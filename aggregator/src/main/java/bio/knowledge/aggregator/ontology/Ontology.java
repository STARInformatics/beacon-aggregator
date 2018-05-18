package bio.knowledge.aggregator.ontology;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.ontology.BeaconBiolinkModel;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkSlot;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.ontology.mapping.InheritanceLookup;
import bio.knowledge.ontology.mapping.ModelLookup;
import bio.knowledge.ontology.mapping.NameSpace;
import bio.knowledge.ontology.utils.Utils;

@Component
public class Ontology {
	
	@Autowired KnowledgeBeaconRegistry registry;
	
	private BeaconBiolinkModel biolinkModel;
	
	private ModelLookup<BiolinkClass> classLookup;
	private ModelLookup<BiolinkSlot> slotLookup;
	
	private InheritanceLookup<BiolinkClass> classInheritanceLookup;
	private InheritanceLookup<BiolinkSlot> slotInheritanceLookup;
	
	private final String DEFAULT_CATEGORY = "named thing";
	private final String DEFAULT_PREDICATE = "association";
	
	private final Map<String, String> uriMapping = new HashMap<String, String>();
	
	@PostConstruct
	private void init() {
		uriMapping.put("HTTP://RKB.NCATS.IO", NameSpace.BIOLINK.getPrefix());
		uriMapping.put("HTTP://GARBANZO.SULAB.ORG", NameSpace.WIKIDATA.getPrefix());
		uriMapping.put("HTTPS://BIOLINK-KB.NCATS.IO", NameSpace.BIOLINK.getPrefix());
		uriMapping.put("HTTPS://NDEX-KB.NCATS.IO", NameSpace.UMLSSG.getPrefix());
		
		Optional<BeaconBiolinkModel> optional = BeaconBiolinkModel.load();
		biolinkModel = optional.get();
		
		classInheritanceLookup = new InheritanceLookup<BiolinkClass>(biolinkModel.getClasses());
		slotInheritanceLookup = new InheritanceLookup<BiolinkSlot>(biolinkModel.getSlots());
		
		classLookup = new ModelLookup<BiolinkClass>(biolinkModel.getClasses(), classInheritanceLookup);
		slotLookup = new ModelLookup<BiolinkSlot>(biolinkModel.getSlots(), slotInheritanceLookup);
	}
	
	/**
	 * 
	 * @param biolinkTerm
	 * @return
	 */
	public Optional<BiolinkClass> getClassByName(BiolinkTerm biolinkTerm) {
		return getClassByName(biolinkTerm.getLabel());
	}
	
	/**
	 * 
	 * @param biolinkClassName
	 * @return
	 */
	public Optional<BiolinkClass> getClassByName(String biolinkClassName) {
		BiolinkClass biolinkClass = classLookup.getClassByName(biolinkClassName);
		return Optional.ofNullable(biolinkClass);
	}
	
	/**
	 * 
	 * @return
	 */
	public BiolinkClass getDefaultCategory() {
		return classLookup.getClassByName(DEFAULT_CATEGORY);
	}

	/**
	 * 
	 * @return
	 */
	public BiolinkClass getDefaultPredicate() {
		return classLookup.getClassByName(DEFAULT_PREDICATE);
	}

	/**
	 * 
	 * @param biolinkSlotName
	 * @return
	 */
	public Optional<BiolinkSlot> getSlotByName(String biolinkSlotName) {
		BiolinkSlot slot = slotLookup.getClassByName(biolinkSlotName);
		return Optional.ofNullable(slot);
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param termId
	 * @return
	 */
	public Optional<BiolinkClass> lookUpByBeacon(int beaconId, String termId) {
		KnowledgeBeaconImpl beacon = registry.getBeaconById(beaconId);
		return getMapping( beacon.getUrl(), termId );
	}
	
	/**
	 * 
	 * @param namespace
	 * @param termId
	 * @return
	 */
	public Optional<BiolinkClass> getMapping(String namespace, String termId) {
		
		BiolinkClass biolinkClass;
		String prefix;
		
		if( ! Utils.isCurie(termId)) {

			if(Utils.isUri(namespace)) {
				
				prefix = uriMapping.get(namespace.toUpperCase());
				termId = prefix + ":" + termId;
				
			} else {
				
				termId = namespace + ":" + termId;
			}
		}
		
		biolinkClass = classLookup.lookup(termId);
		
		if (biolinkClass != null) {
			return Optional.of(biolinkClass);
		} else {
			return Optional.empty();
		}
	}	
}
