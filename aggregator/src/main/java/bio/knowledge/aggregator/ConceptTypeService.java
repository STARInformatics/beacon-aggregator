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
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

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
					BiolinkTerm type = BiolinkTerm.lookUp(biolinkTerm) ;
					types.add( type );
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
		
		String bolinkTerm = BiolinkModel.lookup(beaconId, termId);
		
		Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();
		
		// Lookup ConceptTypeEntry for Biolink Model Term
		types.add(BiolinkTerm.lookUp(bolinkTerm));
		
		return types;
	}

}
