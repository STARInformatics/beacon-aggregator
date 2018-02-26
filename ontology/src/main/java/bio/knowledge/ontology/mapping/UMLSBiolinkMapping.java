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
		
		 put("OBJC",NAMED_THING);           // "Objects"
		 put("ACTI",ACTIVITY_AND_BEHAVIOR); // "Activities & Behaviors"
		 put("ANAT",ANATOMICAL_ENTITY);     // "Anatomy"
		 put("CHEM",CHEMICAL_SUBSTANCE);    // "Chemicals & Drugs"
		 put("CONC",INFORMATION_CONTENT_ENTITY); // "Concepts & Ideas"
		 put("DEVI",DEVICE);                // "Devices"
		 put("DISO",DISEASE);               // "Disorders"
		 put("GENE",GENOMIC_ENTITY);        // "Genes & Molecular Sequences"
		 put("GEOG",GEOGRAPHIC_LOCATION);   // "Geographic Areas"
		 put("LIVB",ORGANISMAL_ENTITY);     // "Living Beings"
		 put("OCCU",OCCUPATION);            // "Occupations"
		 put("ORGA",ADMINISTRATIVE_ENTITY); // "Organizations"
		 put("PHEN",PHENOMENON);            // "Phenomena"
		 put("PHYS",PHYSIOLOGY);            // "Physiology"
		 put("PROC",PROCEDURE);             // "Procedures"
		
	}
}
