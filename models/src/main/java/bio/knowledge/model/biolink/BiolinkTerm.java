/**
 * 
 */
package bio.knowledge.model.biolink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.neo4j.ogm.annotation.NodeEntity;

import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.ontology.mapping.NameSpace;

/**
 * This class documents the specific hard coded 
 * Concept semantic types defined by the UMLS.
 * 
 * Note that although the UMLS_URI corresponds to 
 * a real web document, hence is somewhat informative and
 * confers globally uniqueness to the URI, the composite 
 * URI itself simulated and is NOT directly resolvable (yet).
 * 
 * @author Richard Bruskiewich
 *
 */
@NodeEntity(label="BiolinkModelTerm")
public class BiolinkTerm extends ConceptTypeEntry {
	
	public BiolinkTerm(
			String baseUri, 
			String prefix, 
			String identifier, 
			String name, 
			String definition
	) {
		super( baseUri, prefix, identifier, name, definition); 
	}
	
	public final static String BIOLINK_BASE_URI = NameSpace.BIOLINK.getBaseUri();
	public final static String BIOLINK_PREFIX   = NameSpace.BIOLINK.getPrefix();

	public final static BiolinkTerm NAMED_THING                = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "NamedThing", "named thing","");
	public final static BiolinkTerm ACTIVITY_AND_BEHAVIOR      = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "ActivityAndBehavior", "activity and behavior","");
	public final static BiolinkTerm ADMINISTRATIVE_ENTITY      = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "AdministrativeEntity", "administrative entity","");
	public final static BiolinkTerm DEVICE                     = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Device", "device","");
	public final static BiolinkTerm OCCUPATION                 = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Occupation", "occupation","");
	public final static BiolinkTerm ANATOMICAL_ENTITY          = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "AnatomicalEntity", "anatomical entity","");
	public final static BiolinkTerm GENOMIC_ENTITY             = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "GenomicEntity", "genomic entity","");
	public final static BiolinkTerm GENE                       = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Gene", "gene","");
	public final static BiolinkTerm PROTEIN                    = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Protein", "protein","");
	public final static BiolinkTerm DISEASE                    = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Disease", "disease","");
	public final static BiolinkTerm CHEMICAL_SUBSTANCE         = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "ChemicalSubstance", "chemical substance","");
	public final static BiolinkTerm DRUG                       = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Drug", "drug","");
	public final static BiolinkTerm GEOGRAPHIC_LOCATION        = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "GeographicLocation", "geographic location","");
	public final static BiolinkTerm ORGANISMAL_ENTITY          = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "OrganismalEntity", "organismal entity","");
	public final static BiolinkTerm INDIVIDUAL_ORGANISM        = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "IndividualOrganism", "individual organism","");
	public final static BiolinkTerm BIOLOGICAL_PROCESS         = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "BiologicalProcess", "biological process","");
	public final static BiolinkTerm PHYSIOLOGY                 = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Physiology", "physiology","");
	public final static BiolinkTerm INFORMATION_CONTENT_ENTITY = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "InformationContentEntity", "information content entity","");
	public final static BiolinkTerm PROCEDURE                  = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Procedure", "procedure","");
	public final static BiolinkTerm PHENOMENON                 = new BiolinkTerm(BIOLINK_BASE_URI,BIOLINK_PREFIX, "Phenomenon", "phenomenon","");
	
	static public String DEFAULT_SEMANTIC_GROUP = BiolinkTerm.NAMED_THING.getCurie();
	
	static private Map<String,BiolinkTerm> catalog = new HashMap<String,BiolinkTerm>();
	
	static {
		catalog.put(NAMED_THING.getName(), NAMED_THING);
		catalog.put(ACTIVITY_AND_BEHAVIOR.getName(), ACTIVITY_AND_BEHAVIOR);
		catalog.put(ADMINISTRATIVE_ENTITY.getName(), ADMINISTRATIVE_ENTITY);
		catalog.put(DEVICE.getName(), DEVICE);
		catalog.put(OCCUPATION.getName(), OCCUPATION);
		catalog.put(ANATOMICAL_ENTITY.getName(), ANATOMICAL_ENTITY);
		catalog.put(GENOMIC_ENTITY.getName(), GENOMIC_ENTITY);
		catalog.put(GENE.getName(), GENE);
		catalog.put(PROTEIN.getName(), PROTEIN);
		catalog.put(DISEASE.getName(), DISEASE);
		catalog.put(CHEMICAL_SUBSTANCE.getName(), CHEMICAL_SUBSTANCE);
		catalog.put(DRUG.getName(), DRUG);
		catalog.put(GEOGRAPHIC_LOCATION.getName(), GEOGRAPHIC_LOCATION);
		catalog.put(ORGANISMAL_ENTITY.getName(), ORGANISMAL_ENTITY);
		catalog.put(INDIVIDUAL_ORGANISM.getName(), INDIVIDUAL_ORGANISM);
		catalog.put(BIOLOGICAL_PROCESS.getName(), BIOLOGICAL_PROCESS);
		catalog.put(PHYSIOLOGY.getName(), PHYSIOLOGY);
		catalog.put(INFORMATION_CONTENT_ENTITY.getName(), INFORMATION_CONTENT_ENTITY);
		catalog.put(PROCEDURE.getName(), PROCEDURE);
		catalog.put(PHENOMENON.getName(), PHENOMENON);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	static public Optional<BiolinkTerm> lookUp(String name) {
		if(catalog.containsKey(name))
			return Optional.of(catalog.get(name));
		else
			return Optional.empty();
	}
}
