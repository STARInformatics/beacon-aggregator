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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.database.repository.ConceptTypeRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.biolink.BiolinkTerm;
import bio.knowledge.ontology.BiolinkModel;
import bio.knowledge.ontology.mapping.BeaconBiolinkMappingIndex;
import bio.knowledge.ontology.mapping.NameSpace;

/**
 * @author Richard
 *
 */
@Service
public class ConceptTypeService {
	
	//private static Logger _logger = LoggerFactory.getLogger(ConceptTypeService.class);
	
	@Autowired private ConceptTypeRepository   conceptTypeRepository;
	
	public ConceptTypeService() { }
	
	public Set<ConceptTypeEntry> lookUpByIdentifier(String curie) {
		
		Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();
		
		if( curie == null || curie.isEmpty() ) {
			throw new RuntimeException("ConceptTypeService needs a valid identifier!");
		} else {
			Optional<NameSpace> nsOpt = NameSpace.lookUpByPrefix(curie);
			if(nsOpt.isPresent()) {
				NameSpace nameSpace = nsOpt.get();
				Optional<String> mapping = 
						BeaconBiolinkMappingIndex.getMapping(nameSpace.getPrefix(), curie);
				if(mapping.isPresent()) {
					String biolinkTerm = mapping.get();
					Optional<BiolinkTerm> cteOpt = BiolinkTerm.lookUp(biolinkTerm);
					if(cteOpt.isPresent())
						types.add(cteOpt.get());
					else
						// Just an object... not sure what kind
						types.add(BiolinkTerm.NAMED_THING);
				} else {
					// Unknown term mapping?
					types.add( BiolinkTerm.NAMED_THING );
				}
			} else 
				// Unknown namespace?
				types.add( BiolinkTerm.NAMED_THING );
			}
		return types; 
	}
	
	/**
	 * 
	 * @param curie
	 * @return
	 */
	public ConceptTypeEntry lookUp(String curie) {
		Set<ConceptTypeEntry> conceptTypes = lookUpByIdentifier(curie);
		if (conceptTypes.isEmpty()) {
			return null;
		} else {
			return conceptTypes.iterator().next();
		}
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param termId
	 * @return
	 */
	public Set<ConceptTypeEntry> lookUp( Integer beaconId, String termId ) {
		
		Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();
		
		// TermId may already be a naked BiolinkTerm name?
		Optional<BiolinkTerm> cteOpt = BiolinkTerm.lookUp(termId);
		if(cteOpt.isPresent()) {
			types.add(cteOpt.get());
		} else {
			// Try to resolve termId treated as "curie", to its Biolink Model Term
			String biolinkTerm = BiolinkModel.lookUp(beaconId, termId);
			cteOpt = BiolinkTerm.lookUp(biolinkTerm);
			if(cteOpt.isPresent())
				types.add(cteOpt.get());
			else
				// Just an object... not sure what kind
				types.add(BiolinkTerm.NAMED_THING);
		}
		return types;
	}

	/**
	 * 
	 * @param clique
	 * @return
	 */
	public Set<ConceptTypeEntry> getConceptTypes(String clique) {
		
		Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();
		
		// TODO: How do I fix this to potentially return more than one type?
		Long typeId = conceptTypeRepository.getConceptType(clique);

		if( typeId != null ) {
			Optional<Map<String,Object>> typeOpt = conceptTypeRepository.retrieveById(typeId);
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
		
		return types;
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
			label += type.getName();
		}
		return label;
	}
}
