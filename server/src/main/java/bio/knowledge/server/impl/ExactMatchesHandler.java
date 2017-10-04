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
package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.database.repository.ConceptCliqueRepository;
import bio.knowledge.model.ConceptClique;

@Service
public class ExactMatchesHandler {
	
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;
	@Autowired private KnowledgeBeaconService kbs;
	
	public ConceptClique assignAccessionId(ConceptClique clique) {
		// Heuristic in Java code to set a reasonable "equivalent concept clique" canonical identifier
		// (See also bio.knowledge.database.repository.ConceptCliqueRepository.accessionIdFilter)
		
		List<String> conceptIds = clique.getConceptIds();
		
		String accessionId = null ;
		
		// Detect matches in this array order of precedence?
		for (String prefix : new String[] {"NCBIGENE","WD","CHEBI","UMLS"} ) {
			// Need to scan all the identifiers for the first match to the given prefix
			for ( String id : conceptIds ) {
				id = id.toUpperCase();
				if(id.startsWith(prefix+":")) {
					// first match past the gate wins (probably faulty heuristic, but alas...
					accessionId = id;
					break;
				}
			}
			
			if(accessionId!=null) break; // found
		}
		if( accessionId==null ) {
			accessionId = conceptIds.get(0).toUpperCase() ;
		}
		
		// Best guess accessionId set here
		clique.setId(accessionId);
		
		return clique;
	}
	
	/**
	 * Builds up concept cliques for each conceptId in {@code c}, and then merges them into a single
	 * set of conceptIds and returns this set.
	 * @param conceptIds
	 * @param sources 
	 * @return
	 */
	// RMB Revised this function to return a merged ConceptClique object every time?
	//public List<String> getExactMatchesSafe(List<String> c, String sessionId) {
	public ConceptClique getExactMatchesSafe(List<String> conceptIds, String sessionId) {
		
		List<Map<String, Object>> l = conceptCliqueRepository.getConceptCliques(conceptIds);
		
		List<ConceptClique> cliques = new ArrayList<ConceptClique>();
		List<String> unmatchedConceptIds = new ArrayList<String>(conceptIds);
		
		for (Map<String, Object> m : l) {
			List<String> matchedConceptIds = Arrays.asList((String[]) m.get("matchedConceptIds"));
			ConceptClique clique = (ConceptClique) m.get("clique");
			
			unmatchedConceptIds.removeAll(matchedConceptIds);
			cliques.add(clique);
		}
		
		if ( ! unmatchedConceptIds.isEmpty() ) {
			//Set<String> union = ConceptClique.unionOfConceptIds(cliques);
			//return new ArrayList<String>(union);
			//return mergeCliques(cliques);
		//} else {
			
			List<ConceptClique> foundCliques = unmatchedConceptIds.stream().map(
					conceptId -> new ConceptClique(findAggregatedExactMatches(conceptId, sessionId))
			)
			.map(clique->assignAccessionId(clique)) // heuristically assign the proper accessionId to each clique
			.collect(Collectors.toList());
			
			foundCliques.forEach(clique -> conceptCliqueRepository.save(clique));
			
			cliques.addAll(foundCliques);
			
			//Set<String> union = ConceptClique.unionOfConceptIds(cliques);
			
			//int sumOfCliqueSizes = cliques.stream().mapToInt(clique -> clique.size()).sum();
			//return new ArrayList<String>(union);
		}
		
		//TODO: This can be put inside a thread to speed things up.
		//		All we're doing here is cleaning up the database
		//		without changing what this method will return.
		// RMB: PRACTICAL ISSUE: THIS IMPLIES THAT CLIQUES DON'T HAVE 
		//      STABLE IDENTITY? PERHAPS PROBLEMATIC UPSTREAM IN TKBIO ?
		ConceptClique theClique = null ;
		if(cliques.size() != 0) {

			if(cliques.size()==1) {
				theClique = cliques.get(0) ;
			} else
				//if (sumOfCliqueSizes != union.size()) {
					for (ConceptClique clique1 : cliques) {
						for (ConceptClique clique2 : cliques) {
							if (clique1 != clique2) {
								if (ConceptClique.notDisjoint(clique1, clique2)) {
									theClique = conceptCliqueRepository.mergeConceptCliques(clique1.getDbId(), clique2.getDbId());
								}
							}
						}
					}
				//}
		} else {
			// Last ditch desperate effort?
			theClique = getExactMatchesUnsafe( conceptIds,  sessionId );
		}
		if(theClique==null)
			throw new RuntimeException("getExactMatchesSafe() ERROR: theClique should not be null at this point?");
		
		return theClique;
	}
	
	/**
	 * Performs a similar function as {@code getExactMatchesSafe(List<String>)}, but
	 * <b>assumes that {@code c} is a list of exact matches</b>. If this assumption fails to
	 * hold true, the database could be populated with faulty concept cliques. Because of this,
	 * this method is much faster than {@code getExactMatchesSafe}. Note that if {@code c.size() == 1},
	 * then the assumption is trivially satisfied and this method can be used safely.
	 * 
	 * @param c
	 * 		A list of <b>exactly matching</b> concept ID's
	 * @return
	 */
	public ConceptClique getExactMatchesUnsafe(List<String> c, String sessionId) {
		
		if(c.isEmpty()) return null;
		
		ConceptClique conceptClique = conceptCliqueRepository.getConceptClique(c);
		
		if (conceptClique != null) {
			return conceptClique;
		} else {
			
			ConceptClique clique = new ConceptClique(findAggregatedExactMatches(c, sessionId));
			
			clique = assignAccessionId(clique);
			
			conceptCliqueRepository.save(clique);
			
			return clique;
		}
	}
	
	private Set<String> findAggregatedExactMatches(List<String> c, String sessionId) {
		Set<String> matches = new HashSet<String>(c);
		int size;
		
		do {
			size = matches.size();
			CompletableFuture<List<String>> future = kbs.getExactMatchesToConceptList(new ArrayList<String>(matches), sessionId);
			
			try {
				List<String> aggregatedMatches = future.get(ControllerImpl.DEFAULT_TIMEOUT, ControllerImpl.TIMEUNIT);
				matches.addAll(aggregatedMatches);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}
			
		} while (size < matches.size());
		
		return matches;
	}
	
	private Set<String> findAggregatedExactMatches(String conceptId, String sessionId) {
		return findAggregatedExactMatches(Arrays.asList(new String[]{conceptId}), sessionId);
	}
}
