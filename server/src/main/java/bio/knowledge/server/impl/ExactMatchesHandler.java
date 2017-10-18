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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.database.repository.ConceptCliqueRepository;
import bio.knowledge.model.ConceptClique;
import bio.knowledge.server.impl.Cache.CacheLocation;

/*
 * RMB September 26 revision: removed 'sessionId' from all calls since 
 * equivalent concept clique resolution is a global operation.
 * 
 * Added a new global application cache to minimize lookups for equivalent concept id's 
 * to ensure that the system doesn't repetitively retrieve the same cliques
 * from the back end data sources more than once in an application session(?)
 *
 *  One concern I have is whether or not the ConceptClique cached is "complete" when
 *  indexed in the cache. The conceptIds are a list of conceptId.. 
 *  Every conceptId in the clique ought to be cached to the same clique...
 *  We may have to tweak the Cache behaviour for non-redundant multi-key caching?
 */
@Service
public class ExactMatchesHandler {
	
	private static Logger _logger = LoggerFactory.getLogger(ExactMatchesHandler.class);
	
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;
	@Autowired private KnowledgeBeaconService kbs;
	
	@Autowired @Qualifier("Global")
	private Cache cache;
	
	
	public ConceptClique assignAccessionId(ConceptClique clique) {
		clique.assignAccessionId();
		return clique;
	}
	/**
	 * Builds up concept cliques for each conceptId in {@code c}, and then merges them into a single
	 * set of conceptIds and returns this set.
	 * @param conceptIds
	 * @param sources 
	 * @return
	 */
	/*
	 *  RMB (Sept 2017) - Revised this function to return a merged ConceptClique object every time.
	 *  TODO: This function should be reviewed for "complete" (once only) clique construction and indexing
	 *  that is, the first time *any* conceptId member of the clique is encountered, *all* clique 
	 *  memberIds should be used to index the resulting ConceptClique
	 */
	public ConceptClique getExactMatchesSafe(List<String> conceptIds) {
		
		// TODO: a "multi-key" indexed searchForEntity() should be created?
		CacheLocation cacheLocation = 
				cache.searchForEntity( "ConceptClique", "", conceptIds.toArray(new String[]{}) );
		
		ConceptClique cacheResult = (ConceptClique)cacheLocation.getEntity() ;
		
		ConceptClique theClique = null ;
		
		if(cacheResult==null) {
			
			List<Map<String, Object>> l = conceptCliqueRepository.getConceptCliques(conceptIds);
			
			List<ConceptClique> cliques = new ArrayList<ConceptClique>();
			List<String> unmatchedConceptIds = new ArrayList<String>(conceptIds);
			
			for (Map<String, Object> m : l) {
				List<String> matchedConceptIds = Arrays.asList((String[]) m.get("matchedConceptIds"));
				ConceptClique clique = (ConceptClique) m.get("clique");
				
				/*
				 *  Probably done in setId() during loading ... 
				 *  but no harm calling this now, just in case,
				 *  to ensure a normalized accessionId CURIE 
				 */
				clique.assignAccessionId();
				
				unmatchedConceptIds.removeAll(matchedConceptIds);
				cliques.add(clique);
			}
			
			if ( ! unmatchedConceptIds.isEmpty() ) {
				//Set<String> union = ConceptClique.unionOfConceptIds(cliques);
				//return new ArrayList<String>(union);
				//return mergeCliques(cliques);
			//} else {
				
				List<ConceptClique> foundCliques = unmatchedConceptIds.stream().map(
						conceptId -> new ConceptClique(findAggregatedExactMatches(conceptId))
				) // heuristically assign the proper accessionId to each clique
				.map(clique->assignAccessionId(clique)).collect(Collectors.toList());
				
				//foundCliques.forEach(clique -> conceptCliqueRepository.save(clique));
				for(ConceptClique clique : foundCliques) {
					conceptCliqueRepository.save(clique);
				}
				
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
										// Normalize the accessionId CURIE
										theClique.assignAccessionId();
									}
								}
							}
						}
					//}
				
			} else {
				// Last ditch desperate effort?
				theClique = getExactMatchesUnsafe( conceptIds );
			}
			
			// putting fetched result to cache
			// TODO: a "multi-key" indexed setEntity() should be created
			// i.e. cacheLocation.setEntity(theClique,theClique.getConceptIds());
			cacheLocation.setEntity(theClique);
			_logger.trace("ConceptClique constructed from beacons");

		} else {
			_logger.trace("ConceptClique fetched from cached data");
			theClique = cacheResult;
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
	public ConceptClique getExactMatchesUnsafe(List<String> c) {
		
		if(c.isEmpty()) return null;
		
		ConceptClique conceptClique = conceptCliqueRepository.getConceptClique(c);
		
		if (conceptClique != null) {
			// call this to ensure normalization of retrieved accessionId CURIE
			conceptClique.assignAccessionId(); 
		} else {
			conceptClique = new ConceptClique(findAggregatedExactMatches(c));
		}
		conceptClique = conceptCliqueRepository.save(conceptClique);
		return conceptClique;
	}
	
	private Set<String> findAggregatedExactMatches(List<String> c) {
		Set<String> matches = new HashSet<String>(c);
		int size;
		
		do {
			size = matches.size();
			CompletableFuture<List<String>> future = kbs.getExactMatchesToConceptList(new ArrayList<String>(matches));
			
			try {
				List<String> aggregatedMatches = 
						future.get(
								2*KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,  
								KnowledgeBeaconService.BEACON_TIMEOUT_UNIT 
						);
				matches.addAll(aggregatedMatches);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}
			
		} while (size < matches.size());
		
		return matches;
	}
	
	private Set<String> findAggregatedExactMatches(String conceptId) {
		return findAggregatedExactMatches(Arrays.asList(new String[]{conceptId}));
	}
}
