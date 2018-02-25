/**
 * 
 */
package bio.knowledge.ontology.mapping;

/**
 * @author richard
 *
 */
public class UMLSBiolinkMapping extends BiolinkModelMapping {

	private static final long serialVersionUID = 373558122178877621L;
	
	UMLSBiolinkMapping() {
		
		 put("ACTI",NAMED_THING);         // "Activities & Behaviors"
		 put("ANAT",ANATOMICAL_ENTITY);   // "Anatomy"
		 put("CHEM",CHEMICAL_SUBSTANCE);  // "Chemicals & Drugs"
		 put("CONC",INFORMATION_CONTENT_ENTITY); // "Concepts & Ideas"
		 put("DEVI",NAMED_THING);         // "Devices"
		 put("DISO",DISEASE);             // "Disorders"
		 put("GENE",GENE);                // "Genes & Molecular Sequences"
		 put("GEOG",GEOGRAPHIC_LOCATION); // "Geographic Areas"
		 put("LIVB",INDIVIDUAL_ORGANISM); // "Living Beings"
		 put("OBJC",NAMED_THING);         // "Objects"
		 put("OCCU",NAMED_THING);         // "Occupations"
		 put("ORGA",NAMED_THING);         // "Organizations"
		 put("PHEN",NAMED_THING);         // "Phenomena"
		 put("PHYS",BIOLOGICAL_PROCESS);  // "Physiology"
		 put("PROC",NAMED_THING);         // "Procedures"
		
	}
}
