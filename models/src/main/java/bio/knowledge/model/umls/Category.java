/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.model.umls;

import org.neo4j.ogm.annotation.NodeEntity;

import bio.knowledge.model.ConceptTypeEntry;

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
@Deprecated
@NodeEntity(label="UMLS_Semantic_Category")
public class Category extends ConceptTypeEntry {
	
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