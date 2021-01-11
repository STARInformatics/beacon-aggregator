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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.model.CURIE;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.ontology.mapping.NameSpace;

/**
 * @author Richard
 *
 */
@Service
public class ConceptCliqueService {
	
	private static Logger _logger = LoggerFactory.getLogger(ConceptCliqueService.class);

	@Autowired private ConceptCategoryService conceptTypeService;
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;

	/**
	 * Heuristic in Java code to set a reasonable canonical "equivalent concept clique" accession identifier 
	 */
	public void assignAccessionId(Neo4jConceptClique theClique) {
		
		List<String> conceptIds = theClique.getConceptIds();
		
		if(conceptIds.isEmpty()) {
			_logger.error("assignAccessionId(): clique set of concept ids is empty. Cannot infer an accession identifier?");
			return;
		}
		
		String accessionId = null ;
		
		// Detect matches in the BioNameSpace in order of precedence?
		for (NameSpace namespace : NameSpace.values()) {
			/*
			 * Need to scan all the identifiers 
			 * for the first match to the given prefix.
			 * 
			 * First match past the gate wins 
			 * (probably faulty heuristic, but alas...)
			 * Prefix normalized to lower case... 
			 * Case sensitivity of prefix id's is a thorny issue!
			 */
			for ( String id : conceptIds ) {
				
				// not a valid CURIE? Ignore?
				if(id.indexOf(":")<=0) continue; 
				
				String[] idPart = id.split(":");
				
				if( namespace.getPrefix().equals( idPart[0] ) ) {
					
					/*
					 * RMB Oct 21, 2017 Design decision:
					 * We have started to track the source of
					 * identifiers by beacon id. This will allow
					 * us (in principle) to more easily resolve
					 * identifiers with divergent case sensitivity
					 * across beacons, thus, to help in clique 
					 * merging, we now normalize the prefix
					 * all to the recorded namespace.
					 *  
					 *  RMB Oct 17, 2017 Design decision:
					 *  Use whichever candidate CURIE first passes 
					 *  the namespace test here *without* normalizing 
					 *  string case to the BioNameSpace recorded case.
					 *  
					 *  This won't solve the problem of different
					 *  beacons using different string case for 
					 *  essentially the same namespace...
					 *  but will ensure that at least one 
					 *  beacon recognizes the identifier?
					 */
					accessionId =  namespace.name()+ ":" + idPart[1];
					
					String currentConceptType = theClique.getConceptCategory();
					if( 
						currentConceptType == null || 
						currentConceptType.equals(BiolinkTerm.NAMED_THING.getLabel())
						
					) theClique.setConceptType(conceptTypeService.defaultConceptCategory().getId());
					
					break;
				}
			}
			
			/* 
			 * We found a candidate canonical clique id? 
			 * No need to screen further namespaces?
			 */
			if(accessionId!=null) break; 
		}
		
		if( accessionId==null ) {
			/*
			 * Just take the first one in the list.
			 * Less satisfying heuristic but better than returning null?
			 */
			accessionId = CURIE.makeNormalizedCurie(conceptIds.get(0));
		}
		
		// Best guess accessionId is set here
		theClique.setId(accessionId);

		String semgroup = theClique.getConceptCategory();
		if(semgroup == null || semgroup.isEmpty())
			theClique.setConceptType(BiolinkTerm.NAMED_THING.getLabel()); // default unknown type
	}
	
	/**
	 * Merging of the data of second clique into first
	 * Also deletes the second clique
	 * @param second ConceptClique to be merged
	 */
	public void mergeConceptCliques( Neo4jConceptClique first, Neo4jConceptClique second ) {
		
		// For all 'other' beacon subcliques...
		for(int i = 1 ; i < second.getBeaconSubcliques().size() ; i++) {
			if(!second.getBeaconSubcliques().get(i).isEmpty()) {
				List<String> subclique = second.getConceptIds(i);
				first.addConceptIds(i, subclique);
			}
		}
		
		if( second.getDbId() != null ) 
			conceptCliqueRepository.delete(second);
		
		// Re-calibrate the accession identifier
		assignAccessionId(first);
	}
}
