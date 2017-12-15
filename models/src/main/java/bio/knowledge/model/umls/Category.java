/**
 * 
 */
package bio.knowledge.model.umls;

import bio.knowledge.model.ConceptType;

/**
 * @author richard
 *
 */
public class Category extends ConceptType {
	
	public final static String UMLS_URI = "http://umls.nih.gov/semgroup#";

	public final static ConceptType ANY  = new ConceptType(UMLS_URI+"ANY","Any Semantic Type");
	public final static ConceptType ACTI = new ConceptType(UMLS_URI+"ACTI","Activities & Behaviors"); 
	public final static ConceptType ANAT = new ConceptType(UMLS_URI+"ANAT","Anatomy"); 
	public final static ConceptType CHEM = new ConceptType(UMLS_URI+"CHEM","Chemicals & Drugs"); 
	public final static ConceptType CONC = new ConceptType(UMLS_URI+"CONC","Concepts & Ideas"); 
	public final static ConceptType DEVI = new ConceptType(UMLS_URI+"DEVI","Devices"); 
	public final static ConceptType DISO = new ConceptType(UMLS_URI+"DISO","Disorders"); 
	public final static ConceptType GENE = new ConceptType(UMLS_URI+"GENE","Genes & Molecular Sequences"); 
	public final static ConceptType GEOG = new ConceptType(UMLS_URI+"GEOG","Geographic Areas"); 
	public final static ConceptType LIVB = new ConceptType(UMLS_URI+"LIVB","Living Beings"); 
	public final static ConceptType OBJC = new ConceptType(UMLS_URI+"OBJC","Objects"); 
	public final static ConceptType OCCU = new ConceptType(UMLS_URI+"OCCU","Occupations"); 
	public final static ConceptType ORGA = new ConceptType(UMLS_URI+"ORGA","Organizations"); 
	public final static ConceptType PHEN = new ConceptType(UMLS_URI+"PHEN","Phenomena"); 
	public final static ConceptType PHYS = new ConceptType(UMLS_URI+"PHYS","Physiology"); 
	public final static ConceptType PROC = new ConceptType(UMLS_URI+"PROC","Procedures");

}
