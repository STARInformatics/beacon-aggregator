package bio.knowledge.aggregator.ontology;

import java.net.URI;
import java.net.URISyntaxException;
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

@Component
public class Ontology {
	
	@Autowired KnowledgeBeaconRegistry registry;
	
	private BeaconBiolinkModel biolinkModel;
	
	private ModelLookup<BiolinkClass> classLookup;
	private ModelLookup<BiolinkSlot> slotLookup;
	
	private InheritanceLookup<BiolinkClass> classInheritanceLookup;
	private InheritanceLookup<BiolinkSlot> slotInheritanceLookup;
	
	private final String DEFAULT_CLASS_LABEL = "named thing";
	
	private final Map<String, String> uriMapping = new HashMap<String, String>();
	
	@PostConstruct
	public void init() {
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
	
	public BiolinkClass getClassByName(BiolinkTerm biolinkTerm) {
		return getClassByName(biolinkTerm.getLabel());
	}
	
	public BiolinkClass getClassByName(String biolinkClassName) {
		return classLookup.getClassByName(biolinkClassName);
	}
	
	public BiolinkClass getDefault() {
		return classLookup.getClassByName(DEFAULT_CLASS_LABEL);
	}
	
	public Optional<BiolinkClass> lookUpByBeacon(int beaconId, String termId) {
		KnowledgeBeaconImpl beacon = registry.getBeaconById(beaconId);
		return getMapping(beacon.getUrl(), termId);
	}
	
	public Optional<BiolinkClass> getMapping(String namespace, String termId) {
		String curie;
		
		if(isUri(namespace)) {
			String prefix = uriMapping.get(namespace);
			curie = prefix + ":" + termId;
		} else {
			curie = namespace + ":" + termId;
		}
		
		BiolinkClass biolinkClass = classLookup.lookup(curie);
		
		if (biolinkClass != null) {
			return Optional.of(biolinkClass);
		} else {
			return Optional.empty();
		}
	}
	
	private static boolean isUri(String namespace) {
		try {
			return new URI(namespace.toLowerCase()) != null;
		} catch (URISyntaxException e) {
			return false;
		}
	}
	
}


















