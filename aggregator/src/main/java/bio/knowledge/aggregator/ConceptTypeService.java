/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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
package bio.knowledge.aggregator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.Util;
import bio.knowledge.database.repository.ConceptTypeRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.ontology.BeaconBiolinkModel;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.ontology.mapping.BeaconBiolinkMappingIndex;
import bio.knowledge.ontology.mapping.NameSpace;

/**
 * @author Richard
 *
 */
@Service
public class ConceptTypeService implements Util {
	
	//private static Logger _logger = LoggerFactory.getLogger(ConceptTypeService.class);
	
	@Autowired private ConceptTypeRepository conceptTypeRepository;
	
	public ConceptTypeService() { }
	
	/**
	 * 
	 * @param term
	 * @return
	 */
	public ConceptTypeEntry getConceptTypeByTerm(BiolinkTerm term) {
		
		Optional<ConceptTypeEntry> typeOpt = 
				conceptTypeRepository.getConceptTypeByCurie(term.getCurie());
		
		ConceptTypeEntry type;
		if(typeOpt.isPresent())
			type = typeOpt.get();
		else {
			type = new ConceptTypeEntry(term);
			type = conceptTypeRepository.save(type);
		}
		
		return type;
	}
	
	/**
	 * 
	 * @return
	 */
	public ConceptTypeEntry defaultType() {
		return getConceptTypeByTerm(BiolinkTerm.NAMED_THING);
	}

	/**
	 * @param termId
	 * @return a ConceptTypeEntry based on the termId either as a Biolink name or a known CURIE.
	 */
	public ConceptTypeEntry lookUpByIdentifier(String termId) {
		
		if( nullOrEmpty(termId) ) {
			return null;
			
		} else {
			
			BiolinkTerm biolinkTerm = null;
			
			// TermId may already be a naked BiolinkTerm label?
			Optional<BiolinkTerm> termOpt = BiolinkTerm.lookUpName(termId);
			if(termOpt.isPresent()) 
				biolinkTerm = termOpt.get();
			
			else {
				
				// Check for legacy UMLS Semantic Group conversion
				termOpt = BeaconBiolinkMappingIndex.getMapping (NameSpace.UMLSSG.getPrefix(), termId );
				if(termOpt.isPresent()) 
					biolinkTerm = termOpt.get();
				else {
					/*
					 * See if you can translate the term id assuming 
					 * it is a CURIE and using its namespace prefix as a cue
					 */
					Optional<NameSpace> nsOpt = NameSpace.lookUpByPrefix(termId);
					if(nsOpt.isPresent()) {
						NameSpace nameSpace = nsOpt.get();
						termOpt = BeaconBiolinkMappingIndex.getMapping(nameSpace.getPrefix(), termId);
						if(termOpt.isPresent())
							biolinkTerm = termOpt.get();
					}
				}
			}
			
			if(biolinkTerm == null)
				// Unknown thing... just tag it as a Named Thing
				biolinkTerm = BiolinkTerm.NAMED_THING ;
			
			return getConceptTypeByTerm(biolinkTerm);
		}
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param termId
	 * @return
	 */
	public ConceptTypeEntry lookUp( Integer beaconId, String termId ) {
		
		// Check first if the term can be directly resolved
		ConceptTypeEntry cte = lookUpByIdentifier(termId) ;
		if(cte == null) {
			// Otherwise, try to resolve using the mapping function
			Optional<BiolinkTerm> termOpt = BeaconBiolinkModel.lookUp(beaconId, termId);
			BiolinkTerm biolinkTerm = null;
			if(termOpt.isPresent())
				biolinkTerm = termOpt.get();
			else
				// Just an object... not sure what kind
				biolinkTerm = BiolinkTerm.NAMED_THING;
			cte = getConceptTypeByTerm(biolinkTerm);
		}
		return cte;
	}

	/**
	 * 
	 * @param clique
	 * @return
	 */
	public Set<ConceptTypeEntry> getConceptTypesByClique(String clique) {
		
		Set<ConceptTypeEntry> typeSet = new HashSet<ConceptTypeEntry>();
		
		Optional<List<ConceptTypeEntry>> typesOpt = 
				conceptTypeRepository.getConceptTypeByClique(clique);
		
		if(typesOpt.isPresent()) typeSet.addAll(typesOpt.get());
		
		return typeSet;
		
		/*
		 * 

		Long typeId = conceptTypeRepository.getConceptTypeByClique(clique);

		if( typeId != null ) {
			
			Optional<Map<String,Object>> typeOpt = 
					conceptTypeRepository.retrieveByDbId(typeId);
			
			if(typeOpt.isPresent()) {
				Map<String,Object> entry = typeOpt.get();
				ConceptTypeEntry type = 
						new ConceptTypeEntry(
								(String)entry.get("baseUri"),
								(String)entry.get("prefix"),
								(String)entry.get("identifier"),
								(String)entry.get("name"),
								(String)entry.get("definition")
						);
				type.setDbId(typeId);
				Long version = (Long)entry.get("version");
				type.setVersion(version.intValue()); // might fail for super large versions?
				type.setVersionDate((Long)entry.get("versionDate"));
				types.add(type);
			}
		}
		 */
	}

	/**
	 * 
	 * @param types
	 * @return
	 */
	static public String getString(Set<ConceptTypeEntry> types) {
		String label = "";
		boolean first = true;
		for(ConceptTypeEntry type:types) {
			if(first)
				first = false;
			else
				label +="|";
			label += type.getLabel();
		}
		return label;
	}
}
