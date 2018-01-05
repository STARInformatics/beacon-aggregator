/**
 * 
 */
package bio.knowledge.model.umls;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

import bio.knowledge.model.ConceptType;

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
@NodeEntity
public class Category extends ConceptType {
	
	@GraphId Long id;

	public Category(
			String baseUri, 
			String prefix, 
			String identifier, 
			String name, 
			String definition
	) {
		super( baseUri, prefix, identifier, name, definition); 
	}
	
	public final static String UMLS_URI = "https://metamap.nlm.nih.gov/SemanticTypesAndGroups.shtml/group#";
	public final static String UMLS_PREFIX = "umls_sg";
	
	public final static Category ACTI = new Category(UMLS_URI,UMLS_PREFIX,"ACTI","Activities & Behaviors",""); 
	public final static Category ANAT = new Category(UMLS_URI,UMLS_PREFIX,"ANAT","Anatomy",""); 
	public final static Category CHEM = new Category(UMLS_URI,UMLS_PREFIX,"CHEM","Chemicals & Drugs",""); 
	public final static Category CONC = new Category(UMLS_URI,UMLS_PREFIX,"CONC","Concepts & Ideas",""); 
	public final static Category DEVI = new Category(UMLS_URI,UMLS_PREFIX,"DEVI","Devices",""); 
	public final static Category DISO = new Category(UMLS_URI,UMLS_PREFIX,"DISO","Disorders",""); 
	public final static Category GENE = new Category(UMLS_URI,UMLS_PREFIX,"GENE","Genes & Molecular Sequences",""); 
	public final static Category GEOG = new Category(UMLS_URI,UMLS_PREFIX,"GEOG","Geographic Areas",""); 
	public final static Category LIVB = new Category(UMLS_URI,UMLS_PREFIX,"LIVB","Living Beings",""); 
	public final static Category OBJC = new Category(UMLS_URI,UMLS_PREFIX,"OBJC","Objects",""); 
	public final static Category OCCU = new Category(UMLS_URI,UMLS_PREFIX,"OCCU","Occupations",""); 
	public final static Category ORGA = new Category(UMLS_URI,UMLS_PREFIX,"ORGA","Organizations",""); 
	public final static Category PHEN = new Category(UMLS_URI,UMLS_PREFIX,"PHEN","Phenomena",""); 
	public final static Category PHYS = new Category(UMLS_URI,UMLS_PREFIX,"PHYS","Physiology",""); 
	public final static Category PROC = new Category(UMLS_URI,UMLS_PREFIX,"PROC","Procedures","");
	
	public static String DEFAULT_SEMANTIC_GROUP = Category.OBJC.getCurie();

}
