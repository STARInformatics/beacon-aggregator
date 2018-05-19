package bio.knowledge.aggregator.ontology;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.ontology.BeaconBiolinkModel;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkEntityInterface;
import bio.knowledge.ontology.BiolinkSlot;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.ontology.mapping.InheritanceLookup;
import bio.knowledge.ontology.mapping.ModelLookup;
import bio.knowledge.ontology.mapping.NameSpace;
import bio.knowledge.ontology.utils.Utils;

@Component
public class Ontology {
	
	private static Logger _logger = LoggerFactory.getLogger(Ontology.class);
	
	@Autowired KnowledgeBeaconRegistry registry;
	
	private BeaconBiolinkModel biolinkModel;
	
	private ModelLookup<BiolinkClass> classLookup;
	private ModelLookup<BiolinkSlot> slotLookup;
	
	private InheritanceLookup<BiolinkClass> classInheritanceLookup;
	private InheritanceLookup<BiolinkSlot> slotInheritanceLookup;
	
	private final String DEFAULT_CATEGORY = "named thing";
	private final String DEFAULT_PREDICATE = "related to";
	
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
	public BiolinkSlot getDefaultPredicate() {
		return slotLookup.getClassByName(DEFAULT_PREDICATE);
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
	 * @param category
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Optional<BiolinkClass> lookUpCategoryByBeacon(
			Integer beaconId, 
			String category
	) {
		return (Optional<BiolinkClass>)lookUpByBeacon(beaconId,category,classLookup);
	}
	
	
	/**
	 * 
	 * @param beaconId
	 * @param category
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Optional<BiolinkSlot> lookUpPredicateByBeacon(
			Integer beaconId, 
			String category
	) {
		return (Optional<BiolinkSlot>)lookUpByBeacon(beaconId,category,slotLookup);
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param termId
	 * @return
	 */
	public Optional<? extends BiolinkEntityInterface> lookUpByBeacon(
			Integer beaconId, 
			String termId,  
			ModelLookup<? extends BiolinkEntityInterface> modelLookup
	) {
		KnowledgeBeaconImpl beacon = registry.getBeaconById(beaconId);
		return getMapping( beacon.getUrl(), termId, modelLookup );
	}
	
	/**
	 * 
	 * @param namespace
	 * @param termId
	 * @param modelLookup
	 * @return
	 */
	public Optional<BiolinkEntityInterface> getMapping( 
			String namespace, 
			String termId,  
			ModelLookup<? extends BiolinkEntityInterface> modelLookup
		) {
		
		BiolinkEntityInterface biolinkTerm;
		String prefix;
		String curie;
		
		if( Utils.isCurie(termId)) {
			
			// Sanity check: make sure that the Curie is uniformly upper case
			curie = termId.toUpperCase();
			
		} else {
			// doesn't (yet) look like a Curie..but try to synthesize one
			
			if(Utils.isUri(namespace)) {
				
				prefix = uriMapping.get(namespace.toUpperCase());
				curie = prefix + ":" + termId;
				
			} else {
				
				curie = namespace + ":" + termId;
			}
		}
		
		biolinkTerm = modelLookup.lookup(curie);
		
		if (biolinkTerm != null) {
			return Optional.of(biolinkTerm);
		} else {
			_logger.warn("Ontology.getMapping(termId: '"+termId+"') has no Biolink Mapping?");
			return Optional.empty();
		}
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param id
	 * @param category
	 * @return
	 */
	public BiolinkClass lookupCategory( Integer beaconId, String id, String category ) {
		
		BiolinkClass biolinkClass = 
				(BiolinkClass)lookupTerm( 
						beaconId, 
						id, 
						category, 
						classLookup,
						(String s)-> getClassByName(s) 
		) ;
		
		if(biolinkClass==null)
			
			_logger.warn("Ontology.lookupCategory(category: '"+category+"') has no Biolink Mapping?");

			/*
			 * Not all beacon concept types will 
			 * already be mapped onto Biolink
			 * so we'll tag such types to "NAME_TYPE"
			 */
			biolinkClass = getDefaultCategory();
		
		return biolinkClass;
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param id
	 * @param predicate
	 * @return
	 */
	public BiolinkSlot lookupPredicate( Integer beaconId, String id, String predicate ) {
		
		BiolinkSlot biolinkSlot = 
				(BiolinkSlot)lookupTerm( 
						beaconId, 
						id, 
						predicate, 
						slotLookup, 
						(String s)-> getSlotByName(s) 
		) ;
		
		if( biolinkSlot == null)
			
			_logger.warn("Ontology.lookupPredicate(predicate: '"+predicate+"') has no Biolink Mapping?");

			/*
			 * Since the Translator community are still
			 * debating the "canonical" version of predicates 
			 * and how to encode them, we will, for now
			 * not reject "missing" predicates but rather
			 * just propagate them directly through.
			 */
			biolinkSlot = getDefaultPredicate();

		return biolinkSlot;
	}
	
	/*
	 * Common code for two Biolink Model ontology lookups above
	 */
	private BiolinkEntityInterface lookupTerm(
			Integer beaconId,
			String id,
			String term,  
			ModelLookup<? extends BiolinkEntityInterface> modelLookup,
			Function<String,Optional<? extends BiolinkEntityInterface>> lookupByName
	) {
		
		BiolinkEntityInterface biolinkTerm = null;
		
		Optional<? extends BiolinkEntityInterface> optionalBiolinkTerm = lookupByName.apply( term );
		
		if( ! optionalBiolinkTerm.isPresent()) {
			optionalBiolinkTerm = lookUpByBeacon( beaconId, id, modelLookup );
		}

		if(optionalBiolinkTerm.isPresent())
			biolinkTerm = optionalBiolinkTerm.get();
		
		return biolinkTerm;
	}
}
