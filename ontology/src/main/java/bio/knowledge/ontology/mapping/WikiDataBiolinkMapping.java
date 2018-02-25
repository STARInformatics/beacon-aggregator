/**
 * 
 */
package bio.knowledge.ontology.mapping;

/**
 * @author richard
 *
 */
public class WikiDataBiolinkMapping extends BiolinkModelMapping {

	private static final long serialVersionUID = 5362827597807692913L;
	
	/*
	 *  TODO: Greg needs to populate this table with the 
	 *  list of Wikidata "concept type" properties with 
	 *  their mappings to the Biolink Model
	 */
	WikiDataBiolinkMapping() {
		put("wd:Q12140",CHEMICAL_SUBSTANCE);  // "Drug"
		put("wd:Q7187", GENE);                // "Gene"
		put("wd:Q11173",CHEMICAL_SUBSTANCE);  // "Chemical compound"
		put("wd:Q8054", PROTEIN);             // "Protein"
		put("wd:Q12136",DISEASE);             // "Disease"
	}

}
