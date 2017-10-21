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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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

import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.database.repository.aggregator.BeaconConceptSubCliqueRepository;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.model.BioNameSpace;
import bio.knowledge.model.aggregator.BeaconConceptSubClique;
import bio.knowledge.model.aggregator.ConceptClique;
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
	@Autowired private BeaconConceptSubCliqueRepository subCliqueRepository;
	
	@Autowired private KnowledgeBeaconRegistry registry;

	@Autowired private KnowledgeBeaconService kbs;
	
	@Autowired @Qualifier("Global")
	private Cache cache;
	
	
	public ConceptClique assignAccessionId(ConceptClique clique) {
		
		List<String> conceptIds = clique.getConceptIds();
		
		// Heuristic in Java code to set a reasonable "equivalent concept clique" canonical identifier
		// (See also bio.knowledge.database.repository.ConceptCliqueRepository.accessionIdFilter)
		
		if(conceptIds.isEmpty()) {
			_logger.error("assignAccession(): clique set of concept ids is empty??!");
			return clique;
		}
		
		String accessionId = null ;
		
		// Detect matches in the BioNameSpace in order of precedence?
		for (BioNameSpace namespace : BioNameSpace.values()) {
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
				
				// ignore case for namespace detection
				if( idPart[0].equalsIgnoreCase(namespace.name()) 
				) {
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
					accessionId = namespace.name()+":"+idPart[1];
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
			accessionId = conceptIds.get(0);
		}
		
		// Best guess accessionId is set here
		clique.setId(accessionId);
		
		return clique;
	}
	
	public ConceptClique getClique(String cliqueId) {
		
		CacheLocation cacheLocation = 
				cache.searchForEntity( "ConceptClique", "ByCliqueId", new String[]{ cliqueId } );
		
		ConceptClique cacheResult = (ConceptClique)cacheLocation.getEntity() ;
		
		ConceptClique theClique = null ;
		
		if(cacheResult==null) {
			
			theClique = conceptCliqueRepository.getConceptCliqueById(cliqueId);
			
			if(theClique!=null) {
				
				// putting fetched result to in-memory cache
				cacheLocation.setEntity(theClique);
				_logger.debug("ConceptClique retrieved from the database and placed in the cache");
				
			} else {
				
				/*
				 *  "silent" failure - shouldn't normally happen since 
				 *  all cliqueIds are only published by the beaconAggregator
				 *  for ConceptClique objects normally saved to the database?
				 */
				_logger.error("ConceptClique for clique id '"+
				 cliqueId+"' could NOT be retrieved from the database?");
			}

		} else {
			_logger.trace("ConceptClique fetched by cliqueId from cached data");
			theClique = cacheResult;
		}
		
		return theClique;
	}
	
	public static boolean notDisjoint(ConceptClique clique1, ConceptClique clique2) {
		return ! Collections.disjoint(clique1.getConceptIds(), clique2.getConceptIds());
	}
	

	public static Set<String> unionOfConceptIds(Collection<ConceptClique> cliques) {
		return cliques.stream().map(
				clique -> { return clique.getConceptIds(); }
		).flatMap(List::stream).collect(Collectors.toSet());
	}

	private <T> List<T> listOfOne(T item) {
		List<T> list = new ArrayList<T>();
		list.add(item);
		return list;
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
	 *  TODO: This function should be reviewed for "complete" (once only) clique construction and 
	 *  indexing, that is, the first time *any* conceptId member of the clique is encountered, 
	 *  *all* clique associated concept identifiers should be used to index the resulting ConceptClique
	 */
	public ConceptClique getExactMatches( KnowledgeBeaconImpl beacon, String conceptId ) {
		
		// TODO: a "multi-key" indexed searchForEntity() should be created?
		CacheLocation conceptIdsCacheLocation = 
				cache.searchForEntity( "ConceptClique", "ByConceptIds", new String[]{conceptId} );
		
		ConceptClique cacheResult = (ConceptClique)conceptIdsCacheLocation.getEntity() ;
		
		ConceptClique theClique = null ;
		
		if(cacheResult==null) {
			
			List<Map<String, Object>> l = conceptCliqueRepository.getConceptCliques(listOfOne(conceptId));
			
			List<ConceptClique> cliques = new ArrayList<ConceptClique>();
			List<String> unmatchedConceptIds = listOfOne(conceptId);
			
			for (Map<String, Object> m : l) {
				
				List<String> matchedConceptIds = Arrays.asList((String[]) m.get("matchedConceptIds"));
				ConceptClique clique = (ConceptClique) m.get("clique");
				
				/*
				 *  Probably done in setId() during loading ... 
				 *  but no harm calling this now, just in case,
				 *  to ensure a normalized accessionId CURIE 
				 */
				clique = assignAccessionId(clique);
				
				unmatchedConceptIds.removeAll(matchedConceptIds);
				cliques.add(clique);
			}
			
			if ( ! unmatchedConceptIds.isEmpty() ) {
				
				// Attempt additional matches of each singular unmatched concept id...
				List<ConceptClique> foundCliques = unmatchedConceptIds.stream().map(
						id -> findAggregatedExactMatches( beacon.getId(), id)
						
				) // and heuristically assign the proper accessionId to each clique
				.map(clique->assignAccessionId(clique)).collect(Collectors.toList());
				
				List<ConceptClique> savedCliques = new ArrayList<ConceptClique>();
				for(ConceptClique clique : foundCliques) {
					unmatchedConceptIds.removeAll(clique.getConceptIds()) ;
					savedCliques.add( conceptCliqueRepository.save(clique) );
				}
				
				cliques.addAll(savedCliques);
			}
			
			/*
			 *  If some identifier(s) still not matched,
			 *  then create their own clique?
			 */
			if ( ! unmatchedConceptIds.isEmpty() ) {
				
				ConceptClique orphanClique = new ConceptClique();
				orphanClique.addConceptIds(beacon.getId(), unmatchedConceptIds);
				orphanClique = assignAccessionId(orphanClique);
				orphanClique = conceptCliqueRepository.save(orphanClique);
				cliques.add(orphanClique);
			}
			
			/*
			 *  Merging of found subcliques? TODO: This can be put 
			 *  inside a thread to speed things up. All we're doing 
			 *  here is cleaning up the database without changing 
			 *  what this method will return. RMB: PRACTICAL ISSUE: 
			 *  THIS IMPLIES THAT CLIQUES MAY NOT HAVE STABLE IDENTITY? 
			 *  PERHAPS PROBLEMATIC UPSTREAM CLIENTS LIKE TKBIO ?
			 */
			if(cliques.size()!=0) {
				if(cliques.size()==1) {
					theClique = cliques.get(0) ;
				} else {
					for (ConceptClique clique1 : cliques) {
						for (ConceptClique clique2 : cliques) {
							if (clique1 != clique2) {
								if (notDisjoint(clique1, clique2)) {
									/*
									 * Figuring out how to do the Cypher merge of the new type of cliques is a bit too challenging
									 * for now (given the lack of time) so I'll attempt an in memory merge instead, with saving of the 
									 * new clique followed by deletion of the old ones.
									 * 
									 * theClique = conceptCliqueRepository.mergeConceptCliques(clique1.getDbId(), clique2.getDbId());
									 */
									theClique = mergeConceptCliques(clique1,clique2);
								}
							}
						}
					}
				}
			
				// putting fetched result to caches
				// ... cached by input concept ids...
				conceptIdsCacheLocation.setEntity(theClique);
				
				// .. cached by resulting clique id...
				CacheLocation cliqueIdCacheLocation = 
						cache.searchForEntity( "ConceptClique", "ByCliqueId", new String[]{ theClique.getId() } );
				cliqueIdCacheLocation.setEntity(theClique);
			
				_logger.trace("ConceptClique constructed from beacons and placed in the cache");
			} 

		} else {
			
			_logger.trace("ConceptClique fetched by conceptIds from cached data");
			theClique = cacheResult;
		}
		
		return theClique;
	}
	
	/*
	 * This is a rather elaborate, programmatic algorithm for merging two
	 * cliques. Unfortunately, time pressures prevent thoughtful designs
	 * of a more efficient, perhaps Neo4j Cypher query language driven procedure.
	 * 
	 * TODO: Design a database-side Cypher or stored function/procedure for merging!
	 */
	private ConceptClique mergeConceptCliques(ConceptClique clique1, ConceptClique clique2) {
		
		Set<BeaconConceptSubClique> subcliques = new HashSet<BeaconConceptSubClique>() ;
		Set<String> intersectingBeaconIds      = new HashSet<String>() ;
		
		/*
		 *  Merge the subcliques with common beacon id's, 
		 *  alas, is worst case, a b1.size x b2.size order N-squared procedure 
		 *  that could get ugly as the number of beacon cliques gets large?
		 *  I try my best here to hack an efficient Java-based algorithm.
		 *  Probably overkill for small numbers of beacon subcliques but
		 *  significantly more efficient as the number of beacons increases?
		 */
		
		List<BeaconConceptSubClique> scList1 = new ArrayList<BeaconConceptSubClique>(clique1.getBeaconSubCliques());
		Iterator<BeaconConceptSubClique> scIt1 = scList1.iterator();

		List<BeaconConceptSubClique> scList2 = new ArrayList<BeaconConceptSubClique>(clique2.getBeaconSubCliques());
		Iterator<BeaconConceptSubClique> scIt2 = scList2.iterator();
		
		// pick the smallest list as the inner loop iterator since it'll be the smallest to consume?
		// don't care if they are the same size
		Iterator<BeaconConceptSubClique> inner, outer; 
		if( scList1.size()<scList2.size() ) {
			inner = scIt1;
			outer = scIt2;
		} else {
			inner = scIt2;
			outer = scIt1;
		}
		
		/*
		 *  ... because iterators allow item removal, 
		 *  to effectively prune the inner loop iterations...
		 */
		while(outer.hasNext()) {
			
			BeaconConceptSubClique b1 = outer.next();
			
			while(inner.hasNext()) {
				
				BeaconConceptSubClique b2 = inner.next();
				
				if(b1.getId().equals(b2.getId())) {
					
					Set<String> union = new HashSet<String>(b1.getConceptIds()) ;
					union.addAll(b2.getConceptIds());
					
					/*
					 * Create a new merged subclique but defer 
					 * saving it to the database until later(see below)
					 */
					BeaconConceptSubClique mergedSubClique = new BeaconConceptSubClique( b1.getId() );
					mergedSubClique.addConceptIds(new ArrayList<String>(union));
					subcliques.add(mergedSubClique);
					
					// I need to know which beaconId's got matched up
					intersectingBeaconIds.add(b1.getId());
	
					// prune the intersection sets
					scIt1.remove();
					scIt2.remove();
					
					break;  
				} 
			}
		}

		/*
		 *  Harvesting non-intersecting subcliques from both cliques
		 *  
		 *  TODO: could this procedure perhaps somehow be integrated for efficiency into the above iteration loops??
		 */
		for( BeaconConceptSubClique b1 : clique1.getBeaconSubCliques() ) {
			if(! intersectingBeaconIds.contains( b1.getId() ) ) {
				BeaconConceptSubClique mergedSubClique = new BeaconConceptSubClique( b1.getId() );
				mergedSubClique.addConceptIds( b1.getConceptIds() );
				subcliques.add( mergedSubClique );
			}
		}

		for( BeaconConceptSubClique b2 : clique2.getBeaconSubCliques()) {
			if(! intersectingBeaconIds.contains( b2.getId() ) ) {
				BeaconConceptSubClique mergedSubClique = new BeaconConceptSubClique( b2.getId() );
				mergedSubClique.addConceptIds( b2.getConceptIds() );
				subcliques.add( mergedSubClique );
			}
		}
		
		// Save the newly created subcliques
		Iterable<BeaconConceptSubClique> savedSubcliques = subCliqueRepository.save(subcliques) ;

		// Create, load and save a new merged clique
		ConceptClique mergedClique = new ConceptClique();
		
		for(BeaconConceptSubClique subclique : savedSubcliques) 
			mergedClique.addBeaconSubclique(subclique);
		
		// Set the merged Clique's accessionId CURIE
		mergedClique = assignAccessionId(mergedClique);
		
		//... then save it...
		mergedClique = conceptCliqueRepository.save(mergedClique) ;
		
		// Then, delete the old subcliques...
		List<BeaconConceptSubClique> defunctSubcliques = clique1.removeBeaconSubcliques() ;
		defunctSubcliques.addAll(clique2.removeBeaconSubcliques());
		subCliqueRepository.delete(defunctSubcliques);
		
		//... and the two Cliques
		List<ConceptClique> defunctCliques = new ArrayList<ConceptClique>();
		defunctCliques.add(clique1);
		defunctCliques.add(clique2);
		conceptCliqueRepository.delete(defunctCliques);
		
		return mergedClique;
	}

	/**
	 * Performs a similar function as {@code getExactMatchesSafe(List<String>)}, but
	 * <b>assumes that {@code c} is a list of exact matches</b>. If this assumption fails to
	 * hold true, the database could be populated with faulty concept cliques. Because of this,
	 * this method is much faster than {@code getExactMatchesSafe}. Note that if {@code c.size() == 1},
	 * then the assumption is trivially satisfied and this method can be used safely.
	 * 
	 * @param conceptId
	 * 		A list of <b>exactly matching</b> concept ID's
	 * @return
	 *   UNUSED FUNCTION - I comment it out for now/
	public ConceptClique getExactMatchesUnsafe(List<String> conceptId) {
		
		if(conceptId.isEmpty()) return null;
		
		ConceptClique conceptClique = conceptCliqueRepository.getConceptClique(conceptId);
		
		if (conceptClique != null) {
			// call this to ensure normalization of retrieved accessionId CURIE
			assignAccessionId(conceptClique); 
		} else {
			conceptClique = findAggregatedExactMatches(conceptId);
		}
		conceptClique = conceptCliqueRepository.save(conceptClique);
		
		return conceptClique;
	}
	*/
	
	private ConceptClique findAggregatedExactMatches( String sourceBeaconId, String conceptId ) {
		
		ConceptClique clique = new ConceptClique();
		
		Set<String> matches = new HashSet<String>() ;
		matches.add(conceptId);
		
		int size;
		
		do {
			size = matches.size();
			
			CompletableFuture<Map<KnowledgeBeaconImpl, List<String>>> future = 
						kbs.getExactMatchesToConceptList( new ArrayList<String>(matches), registry.getBeaconIds() ) ;
			
			try {
				Map<KnowledgeBeaconImpl, List<String>> aggregatedMatches = 
						future.get(
								/*
								 *  Try scaling the timeout up proportionately 
								 *  to the number of concept ids being matched?
								 */
								matches.size()*KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,  
								KnowledgeBeaconService.BEACON_TIMEOUT_UNIT 
						);

				for(KnowledgeBeaconImpl beacon : aggregatedMatches.keySet()) {
					
					List<String> beaconMatches = aggregatedMatches.get(beacon);
					
					/* 
					 * Subtle challenge here: if the beacon reports new matches,
					 * then that implies that it recognized at least one of the
					 * input concept ids, which means that these are also part
					 * of the equivalent concept subclique... but which one of the
					 * input ids was specifically recognized may be obscure?
					 * 
					 * 1.0.14 API needs to be changed to also return the input 
					 * identifiers used to match the new identifiers, i.e. to
					 * truly return the full 'subclique' of identifiers known
					 * by a given beacon.
					 * 
					 */
					// Only record non-empty subcliques for beacons
					if(! (beaconMatches==null || beaconMatches.isEmpty() ) ) {
						clique.addConceptIds( beacon.getId(), beaconMatches );
						matches.addAll(beaconMatches);
					}
				}
				
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}
			
		} while (size < matches.size());
		
		if(clique.getConceptIds().isEmpty()) 
			/*
			 *  Nothing new was matched in the beacons 
			 *  so 'matches' only has the original conceptId?
			 */
			clique.addConceptIds( sourceBeaconId, listOfOne(conceptId) );
		
		/*
		 *  With available equivalent concept id's gathered, 
		 *  choose a leader then return the clique!
		 */
		return clique;
	}
	
	/*
	 * Redundant functionality here to query a 
	 * list of concept ids since now always 
	 * only used for a list of one

	private ConceptClique findAggregatedExactMatches(String conceptId) {
		return findAggregatedExactMatches(Arrays.asList(new String[]{conceptId}));
	}
	*/
}
