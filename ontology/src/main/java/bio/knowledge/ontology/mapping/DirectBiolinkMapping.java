/**
 * 
 */
package bio.knowledge.ontology.mapping;

/**
 * @author richard
 *
 */
public class DirectBiolinkMapping extends BiolinkModelMapping {

	private static final long serialVersionUID = -4624889836940523935L;

	DirectBiolinkMapping() {
		put("BLM:NamedThing",NAMED_THING);
		put("BLM:ActivityAndBehavior",ACTIVITY_AND_BEHAVIOR);
		put("BLM:AdministrativeEntity",ADMINISTRATIVE_ENTITY);
		put("BLM:Device",DEVICE);
		put("BLM:Occupation",OCCUPATION);
		put("BLM:AnatomicalEntity",ANATOMICAL_ENTITY);
		put("BLM:GenomicEntity",GENOMIC_ENTITY);
		put("BLM:Gene",GENE);
		put("BLM:Protein",PROTEIN);
		put("BLM:Disease",DISEASE);
		put("BLM:ChemicalSubstance",CHEMICAL_SUBSTANCE);
		put("BLM:Drug",DRUG);
		put("BLM:GeographicLocation",GEOGRAPHIC_LOCATION);
		put("BLM:OrganismalEntity",ORGANISMAL_ENTITY);
		put("BLM:IndividualOrganism",INDIVIDUAL_ORGANISM);
		put("BLM:BiologicalProcess",BIOLOGICAL_PROCESS);
		put("BLM:Physiology",PHYSIOLOGY);
		put("BLM:InformationContentEntity",INFORMATION_CONTENT_ENTITY);
		put("BLM:Procedure",PROCEDURE);
		put("BLM:Phenomenon",PHENOMENON);
	}
}
