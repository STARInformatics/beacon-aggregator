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
import bio.knowledge.aggregator.ontology.Ontology;
import bio.knowledge.database.repository.ConceptCategoryRepository;
import bio.knowledge.model.CURIE;
import bio.knowledge.model.ConceptCategory;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.ontology.BiolinkClass;
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

	@Autowired private ConceptCategoryRepository conceptTypeRepository;
	@Autowired private Ontology ontology;
	
	public ConceptTypeService() { }
	
	/**
	 * 
	 * @param biolinkClass
	 * @return
	 */
	public ConceptCategory getConceptType(BiolinkTerm biolinkTerm) {
		Optional<BiolinkClass> biolinkClassOpt = ontology.getClassByName(biolinkTerm);
		if(biolinkClassOpt.isPresent()) {
			return getConceptType(biolinkClassOpt.get());
		} else {
			return null ;
		}
		
	}
	
	/**
	 * 
	 * @param biolinkClass
	 * @return
	 */
	public ConceptCategory getConceptType(BiolinkClass biolinkClass) {
		
		String categoryName = biolinkClass.getName();
		
		Optional<ConceptCategory> categoryOpt = 
				conceptTypeRepository.getConceptCategoryByName(categoryName);
		
		ConceptCategory category;
		if(categoryOpt.isPresent()) {
			category = categoryOpt.get();
		} else {
			category = new ConceptCategory(biolinkClass);
			category = conceptTypeRepository.save(category);
		}
		return category;
	}
	
	/**
	 * 
	 * @return
	 */
	public ConceptCategory defaultConceptType() {
		return getConceptType(BiolinkTerm.NAMED_THING);
	}

	/**
	 * @param termId
	 * @return a ConceptTypeEntry based on the termId either as a Biolink name or a known CURIE.
	 */
	public ConceptCategory lookUpByIdentifier(String termId) {
		
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
			
			return getConceptType(biolinkTerm);
		}
	}

	/**
	 * 
	 * @param beaconId
	 * @param categoryLabel
	 * @return
	 */
	public ConceptCategory lookUp(Integer beaconId, String categoryLabel) {
		
		// TODO: This method won't handle category labels which are concatenations of several categories?
		
		Optional<BiolinkClass> biolinkClassOpt = ontology.getClassByName(categoryLabel);
		
		if (biolinkClassOpt.isPresent()) {
			return getConceptType(biolinkClassOpt.get());
			
		} else {
			// We assume here that this is not a Biolink category...
			Optional<BiolinkClass> biolinkClassOptional = 
					ontology.lookUpCategoryByBeacon(beaconId, categoryLabel);
			
			if (biolinkClassOptional.isPresent()) {
				return getConceptType(biolinkClassOptional.get());
				
			} else {
				return lookUpByIdentifier(categoryLabel);
			}
		}
	}

	/**
	 * 
	 * @param clique
	 * @return
	 */
	public Set<ConceptCategory> getConceptTypesByClique(String clique) {
		
		Set<ConceptCategory> categories = new HashSet<ConceptCategory>();
		
		Optional<List<ConceptCategory>> categoriesOpt = 
				conceptTypeRepository.getConceptTypeByClique(clique);
		
		if(categoriesOpt.isPresent()) categories.addAll(categoriesOpt.get());
		
		return categories;
		
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
				categories.add(type);
			}
		}
		 */
	}

	/**
	 * Builds a list of concept categories assuming one or more delimited list of Clique categories.
	 * 
	 * @param clique
	 * @return
	 */
	public Set<ConceptCategory> getConceptTypesByClique(ConceptClique clique) {
		Set<ConceptCategory> categories = new HashSet<ConceptCategory>();
		if(clique!=null) {
			String cliqueCategory = clique.getConceptType();
			if( cliqueCategory != null ) {
				String[] terms = cliqueCategory.split(",");
				for(String term : terms) {
					if(!nullOrEmpty(term)) {
						ConceptCategory category = lookUpByIdentifier(term);
						categories.add(category);
					}
				}
			}
		}
		
		return categories;
	}
	
	/**
	 * 
	 * @param curie
	 * @return
	 */
	public ConceptCategory defaultConceptType(String curie) {
		String prefix = CURIE.getQualifier(curie);
		Optional<NameSpace> nsOpt =  NameSpace.lookUpByPrefix(prefix);
		if(nsOpt.isPresent()) {
			NameSpace namespace = nsOpt.get();
			return getConceptType(namespace.defaultConceptType());
		}
		return defaultConceptType();
	}

	/**
	 * 
	 * @param categories
	 * @param delimiter
	 * @return
	 */
	public String getDelimitedString(Set<ConceptCategory> categories) {
		
		if(nullOrEmpty(categories))
			return BiolinkTerm.NAMED_THING.getLabel();
				
		String label = "";
		boolean first = true;
		for(ConceptCategory category : categories) {
			if(first)
				first = false;
			else
				label += "|" ;
			label += category.getName();
		}
		return label;
	}
}
