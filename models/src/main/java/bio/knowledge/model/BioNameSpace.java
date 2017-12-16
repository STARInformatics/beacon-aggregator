/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package bio.knowledge.model;

/**
 * List of canonical Biological NameSpaces
 * in order of precedence?, all normalized 
 * to upper case
 * 
 * @author Richard
 *
 */
public enum BioNameSpace {
	
	// PubMed concepts should always be tagged as scientific articles?
	PMID("PMID","CONC"),
	PUBMED("PMID","CONC"),
	
	DOID("DOID","DISO"),  // Disease Ontology
	ORPHANET("ORPHANET","DISO"), //	ORPHANET: http://www.orpha.net/
	
	NCBIGENE("NCBIGENE","GENE"),
	HGNC_SYMBOL("HGNC.SYMBOL","GENE"),
	GENECARDS("GENECARDS","GENE"),
	
	UNIPROT("uniprot","GENE"),  // Uniprot protein database - actually also "CHEM"...
	
	CHEBI("CHEBI","CHEM"),
	DRUGBANK("DRUGBANK","CHEM"),
	
	// Kyoto Encyclopedia of Genes and Genomes
	KEGG("KEGG","PHYS"), 
	KEGG_PATHWAY("KEGG_PATHWAY","PHYS"),
	
	REACT("REACT","PHYS"),    // REACTome == pathways?
	REACTOME("REACTOME","PHYS"), // REACTOME == pathways?
	BP("BP","PHYS"), // BioPAX
	PATHWAYCOMMONS("PATHWAYCOMMONS","PHYS"),  // Pathway Commons
	MIR("mirtarbase","CHEM"), // mirtarbase - micro RNA targets
	SMPDB("SMPDB","PHYS"),   // Small Molecular Pathway Database
	
	UMLS("UMLS","OBJC"),
	
	WD("wd","OBJC")
	;
	
	private String prefix;
	private String defaultSemanticGroup;
	
	private BioNameSpace(String prefix,String defaultSemanticGroup) {
		this.prefix = prefix;
		this.defaultSemanticGroup = defaultSemanticGroup;
	}
	
	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public static BioNameSpace getNameSpace(String prefix) {
		try {
			prefix = prefix.toUpperCase().replace(".", "_");
			return valueOf(prefix);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public Boolean equals( String prefix ) {
		try {
			prefix = prefix.toUpperCase().replace(".", "_");
			return this == valueOf(prefix);
		} catch (Exception e) {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return prefix;
	}
	
	public String defaultSemanticGroup() {
		return defaultSemanticGroup;
	}
	
	public static String defaultSemanticGroup(String curie) {
		String prefix = CURIE.getQualifier(curie);
		BioNameSpace namespace = getNameSpace(prefix);
		if(namespace!=null) {
			return namespace.defaultSemanticGroup();
		}
		return "OBJC";
	}

}
