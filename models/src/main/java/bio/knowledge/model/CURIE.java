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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bio.knowledge.ontology.mapping.NameSpace;



/**
 * @author Richard
 *
 */
public class CURIE {
	
	private static Logger _logger = LoggerFactory.getLogger(CURIE.class);
	
	public static final String DEFAULT_QUALIFIER = "kb" ;
	public static final String DEFAULT_BASE_URI = "http://knowledge.bio/" ;

	public static String makeCurie(String ns, String id) {
		return ns+":"+id;
	}
	
	public static String makeNormalizedCurie(String id) {
		
		// not a valid CURIE? Return as is... can't do much more?
		if(id.indexOf(":")<=0) {
			_logger.warn("makeNormalizedCurie(): input identifier '"+id+"' is not a CURIE? Returning it unchanged.");
			return id;
		}
		
		String[] idPart = id.split(":");
		return idPart[0].toUpperCase() + ":" + idPart[1];
	}
	
	/**
	 * 
	 * @param uri 'uniform resource identifier' source of the object id
	 * @param delimiter separating objectId from base URI path
	 * @return String objectId of the URI
	 */
	static public String getObjectId( String uri, String delimiter ) {
		int pathEnd = uri.lastIndexOf(delimiter) ;
		if(pathEnd>-1) {
			return uri.substring(pathEnd+1) ;
		} else
			return uri ; // just pass through?
	}
	
	/**
	 * Same as two argument method, with delimiter defaulting to "/"
	 * @param uri 'uniform resource identifier' source of the object id
	 * @return
	 */
	static public String getObjectId(String uri) {
		return getObjectId(uri,"/");
	}
	
	/**
	 * 
	 * @param uri 'uniform resource identifier' source of the object id
	 * @param delimiter separating objectId from base URI path
	 * @return String baseline URI of the URI
	 */
	static public String getBaseUri(String uri,String delimiter) {
		int pathEnd = uri.lastIndexOf(delimiter) ;
		if(pathEnd>-1) {
			return uri.substring(0,pathEnd) ;
		} else
			return uri ; // just pass through?
	}
	
	/**
	 * Same as two argument method, with delimiter defaulting to "/"
	 * @param uri 'uniform resource identifier' source of the object id
	 * @return
	 */
	static public String getBaseUri(String uri) {
		return getBaseUri(uri,"/");
	}
	
	/**
	 * 
	 * @param accessionId
	 * @return
	 */
	static public String getQualifier(String accessionId) {
		int colonIdx = accessionId.indexOf(':') ;
		if(colonIdx>-1) {
			String qualifier = accessionId.substring(0, colonIdx) ;
			return qualifier;
		} else
			return "" ;
	}
	
	/**
	 * 
	 * @param accessionId
	 * @return
	 */
	static public String getQualifiedObjectId(String accessionId) {
		int colonIdx = accessionId.indexOf(':') ;
		String objectId = accessionId; // default
		if(colonIdx>-1) {
			objectId = accessionId.substring(colonIdx+1) ;
		}
		return objectId ;
	}
	
	private static Map<String,String> mapQualifier2URI = 
			new HashMap<String,String>();
	
	/**
	 * 
	 * @param qualifier
	 * @param uri
	 */
	static public void registerQualifier(String qualifier,String uri) {
		qualifier = qualifier.toUpperCase();
		mapQualifier2URI.put(qualifier, uri);
	}
	
	/**
	 * 
	 * @param accessionId
	 * @return
	 */
	static public String resolveBaseUri(String accessionId) {
		String qualifier = getQualifier(accessionId);
		qualifier = qualifier.toUpperCase();
		if(!mapQualifier2URI.containsKey(qualifier)) 
			qualifier = DEFAULT_QUALIFIER;
		return mapQualifier2URI.get(qualifier);
	}
	
	/**
	 * 
	 * @param accessionId
	 * @return
	 */
	static public String resolveUri(String accessionId) {
		String qualifier = getQualifier(accessionId);
		qualifier = qualifier.toUpperCase();
		if(!mapQualifier2URI.containsKey(qualifier)) 
			qualifier = DEFAULT_QUALIFIER;
		return mapQualifier2URI.get(qualifier)+getQualifiedObjectId(accessionId);
	}
	
	/**
	 * @param query
	 * @return
	 */
	static public String encodeResource(Object query) {
		String parameter = "" ;
		try {
			// Treat as a simple literal but don't quote it if an Integer
			if(query instanceof Integer)
				parameter = ((Integer)query).toString() ;
			
			// Note that Strings with both digits and non-digits will 
			// throw an exception and be treated as a Literal to be quoted!
			
		} catch(Exception e) { // failed to parse Integer?
			// ignore... but parameter will be empty...
		}
		
		if(parameter.isEmpty()) {
			if(query instanceof String) {
				parameter = (String)query ;
				
				if( parameter.startsWith("http://") ||  
					parameter.startsWith("https://") ) {
					// URI needs to be properly bracketed
					 return "<"+parameter+">";				 
				} else if( parameter.indexOf(':')>-1 ) {
					// probably a qualified identifier...
					// just return directly?
					return parameter ; 
				} else // assume a simple literal
					// Need to double quote if the query
					// is a simple non-numeric literal
					return "\""+parameter+"\"" ;
			} else
				// not sure what to do here, so simply 
				// return the String representation!
				return query.toString() ;
		} else
			return parameter ;
	}
	
	public static final String PUBMED_QUALIFIER  = "pmid" ;

	public static final String ANNOTATION_QUALIFIER = "kba" ;
	public static final String BEACON_QUALIFIER     = "kbb" ;
	public static final String CONCEPT_QUALIFIER    = "kbc" ;
	public static final String STATEMENT_QUALIFIER  = "kbs" ;
	public static final String EVIDENCE_QUALIFIER   = "kbe" ;
	
	static {
		registerQualifier(DEFAULT_QUALIFIER, DEFAULT_BASE_URI);
		registerQualifier(ANNOTATION_QUALIFIER, DEFAULT_BASE_URI+"annotation/");
		registerQualifier(BEACON_QUALIFIER, DEFAULT_BASE_URI+"beacon/");  // more likely a equivalent concept beacon subclique
		registerQualifier(CONCEPT_QUALIFIER, DEFAULT_BASE_URI+"concept/"); // more likely a equivalent concept clique
		registerQualifier(STATEMENT_QUALIFIER, DEFAULT_BASE_URI+"statement/");
		registerQualifier(EVIDENCE_QUALIFIER, DEFAULT_BASE_URI+"evidence/");
		
		/* Some Knowledge.Bio domain specific qualifiers  
		 * TODO: This configuration should perhaps be moved
		 * to an domain-specific location(?) or initialized 
		 * from the ExternalDatabase records of the database(?)
		 */
		for(NameSpace ns: NameSpace.values()) {
			registerQualifier(ns.getPrefix().replace(":", ""), ns.getBaseUri());
		}
	}
	
}
