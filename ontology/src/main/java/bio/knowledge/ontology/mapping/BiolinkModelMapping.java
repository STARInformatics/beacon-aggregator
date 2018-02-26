/**
 * 
 */
package bio.knowledge.ontology.mapping;

import java.util.HashMap;

import org.apache.commons.text.WordUtils;

/**
 * @author richard
 *
 */
public class BiolinkModelMapping extends HashMap<String, String> {

	private static final long serialVersionUID = 1234687328782964210L;
	
	static public final String BaseIRI = "http://bioentity.io/vocab/" ;
	
	static public String makeIri(String name) {
		return BaseIRI + WordUtils.capitalizeFully(name,null).replaceAll(" ", "");
	}

	protected final String NAMED_THING         = "named thing";
	protected final String ACTIVITY_AND_BEHAVIOR = "activity and behavior";
	protected final String ADMINISTRATIVE_ENTITY = "administrative entity";
	protected final String DEVICE              = "device";
	protected final String OCCUPATION          = "occupation";
	protected final String ANATOMICAL_ENTITY   = "anatomical entity";
	protected final String GENOMIC_ENTITY      = "genomic entity";
	protected final String GENE                = "gene";
	protected final String PROTEIN             = "protein";
	protected final String DISEASE             = "disease";
	protected final String CHEMICAL_SUBSTANCE  = "chemical substance";
	protected final String DRUG                = "drug";
	protected final String GEOGRAPHIC_LOCATION = "geographic location";
	protected final String ORGANISMAL_ENTITY   = "organismal entity";
	protected final String INDIVIDUAL_ORGANISM = "individual organism";
	protected final String BIOLOGICAL_PROCESS  = "biological process";
	protected final String PHYSIOLOGY          = "physiology";
	protected final String INFORMATION_CONTENT_ENTITY = "information content entity";
	protected final String PROCEDURE           = "procedure";
	protected final String PHENOMENON          = "phenomenon";

}
