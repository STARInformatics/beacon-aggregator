package bio.knowledge.ontology.mapping;

import java.util.Optional;

import org.apache.commons.text.WordUtils;

public enum NameSpace {
	
	BIOLINK("blm","http://bioentity.io/vocab/"),
	WIKIDATA("wd","https://www.wikidata.org/wiki/"),
	BIOPAX("bp","http://www.biopax.org/release/biopax-level3.owl#"),
	UMLSSG("umlssg","https://metamap.nlm.nih.gov/Docs/SemGroups_2013#"),
	NDEXBIO("ndex","http://http://www.ndexbio.org/")
	;
	
	private final String prefix;
	private final String baseUri;
	
	NameSpace(String prefix, String baseUri) {
		this.prefix = prefix;
		this.baseUri = baseUri;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPrefix() {
		return this.prefix;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBaseUri() {
		return this.baseUri;
	}
	
	/**
	 * 
	 * @param curie
	 * @return
	 */
    public static Optional<NameSpace> lookUpByPrefix(
    		String curie // curie may also be a raw prefix... should still work?
    ) { 
    	if(curie==null || curie.isEmpty()) return Optional.empty();
    	String[] idpart = curie.split(":");	
		String prefix = idpart[0].toLowerCase();
    	for(NameSpace type: NameSpace.values()) {
    		if(type.prefix.equals(prefix))
    			return Optional.of(type) ;
    	}
    	return Optional.empty();
    }
	
	static public String makeIri(String id) {
		
		// Sanity check
		if(id == null || id.isEmpty()) return "";
		
		String iri = "" ;
		
		// Check if this is a CURIE, then look it up
		String[]curieParts = id.split("\\:");
		if(curieParts.length>1) {
			
			/*
			 * TODO: add a NameSpace lookup mechanism here
			 * to generate proper IRIs for beacon specific CURIEs
			 */
			Optional<NameSpace> nsOpt = lookUpByPrefix(curieParts[0]);
			
			if(nsOpt.isPresent()) {
				
				NameSpace ns = nsOpt.get();
				
				iri = ns.baseUri;
				
				// Local Hack to tweak Wikidata properties URI
				if(ns.equals(WIKIDATA)) {
					if(curieParts[1].startsWith("P")) {
						iri += "Property:";
					}
				}
				
				iri += curieParts[1];

			} else {
				// Don't know how to transform?
				iri = id;
			}
			
		} else {
			/*
			 * Assume that this is a simple Biolink type and default 
			 * to a regular Biolink IRI, with camel case objectId
			 */
			iri = BIOLINK.baseUri + WordUtils.capitalizeFully(id,null).replaceAll(" ", "");
		}
		
		return iri;
	}
}
