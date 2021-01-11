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
public class Ontology extends bio.knowledge.ontology.Ontology {
	
	private static Logger _logger = LoggerFactory.getLogger(Ontology.class);
	
	@Autowired KnowledgeBeaconRegistry registry;
	
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
		return (Optional<BiolinkClass>)lookUpByBeacon(beaconId,category,getClassLookup());
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
			String predicate
	) {
		return (Optional<BiolinkSlot>)lookUpByBeacon(beaconId,predicate,getSlotLookup());
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
						getClassLookup(),
						(String s)-> getClassByName(s) 
		) ;
		
		if(biolinkClass==null) {
			
			_logger.warn("Ontology.lookupCategory(category: '"+category+"') has no Biolink Mapping?");

			/*
			 * Not all beacon concept types will 
			 * already be mapped onto Biolink
			 * so we'll tag such types to "NAME_TYPE"
			 */
			biolinkClass = getDefaultCategory();
		}
		
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
						getSlotLookup(),
						(String s)-> getSlotByName(s) 
		) ;
		
		if( biolinkSlot == null) {
			
			_logger.warn("Ontology.lookupPredicate(): predicate '"+predicate+"' with id '"+id+"', from beacon '"+beaconId+"' has no Biolink Mapping?");

			/*
			 * Since the Translator community are still
			 * debating the "canonical" version of predicates 
			 * and how to encode them, we will, for now
			 * not reject "missing" predicates but rather
			 * just propagate them directly through.
			 */
			biolinkSlot = getDefaultPredicate();
		}
		
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
