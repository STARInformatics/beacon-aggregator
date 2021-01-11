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
package bio.knowledge.server.controller;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import bio.knowledge.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.ConceptCategoryService;
import bio.knowledge.aggregator.ConceptCliqueService;
import bio.knowledge.aggregator.Curie;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.client.model.ExactMatchResponse;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.model.CURIE;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.server.blackboard.CliqueService;
import bio.knowledge.server.controller.Cache.CacheLocation;

import static java.util.stream.Collectors.toList;

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
public class ExactMatchesHandler implements Curie {
	
	private static Logger _logger = LoggerFactory.getLogger(ExactMatchesHandler.class);
	
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;
	
	@Autowired private KnowledgeBeaconRegistry registry;

	@Autowired private KnowledgeBeaconService kbs;
	
	@Autowired private ConceptCategoryService conceptTypeService;
	
	@Autowired private ConceptCliqueService conceptCliqueService;
	
	//@Autowired private Blackboard blackboard;
	
	@Autowired @Qualifier("Global")
	private Cache cache;
	
	public Neo4jConceptClique getClique2(String cliqueId) {
		return conceptCliqueRepository.getConceptCliqueById(cliqueId);
	}

	public Neo4jConceptClique getClique(String cliqueId) {
		
		// Check in-memory cache first...
		CacheLocation cacheLocation = 
				cache.searchForEntity( "ConceptClique", cliqueId, new String[]{ cliqueId } );
		
		Neo4jConceptClique theClique = (Neo4jConceptClique)cacheLocation.getEntity();
		
		if( theClique == null ) {
			
			// ... then check the remote database...
			try {
				theClique = conceptCliqueRepository.getConceptCliqueById(cliqueId);
			} catch(Exception e) {
				_logger.warn(e.getMessage());
			}
			
			if( theClique != null ) {
				
				/*
				 * Patch to ensure that legacy 
				 * Semantic Groups are properly
				 * set coming from the database?
				 */
				String conceptType = theClique.getConceptCategory();
				
				Neo4jConceptCategory category = conceptTypeService.lookUpByIdentifier(conceptType);
				if(category!=null)
					theClique.setConceptType(category.getName());
				else
					theClique.setConceptType(BiolinkTerm.NAMED_THING.getLabel());
				
				// put fetched result to in-memory cache for future reference?
				cacheLocation.setEntity(theClique);
				
				_logger.info("ConceptClique retrieved from the database and placed in the cache");
				
			} else {
				
				/*
				 *  "silent" failure - probably shouldn't normally happen since 
				 *  all cliqueIds are only published by the beaconAggregator
				 *  for ConceptClique objects normally saved to the database?
				 */
				_logger.info("ConceptClique for clique id '"+
				 cliqueId+"' could NOT be retrieved from the database?");
			}

		} else {
			
			_logger.trace("ConceptClique fetched by cliqueId from cached data");
		}
		
		return theClique; // may still be null
	}
	
	public static boolean notDisjoint(Neo4jConceptClique clique1, Neo4jConceptClique clique2) {
		return ! Collections.disjoint(clique1.getConceptIds(), clique2.getConceptIds());
	}
	

	public static Set<String> unionOfConceptIds(Collection<Neo4jConceptClique> cliques) {
		return cliques.stream().map(
				clique -> { return clique.getConceptIds(); }
		).flatMap(List::stream).collect(Collectors.toSet());
	}

	private <T> List<T> listOfOne(T item) {
		List<T> list = new ArrayList<T>();
		list.add(item);
		return list;
	}
	
	/*
	 * Due diligence saving to avoid Clique duplication in the database
	 */
	private Neo4jConceptClique archive( Neo4jConceptClique clique ) {
		clique = conceptCliqueRepository.save(clique);

		List<Neo4jConceptClique> overlappingCliques = conceptCliqueRepository.getOverlappingCliques(clique.getDbId());

		for (Neo4jConceptClique overlappingClique : overlappingCliques) {
			conceptCliqueService.mergeConceptCliques(clique, overlappingClique);
		}

		return clique;
	}
	

	
	/*
	 * Merge a list of cliques deemed equivalent into one clique.
	 * Purge the old cliques from the database along the way?
	 */
	private Neo4jConceptClique mergeCliques(List<Neo4jConceptClique> cliques, Set<Neo4jConceptCategory> types) {
		
		Neo4jConceptClique theClique = cliques.get(0);
		
		if( theClique.getDbId() != null ) {
			conceptCliqueRepository.deleteById(theClique.getDbId());
			theClique.setDbId(null);
		}
		
		theClique.setConceptType(curieSet(types));
		
		for(int i = 1 ; i < cliques.size() ; i++ ) {
			
			Neo4jConceptClique other = cliques.get(i);
			
			conceptCliqueService.mergeConceptCliques(theClique,other);
			
//			// moved into mergeConceptCliques
//			if( other.getDbId() != null ) 
//				conceptCliqueRepository.delete(other);
		}
		
		// Refresh the accession identifier
		conceptCliqueService.assignAccessionId(theClique);
		
		return theClique;
	}
	
	private List<Neo4jConceptClique> checkForSymbols(String conceptName) {
		
		// ignore flanking whitespace, if any...
		conceptName = conceptName.trim();
		
		/*
		 *  Crude filter: 
		 *  the interior of symbol names should 
		 *  not have blanks or commas; 
		 *  Some beacons may violate this (e.g. Biolink?)
		 *  but their beacon wrappers need to fix the issue somehow?
		 */
		if(  
			conceptName.indexOf(" ")!=-1 ||
			conceptName.indexOf(",")!=-1
			
		) return new ArrayList<>();
		
		List<Neo4jConceptClique> cliques = new ArrayList<Neo4jConceptClique>();
		
		/*
		 *  Special deep search for Genes that 
		 *  have names that are potential CURIE object ids
		 *  e.g. HGNC.SYMBOL, GENECARD, etc. symbols?
		 *  TODO: Are there other namespaces to be probed for symbols?
		 */
		for(String prefix : new String[] {"HGNC.SYMBOL","genecards"}) {
			
			String testCurie = prefix+":"+conceptName;
			
			/*
			 *  Shallow search for an existing clique that matches? 
			 *  This may not always work the first time, but once 
			 *  the symbol is registered legitimately by some other 
			 *  clique, then it will likely be merged here.
			 */
			Neo4jConceptClique testClique = getClique(testCurie) ;
			
			if( testClique != null) { 
				cliques.add(testClique) ;
			}
		}
		
		return cliques;
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param conceptId
	 * @param conceptName
	 * @param categories
	 * @return
	 */
	public Neo4jConceptClique getExactMatches(
			Integer beaconId, 
			String conceptId, 
			String conceptName,
			Set<Neo4jConceptCategory> categories
	) {
		Neo4jConceptClique clique = getConceptCliqueFromDb(new String[]{conceptId});
		
		if (clique == null) {
			Optional<Neo4jConceptClique> optional = 
					compileConceptCliqueFromBeacons(conceptId,conceptName,categories);
			
			if (optional.isPresent()) {
				clique = optional.get();
			}
		}
		
		return clique;
	}
	
	/**
	 * 
	 * @param beacon
	 * @param conceptId
	 * @param conceptName
	 * @param types
	 * @return
	 */
	public Neo4jConceptClique getExactMatches( 
			KnowledgeBeacon beacon, 
			String conceptId, 
			String conceptName,
			Set<Neo4jConceptCategory> types
	) {
		final Integer beaconId = beacon.getId();
		return getExactMatches( beaconId,  conceptId, conceptName, types );
	}
	
	
	/*
	 *  RMB (Sept 2017) - Revised this function to return a merged ConceptClique object every time.
	 *  TODO: This function should be reviewed for "complete" (once only) clique construction and 
	 *  indexing, that is, the first time *any* conceptId member of the clique is encountered, 
	 *  *all* clique associated concept identifiers should be used to index the resulting ConceptClique
	 */
	/**
	 * Builds up concept cliques for each conceptId in {@code c}, and then merges them into a single
	 * set of conceptIds and returns this set.
	 *  
	 * @param beaconId
	 * @param conceptId
	 * @param conceptName
	 * @param types
	 * @return
	 */
//	public Neo4jConceptClique getExactMatches_old(
//			Integer beaconId,
//			String conceptId,
//			String conceptName,
//			Set<Neo4jConceptCategory> types
//	) {
//		Boolean updateCache = true ;
//
//		CacheLocation conceptIdsCacheLocation =
//				cache.searchForEntity( "ConceptClique", conceptId, new String[]{conceptId} );
//
//		Neo4jConceptClique cacheResult = (Neo4jConceptClique)conceptIdsCacheLocation.getEntity() ;
//
//		Neo4jConceptClique theClique = null;
//
//		/*
//		 * Iterative time-based learning: Re-check if some other
//		 * clique more recently registered a symbol match,
//		 * e.g. GENE symbol; If so, we should (re-)merge the cliques
//		 */
//
//		 /*
//		  * NOTE: Oddly enough, some beacons such as Biolink return a
//		  * comma-separated list of gene symbols!?!??
//		  * 		  *
//		  *	String[] candidates = conceptName.split(",");
//		  *	for( String symbol : candidates ) {
//		  *		checkForSymbols( symbol, cliques ) ;
//		  *	}
//		  *
//		  * NOV 11, 2017 Observation:
//		  * But, parsing these out may cause more
//		  * spurious merges between concepts with names
//		  * that contain embedded gene symbols that
//		  * are NOT a simple enumeration of symbols?
//		  *
//		  * MAYBE BETTER TO FIX THE BIOLINK BEACON DIRECTLY
//		  * AND ONLY CHECK FOR SINGULAR SYMBOLS IN THE CONCEPT NAME?
//		  */
//		List<Neo4jConceptClique> cliques = checkForSymbols(conceptName) ;
//
//		if( cacheResult != null ) {
//
//			theClique = cacheResult;
//
//			_logger.debug("Concept Clique fetched by conceptIds from cached data. No additional symbol matches (yet)");
//
//			updateCache = false;
//		}
//
//		if( ! cliques.isEmpty() ) {
//
//			/*
//			 *  Cached result not enough now?
//			 *  Also wanna update with new information...
//			 */
//			if( theClique != null ) cliques.add( theClique ) ;
//
//			updateCache = true;
//		}
//
//		if( updateCache ) {
//
//			/*
//			 * Try to construct or retrieve additional cliques
//			 * with other criteria from some other location...
//			 *
//			 *  First, try the shortcut of checking the
//			 *  special case of whether the given conceptId CURIE
//			 *  can be normalized then interpreted as a cliqueId
//			 */
//
//			theClique = getClique( CURIE.makeNormalizedCurie(conceptId) ) ;
//
//			if( theClique != null) {
//
//				_logger.debug("Existing Concept Clique '"+theClique.getId()+"' fetched from the database?");
//
//				cliques.add(theClique); // may still need to merge with fresh symbol data?
//
//			}  else {
//
//				// Otherwise, broaden the search
//
//				// Directly by the CURIE...
//				List<Map<String, Object>> l =
//						conceptCliqueRepository.getConceptCliques(listOfOne(conceptId));
//
//				List<String> unmatchedConceptIds = listOfOne(conceptId);
//
//				for (Map<String, Object> m : l) {
//
//					List<String> matchedConceptIds = Arrays.asList((String[]) m.get("matchedConceptIds"));
//					Neo4jConceptClique clique = (Neo4jConceptClique) m.get("clique");
//
//					unmatchedConceptIds.removeAll(matchedConceptIds);
//
//					cliques.add(clique);
//				}
//
//				if ( ! unmatchedConceptIds.isEmpty() ) {
//
//					// Attempt additional matches of each singular unmatched concept id...
//					List<Neo4jConceptClique> foundCliques =
//							unmatchedConceptIds.stream()
//							.map( id -> findAggregatedExactMatches( beaconId, id, types ) )
//							.collect(toList());
//
//					for(Neo4jConceptClique clique : foundCliques) {
//						unmatchedConceptIds.removeAll(clique.getConceptIds()) ;
//					}
//
//					cliques.addAll(foundCliques);
//				}
//
//				/*
//				 *  If some identifier(s) are still not matched,
//				 *  then put them in their own clique?
//				 */
//				if ( cliques.isEmpty() ) {
//					Neo4jConceptClique orphanClique = new Neo4jConceptClique(curieSet(types));
//					orphanClique.addConceptIds( beaconId, unmatchedConceptIds );
//					conceptCliqueService.assignAccessionId(orphanClique);
//					cliques.add(orphanClique);
//				}
//
//				if(cliques.isEmpty())
//					throw new RuntimeException(
//							"getExactMatches() ERROR: empty clique impossible here? "
//									+ "should have at least an orphan clique?)"
//							);
//			}
//
//			/*
//			 * Select the sole clique to process further or
//			 * flatten a set of multiply discovered cliques into one
//			 */
//			if(cliques.size()==1)
//				theClique = cliques.get(0) ;
//
//			else
//				// Two or more cliques to merge?
//				theClique = mergeCliques(cliques,types);
//
//			_logger.debug("Canonical Concept Clique assembled from database and/or beacon information");
//
//			/*
//			 * (Re-)add the seed conceptId to the clique, just in case
//			 * the original identifier is a letter case variant or it
//			 * didn't somehow get added in the various beacon queries.
//			 */
//			theClique.addConceptId( beaconId, conceptId );
//
//			// fresh the id and semantic group, just in case
//			conceptCliqueService.assignAccessionId(theClique);
//
//			// Update the remote database
//			theClique = archive(theClique) ;
//
//			// putting fetched result into the in-memory cache
//
//			/*
//			 *  ... cached by every subclique concept id
//			 *  (may overwrite previously partial cliques cached against an identifier)
//			 */
//			cache.setMultiCachedEntity( "ConceptClique", theClique.getConceptIds(), theClique ) ;
//
//			// .. cached by resulting clique id...
//			CacheLocation cliqueIdCacheLocation =
//					cache.searchForEntity( "ConceptClique", theClique.getId(), new String[]{ theClique.getId() } );
//
//			cliqueIdCacheLocation.setEntity(theClique);
//
//			_logger.debug( "Concept Clique "+theClique.getId()+
//					" archived in the database and cached in memory?");
//		}
//
//		return theClique;
//	}

	/*
	 * Polls all the beacons to find exact matches and aggregate them into a single clique.
	 * The 'sourceBeaconId' is the original authority for the CURIE concept id which seeds the clique assembly.
	 */
	private Neo4jConceptClique findAggregatedExactMatches(
			Integer sourceBeaconId, 
			String conceptId, 
			Boolean isTesting, 
			Set<Neo4jConceptCategory> categories
		) throws ApiException {
		
		Neo4jConceptClique clique = createConceptClique(conceptId, sourceBeaconId, categories);
		
		boolean failedTest = isTesting && clique.size() <= 1;
		
		if (failedTest) {
			return null;
		}
		
		clique.setConceptType(curieSet(categories));
		
		return clique;
	}
	
	/**
	 * Returned object might be different instance from original
	 * 
	 * These are the side effects of getExactMatches
	 */
	public Neo4jConceptClique addInformationToClique(
			Neo4jConceptClique clique,
			Integer beaconId,
			String conceptId,
			String conceptName,
			Set<Neo4jConceptCategory> types
	) {
		List<Neo4jConceptClique> cliques = checkForSymbols(conceptName);
		
		cliques.add(clique);
		
		if (cliques.size() != 1) {
			clique = mergeCliques(cliques, types);
		}
		
		clique.addConceptId(beaconId, conceptId);
		
		conceptCliqueService.assignAccessionId(clique);
		
		clique = archive(clique);
		
		return clique;
	}
	
	/**
	 * 
	 * @param conceptId
	 * @param beaconId
	 * @param categories
	 * @return
	 */
	public Neo4jConceptClique createConceptClique(String conceptId, Integer beaconId, Set<Neo4jConceptCategory> categories) throws ApiException {
		String categoryString = conceptTypeService.getDelimitedString(categories);
		return createConceptClique(conceptId, beaconId, categoryString);
	}
	
	@Autowired CliqueService cliqueService;
	private static final int DEFAULT_BEACON_ID = 0;
	
	/**
	 * 
	 * @param conceptId
	 * @param beaconId
	 * @param categoryString
	 * @return
	 */
	public Neo4jConceptClique createConceptClique(String conceptId, Integer beaconId, String categoryString) throws ApiException {
		
		Set<String> clique = cliqueService.getClique(conceptId);
		
		Neo4jConceptClique neo4jClique = new Neo4jConceptClique();
		
		neo4jClique.addConceptIds(DEFAULT_BEACON_ID, listOfOne(conceptId));
		conceptCliqueService.assignAccessionId(neo4jClique);
		neo4jClique.setConceptType(categoryString);
		neo4jClique = archive(neo4jClique);
		return neo4jClique;
	}

	/**
	 * 
	 * @param conceptId
	 * @param conceptName
	 * @param categories
	 * @return
	 */
	public Optional<Neo4jConceptClique> compileConceptCliqueFromBeacons(
			String conceptId, String conceptName, Set<Neo4jConceptCategory> categories
	) {
		String categoryString =
				conceptTypeService.getDelimitedString(categories);
		return compileConceptCliqueFromBeacons(conceptId,conceptName,categoryString);
	}
	
	/**
	 * Polls all the beacons to find exact matches and aggregate them into a single clique.
	 * Will return an empty ConceptClique Optional if no matches are found.
	 * 
	 * @param conceptId
	 * @param conceptName
	 * @param categoryString
	 * @return
	 */
	public Optional<Neo4jConceptClique> compileConceptCliqueFromBeacons(
			String conceptId, String conceptName, String categoryString
	) {
		try {
			Neo4jConceptClique clique = new Neo4jConceptClique();
			clique.setName(conceptName);

			clique.setConceptType(categoryString);

			Set<String> matches = new HashSet<String>() ;
			matches.add(conceptId);
			int size;

			do {
				size = matches.size();

				CompletableFuture<Map<KnowledgeBeacon, List<ExactMatchResponse>>> future = 
						kbs.getExactMatchesToConceptList( new ArrayList<String>(matches), registry.getBeaconIds() ) ;

				try {
					Map<KnowledgeBeacon, List<ExactMatchResponse>> aggregatedMatches = 
							future.get(
									matches.size()*KnowledgeBeaconService.BEACON_TIMEOUT_DURATION*2,  
									KnowledgeBeaconService.BEACON_TIMEOUT_UNIT 
									);

					for(KnowledgeBeacon beacon : aggregatedMatches.keySet()) {

						List<ExactMatchResponse> responses = aggregatedMatches.get(beacon);

						for (ExactMatchResponse response : responses) {
							if (response.getWithinDomain()) {

								List<String> beaconMatches = response.getHasExactMatches();
								beaconMatches.add(response.getId());

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
						}
					}

				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					e.printStackTrace();
				}

			} while (size < matches.size());

			if (clique.getConceptIds().isEmpty()) {
				return Optional.empty();

			} else {
				conceptCliqueService.assignAccessionId(clique);

				clique = archive(clique);

				return Optional.of(clique);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * checks whether a ConceptClique already exists in beaconMatches and merges with current clique if yes
	 * similar to mergeCliques (without type information)?
	 * @param clique
	 * @param beaconMatches
	 */
	private void mergeExistingSubcliques(Neo4jConceptClique clique, List<String> beaconMatches) {
		for (String id : beaconMatches) { 
			List<Neo4jConceptClique> overlappingCliques = conceptCliqueRepository.getOverlappingCliques(id, clique.getId());

			for (Neo4jConceptClique overlappingClique : overlappingCliques) {
				conceptCliqueService.mergeConceptCliques(clique, overlappingClique);
			}
		}
	}

	// Ordinary search for equivalent concept clique?
	public Neo4jConceptClique findAggregatedExactMatches( Integer sourceBeaconId, String conceptId, Set<Neo4jConceptCategory> types ) throws ApiException {
		return findAggregatedExactMatches( sourceBeaconId, conceptId, false, types ) ;
	}

	/**
	 * 
	 * @param identifiers
	 * @return
	 */
	public Neo4jConceptClique getConceptCliqueFromDb(String[] identifiers) {
		return conceptCliqueRepository.getConceptClique(identifiers);
	}
	
	/**
	 * @return
	 */
	public Neo4jConceptClique getConceptCliqueFromDb(String identifier) {
		return conceptCliqueRepository.getConceptCliqueByConceptId(identifier);
	}
	
	/**
	 * Gets concept clique from database if already exists or polls /exactmatches in each beacon to create and save a new
	 * clique
	 * @param conceptIds
	 * @return all cliques related to the given conceptIds (there may be duplicates if any were merged during saving)
	 */
	public List<Neo4jConceptClique> createAndGetConceptCliques(List<String> conceptIds) throws ApiException {
		List<Neo4jConceptClique> results = new ArrayList<>();
		List<String> newIds = new ArrayList<>();
		for (String conceptId : conceptIds) {
			Neo4jConceptClique clique = conceptCliqueRepository.getConceptCliqueByConceptId(conceptId);
			if (clique == null) {
				newIds.add(conceptId);
			} else {
				results.add(clique);
			}
		}
		
		results.addAll(buildCliques(newIds));
		
		return results;
	}

	private List<Neo4jConceptClique> buildCliques(List<String> newIds) throws ApiException {
		// Build all cliques first, in case some of these curie's belong to the same clique.
		for (String curie : newIds) {
			cliqueService.getClique(curie);
		}

		List<Neo4jConceptClique> cliques = new ArrayList<>();

		for (String id : newIds) {
			Neo4jConceptClique neo4jClique = new Neo4jConceptClique();

			neo4jClique.addConceptId(0, id);
			neo4jClique.addConceptIds(0, cliqueService.getClique(id));

			cliques.add(neo4jClique);
		}

		return cliques;
	}

	/**
	 * Creates new concepts cliques and returns the list by polling all beacons to find 
	 * the exact matches to the given list of ids. This method will keep polling until no new exact matches are found.
	 * 
	 * @param newIds
	 * @param beaconId
	 * @return all cliques related to the given newIds (there may be duplicates if any were merged during saving)
	 */
	@Deprecated
	private List<Neo4jConceptClique> createCliquesFromBeacons(List<String> newIds) {
		HashMap<String, Neo4jConceptClique> cliqueMap = new HashMap<>();
		for (String id : newIds) {
			cliqueMap.put(id, new Neo4jConceptClique());
		}
		
		try {
			List<String> nextIds = new ArrayList<>();
			nextIds.addAll(newIds);
			
			HashMap<String, String> originalIdMap = new HashMap<>();
			for (String id : newIds) {
				originalIdMap.put(id, id);
			}
			
			while (!nextIds.isEmpty()) {
				List<List<String>> partitions = partition(nextIds, 35);
				Map<KnowledgeBeacon, List<ExactMatchResponse>> matchesMap = null;
				
				for (List<String> ids : partitions) {
					CompletableFuture<Map<KnowledgeBeacon, List<ExactMatchResponse>>> future = 
							kbs.getExactMatchesToConceptList( ids, registry.getBeaconIds());
					
					Map<KnowledgeBeacon, List<ExactMatchResponse>> response = future.get(newIds.size()*KnowledgeBeaconService.BEACON_TIMEOUT_DURATION*2,  
							   KnowledgeBeaconService.BEACON_TIMEOUT_UNIT);
					
					if (matchesMap == null) {
						matchesMap = response;
					} else {
						for (KnowledgeBeacon key : response.keySet()) {
							List<ExactMatchResponse> values = matchesMap.get(key);
							values.addAll(response.get(key));
							matchesMap.put(key, values);
						}
					}
				}
				
				nextIds.clear();
				loadExactMatchesFromBeacons(nextIds, cliqueMap, matchesMap, originalIdMap);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Neo4jConceptClique> results = createCliques(cliqueMap);
		return results;
		
	}

	/**
	 * Populates nextIds with any new conceptIds found, updates the concept cliques in cliqueMap with the exact matches 
	 * found from matchesMap, and tracks new ids in the originalIdMap
	 * 
	 * This method can potentially change all of the following: nextIds, cliqueMap, and originalIdMap
	 * 
	 * @param cliqueMap - key: original conceptId; value: associated Neo4jConceptClique
	 * @param matchesMap - key: beacon; value: ExactMatchResponse from the beacon
	 * @param originalIdMap - key: conceptId; value: original conceptId that led to finding this conceptId
	 */
	private void loadExactMatchesFromBeacons(List<String> nextIds, HashMap<String, Neo4jConceptClique> cliqueMap,
			Map<KnowledgeBeacon, List<ExactMatchResponse>> matchesMap, HashMap<String, String> originalIdMap) {
		
		for (KnowledgeBeacon beacon : matchesMap.keySet()) {
			List<ExactMatchResponse> beaconMatches = matchesMap.get(beacon);
			for (ExactMatchResponse match : beaconMatches) {
				String originalId = originalIdMap.get(match.getId());
				Neo4jConceptClique clique = cliqueMap.get(originalId);
				
				if (match.getWithinDomain()) {
					clique.addConceptId(beacon.getId(), match.getId());
				}
				
				List<String> idMatches = match.getHasExactMatches();
				if (!idMatches.isEmpty()) {
					clique.addConceptIds(beacon.getId(), idMatches);
					for (String id : idMatches) {
						if (!originalIdMap.containsKey(id)) {
							originalIdMap.put(id, originalId);
							nextIds.add(id);
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Creates new concept cliques from existing cliques in cliqueMap
	 * @param cliqueMap
	 * @return all created cliques - this may result in duplicate cliques if any cliques were merged during creation
	 */
	private List<Neo4jConceptClique> createCliques(HashMap<String, Neo4jConceptClique> cliqueMap) {
		List<Neo4jConceptClique> results = new ArrayList<>();
		for (Neo4jConceptClique clique : cliqueMap.values()) {
			if (!clique.isEmpty()) {
				conceptCliqueService.assignAccessionId(clique);
				clique = archive(clique);
				results.add(clique);
			}
		}
		return results;
	}

	/**
	 * creates sublists of size L. 
	 * from: https://stackoverflow.com/questions/2895342/java-how-can-i-split-an-arraylist-in-multiple-small-arraylists
	 * @param list
	 * @param L
	 * @return
	 */
	static <T> List<List<T>> partition(List<T> list, final int L) {
	    List<List<T>> parts = new ArrayList<List<T>>();
	    final int N = list.size();
	    for (int i = 0; i < N; i += L) {
	        parts.add(new ArrayList<T>(
	            list.subList(i, Math.min(N, i + L)))
	        );
	    }
	    return parts;
	}
	
}
