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
package bio.knowledge.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import bio.knowledge.model.ConceptType;
import bio.knowledge.model.umls.Category;

/**
 * @author Richard
 *
 */
@Service
public class ConceptTypeService {
	
	public ConceptTypeService() { }
	
	@PostConstruct
	private void loadPredefinedTypes() {
		
		// Hardcoded classical UMLS types
		typesById.put("ACTI", Category.ACTI);
		typesById.put("ANAT", Category.ANAT);
		typesById.put("CHEM", Category.CHEM);
		typesById.put("CONC", Category.CONC);
		typesById.put("DEVI", Category.DEVI);
		typesById.put("DISO", Category.DISO);
		typesById.put("GENE", Category.GENE);
		typesById.put("GEOG", Category.GEOG);
		typesById.put("LIVB", Category.LIVB);
		typesById.put("OBJC", Category.OBJC);
		typesById.put("OCCU", Category.OCCU);
		typesById.put("ORGA", Category.ORGA);
		typesById.put("PHEN", Category.PHEN);
		typesById.put("PHYS", Category.PHYS);
		typesById.put("PROC", Category.PROC);
	}

	private Map<String,ConceptType> typesById = 
			new TreeMap<String,ConceptType>();
	
	public List<ConceptType> lookUpByIdentifier(String idList) {
		List<ConceptType> types = new ArrayList<ConceptType>();
		if( !(idList == null || idList.isEmpty())) {
			/*
			 * Some Concepts have more than one 
			 * (space-delimited) assigned type
			 */
			String[] identifiers = idList.split(" ");
			for(String id : identifiers) {
				if(typesById.containsKey(id)) {
					types.add(typesById.get(id));
				}
			}
		}
		return types; // may be empty?
	}
	
	public ConceptType lookUp(String id) {
		List<ConceptType> conceptTypes = lookUpByIdentifier(id);
		if (conceptTypes.isEmpty()) {
			return null;
		} else {
			return conceptTypes.get(0);
		}
	}

}
