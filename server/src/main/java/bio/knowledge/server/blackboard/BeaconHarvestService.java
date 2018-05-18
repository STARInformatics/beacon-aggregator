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
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.Util;
import bio.knowledge.aggregator.Curie;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.ontology.Ontology;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConceptCategory;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconKnowledgeMapStatement;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.ontology.mapping.NameSpace;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerBeaconConceptCategory;
import bio.knowledge.server.model.ServerBeaconPredicate;
import bio.knowledge.server.model.ServerConceptCategoriesByBeacon;
import bio.knowledge.server.model.ServerConceptCategory;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptWithDetailsBeaconEntry;
import bio.knowledge.server.model.ServerKnowledgeMap;
import bio.knowledge.server.model.ServerKnowledgeMapStatement;
import bio.knowledge.server.model.ServerPredicates;
import bio.knowledge.server.model.ServerPredicatesByBeacon;

@Service
public class BeaconHarvestService implements SystemTimeOut, Util, Curie {

	//private static Logger _logger = LoggerFactory.getLogger(BeaconHarvestService.class);

	@Autowired private KnowledgeBeaconRegistry registry;
	@Autowired private KnowledgeBeaconService kbs;
	@Autowired private Ontology ontology;

	/**
	 * 
	 * @return
	 */
	public KnowledgeBeaconService getKnowledgeBeaconService() {
		return kbs;
	}
	
	@Autowired private MetadataRegistry metadataRegistry;

	@Autowired private ExactMatchesHandler exactMatchesHandler;
	
	/**
	 * 
	 * @return
	 */
	public ExactMatchesHandler getExactMatchesHandler() {
		return exactMatchesHandler;
	}

	private Executor executor;
	
	@PostConstruct
	private void initializeService() {
		// Use a custom Executor thread pool
		executor = 
				Executors.newFixedThreadPool(
						Math.min(countAllBeacons(), 25), 
						new ThreadFactory() {
							public Thread newThread(Runnable r) {
								Thread t = new Thread(r);
								t.setDaemon(true);
								return t;
							}
						}
				);
	}
	
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.SystemTimeOut#countAllBeacons()
	 */
	@Override
	public int countAllBeacons() {
		return registry.countAllBeacons();
	}
	
	/**
	 * 
	 * @return index identifiers of all registered beacons
	 */
	public List<Integer> getAllBeacons() {
		return registry.getBeaconIds();
	}

	protected Integer fixInteger(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	protected String fixString(String str) {
		return str != null ? str : "";
	}

	protected List<String> fixString(List<String> l) {
		if (l == null) return new ArrayList<>();

		for (int i = 0; i < l.size(); i++) {
			l.set(i, fixString(l.get(i)));
		}

		return l;
	}

	/*
	 * @param future
	 * @return
	 */
	private <T> Map<KnowledgeBeacon, List<T>> waitFor(CompletableFuture<Map<KnowledgeBeacon, List<T>>> future) {
		return waitFor(
				future,
				// Scale the timeout proportionately to the number of beacons?
				registry.countAllBeacons()*KnowledgeBeaconService.BEACON_TIMEOUT_DURATION
				) ; 
	}

	/*
	 * Waits {@code TIMEOUT} {@code TIMEUNIT} for the future to complete, throwing a runtime exception otherwise.
	 * @param future
	 * @return
	 */
	private <T> Map<KnowledgeBeacon, List<T>> 
	waitFor(
			CompletableFuture<Map<KnowledgeBeacon, List<T>>> future,
			long timeout
			) {
		try {
			return future.get(timeout, KnowledgeBeaconService.BEACON_TIMEOUT_UNIT);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	/******************************** METADATA Data Access *************************************/

	public void loadConceptTypes() {

		/*
		 * TODO: perhaps read in and store types.csv file here, 
		 * perhaps for full list of valid type names with descriptions?
		 */

		CompletableFuture<
		Map<
			KnowledgeBeacon, 
			List<BeaconConceptCategory>
		>
		> future = kbs.getConceptTypes();

		Map<
			KnowledgeBeacon, 
			List<BeaconConceptCategory>
		> conceptTypes = waitFor(future);

		for (KnowledgeBeacon beacon : conceptTypes.keySet()) {
			for (BeaconConceptCategory conceptType : conceptTypes.get(beacon)) {
				indexConceptType( conceptType, beacon.getId() );
			}
		}
	}

	public void indexConceptType( BeaconConceptCategory bct, Integer beaconId ) {

		/*
		 *	Concept Types are now drawn from the Biolink Model
		 *	(https://github.com/biolink/biolink-model) which
		 *  guarantees globally unique names. Thus, we index 
		 *  Concept Types by exact name string (only).
		 */
		String bcId = bct.getId() ;
		String category = bct.getCategory();
		
		Optional<BiolinkClass> optionalBiolinkClass = ontology.getClassByName( category );
		
		if(!optionalBiolinkClass.isPresent()) {
			optionalBiolinkClass = ontology.lookUpByBeacon( beaconId, bcId );
		}

		/*
		 * Not all beacon concept types will 
		 * already be mapped onto Biolink
		 * so we'll tag such types to "NAME_TYPE"
		 */
		BiolinkClass biolinkClass ;
		if(optionalBiolinkClass.isPresent())
			biolinkClass = optionalBiolinkClass.get();
		else
			biolinkClass = ontology.getDefault();
		
		Optional<BiolinkTerm> optionalTerm = BiolinkTerm.lookUpName(biolinkClass.getName());
		BiolinkTerm term = optionalTerm.get();
		
		String id    = biolinkClass.getCurie();
		String iri   = term.getIri();
		String label = biolinkClass.getName();

		ServerConceptCategory scc;

		Map<String,ServerConceptCategory> conceptTypes = metadataRegistry.getConceptCategoriesMap();

		if(!conceptTypes.containsKey(label)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  concept type, then create it!
			 */
			scc = new ServerConceptCategory();
			scc.setLabel(label);
			conceptTypes.put(label, scc);

		} else {
			scc = conceptTypes.get(label);
		}

		//Set term id, as needed?
		String sccId = scc.getId();
		if(nullOrEmpty(sccId)) {
			scc.setId(id);
		}

		//Set term IRI, as needed?
		String sccIri = scc.getIri();
		if(nullOrEmpty(sccIri)) {
			scc.setIri(iri);
		}

		/*
		 * NOTE: Concept Type description may need to be
		 * loaded from Biolink Model / types.csv file?
		 */
		/*
		 * Search for meta-data for the specific beacons.
		 * 
		 * Note that there may be a one-to-many mapping of beacon concept categories against a Biolink type, 
		 * thus we need to track each beacon type uniquely against its CURIE id.
		 */
		List<ServerConceptCategoriesByBeacon> conceptTypesByBeacons = scc.getBeacons() ;

		Optional<ServerConceptCategoriesByBeacon> sccbbOpt = Optional.empty();
		if(!nullOrEmpty(conceptTypesByBeacons)) {
			sccbbOpt = conceptTypesByBeacons.stream().filter( t -> { return t.getBeacon().equals(beaconId); } ).findAny();
		} 
		
		ServerConceptCategoriesByBeacon sccbb;
		if(sccbbOpt.isPresent()) {
			sccbb = sccbbOpt.get();
		} else {
			sccbb = new ServerConceptCategoriesByBeacon();
			sccbb.setBeacon(beaconId);
			conceptTypesByBeacons.add(sccbb);
		}

		List<ServerBeaconConceptCategory> beaconConceptTypes = sccbb.getCategories(); 
	
		ServerBeaconConceptCategory sbp = new ServerBeaconConceptCategory() ;
		sbp.setId(bct.getId());
		sbp.setIri(NameSpace.makeIri(bct.getId()));
		sbp.setLabel(bct.getCategory());
		sbp.setFrequency(bct.getFrequency());
		
		beaconConceptTypes.add(sbp);
	}

	public void loadPredicates() {

		/*
		 * TODO: perhaps read in and store types.csv file here, 
		 * perhaps for full list of valid type names with descriptions?
		 */

		CompletableFuture<
			Map<
				KnowledgeBeacon, 
				List<BeaconPredicate>
			>
		> future = kbs.getAllPredicates();

		Map<
			KnowledgeBeacon, 
			List<BeaconPredicate>
		> predicates = waitFor(future);

		for (KnowledgeBeacon beacon : predicates.keySet()) {

			for (BeaconPredicate response : predicates.get(beacon)) {
				indexPredicate( response, beacon.getId() );
			}
		}
	}
	
	final class TermEntry<K, V> implements Map.Entry<K, V> {
		
	    private final K key;
	    private V value;

	    public TermEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
	
	private void indexPredicate(BeaconPredicate bpt, Integer beaconId) {

		/*
		 *	Predicate relations are now drawn from the Biolink Model
		 *	(https://github.com/biolink/biolink-model) which
		 *  guarantees globally unique names. Thus, we index 
		 *  Predicate by exact name string (only).
		 */
		String predicate = bpt.getEdgeLabel(); // minimum Biolink Predicate
		String bpId = bpt.getId() ;
		
		Optional<BiolinkClass> optionalBiolinkClass = ontology.getClassByName( predicate );
		
		if(!optionalBiolinkClass.isPresent()) {
			optionalBiolinkClass = ontology.lookUpByBeacon( beaconId, bpId );
		}
		
		/*
		 * Since the Translator community are still
		 * debating the "canonical" version of predicates 
		 * and how to encode them, we will, for now
		 * not reject "missing" predicates but rather
		 * just propagate them directly through.
		 */
		BiolinkTerm term;
		if(optionalBiolinkClass.isPresent()) {
			BiolinkClass biolinkClass = optionalBiolinkClass.get();
			term = BiolinkTerm.lookUpName(biolinkClass);
		} else {
			// Cluster under a generic association for now
			term = BiolinkTerm.ASSOCIATION;
		}
		
		String id    = term.getCurie();
		String iri   = term.getIri();
		String label = term.getLabel();

		ServerPredicates p;

		Map<String,ServerPredicates> predicatesMap = metadataRegistry.getPredicatesMap();

		if(!predicatesMap.containsKey(label)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  predicate, then create it!
			 */
			p = new ServerPredicates();
			p.setLabel(label);
			predicatesMap.put(label, p);

		} else {
			p = predicatesMap.get(label);
		}		

		// Set ServerPredicate primary Id, if needed?
		String spId = p.getId();
		if(nullOrEmpty(spId)) {
			p.setId(id);  
		}

		// Set ServerPredicate primary IRI, if needed?
		String spIri = p.getIri();
		if(nullOrEmpty(spIri)) {
			p.setIri(iri);
		}

		/*
		 * TODO: Predicate description may need to be
		 * loaded from Biolink Model / types.csv file?
		 * For now, use the first non-null beacon definition seen?
		 */
		String description = bpt.getDescription();
		if( nullOrEmpty(p.getDescription()) && ! nullOrEmpty(description))
			p.setDescription(description);

		/*
		 * Search for meta-data for the specific beacons.
		 * 
		 * Note that there may be a one-to-many mapping of beacon predicates against a Biolink type, 
		 * thus we need to track each beacon type uniquely against its CURIE id.
		 */
		List<ServerPredicatesByBeacon> predicatesByBeacons = p.getBeacons() ;

		Optional<ServerPredicatesByBeacon> spbbOpt = Optional.empty();
		if(!nullOrEmpty(predicatesByBeacons)) {
			spbbOpt = predicatesByBeacons.stream().filter( d -> { return d.getBeacon().equals(beaconId); } ).findAny();
		} 
		
		ServerPredicatesByBeacon spbb;
		if(spbbOpt.isPresent()) {
			spbb = spbbOpt.get();
		} else {
			spbb = new ServerPredicatesByBeacon();
			spbb.setBeacon(beaconId);
			predicatesByBeacons.add(spbb);
		}

		List<ServerBeaconPredicate> beaconPredicates = spbb.getPredicates(); 
	
		ServerBeaconPredicate sbp = new ServerBeaconPredicate() ;
		sbp.setId(bpt.getId());
		sbp.setIri(NameSpace.makeIri(bpt.getId()));
		sbp.setLabel(bpt.getEdgeLabel());
		sbp.setFrequency(bpt.getFrequency());

		/*
		 * TODO: BeaconPredicate API needs to be fixed to return the predicate usage frequency?
		 */
		sbp.setFrequency(0);
		
		beaconPredicates.add(sbp);

	}

	/**
	 * 
	 * @param beacons
	 * @param queryId
	 * @return
	 */
	public List<ServerKnowledgeMap> getKnowledgeMap(List<Integer> beacons) {
		
		List<ServerKnowledgeMap> responses = new ArrayList<ServerKnowledgeMap>();
		
		CompletableFuture<
			Map<
				KnowledgeBeacon, 
				List<BeaconKnowledgeMapStatement>
			>
		> future = kbs.getAllKnowledgeMaps( beacons );

		Map<
			KnowledgeBeacon, 
			List<BeaconKnowledgeMapStatement>
		> kmaps = waitFor(
					future,
					weightedTimeout(beacons,1)
				 );  // Scale timeout proportionately to the number of beacons only?
	
		for (KnowledgeBeacon beacon : kmaps.keySet()) {
			
			ServerKnowledgeMap knowledgeMap = new ServerKnowledgeMap();
			
			knowledgeMap.setBeacon(beacon.getId());
			List<ServerKnowledgeMapStatement> statements = knowledgeMap.getStatements();
			
			for (BeaconKnowledgeMapStatement beaconMapStatement : kmaps.get(beacon)) {
				
				ServerKnowledgeMapStatement translation = Translator.translate( beaconMapStatement );
				statements.add(translation);
			}
			
			responses.add(knowledgeMap);
		}
		
		return responses;
	}
	
	/**
	 * This method is a non-blocking call to initiate Concept, Statement, etc. harvesting from beacons
	 * as independent CompletableFuture threads which call back their completion or exceptions
	 * to the *Query wrapped user submitted query object.
	 * 
	 * @param query
	 */
	public void initiateBeaconHarvest(AbstractQuery<?,?,?> query) {
		
		/*
		 * The user stipulated a set of "QueryBeacons" to target for their 
		 * query. However, it is possible that the those beacons were 
		 * already previously harvested for the given query. Therefore, 
		 * the "BeaconsToHarvest" may be a strict subset of the 
		 * "QueryBeacons" depending on what previous queries were recorded
		 *  in the database (and which tag existing data there...)
		 */
		List<Integer> beaconsToHarvest = query.getBeaconsToHarvest();
		
		Map<
			Integer,
			CompletableFuture<Integer>
		> beaconCallMap = query.getBeaconCallMap();
		
		// Initiate non-blocking /concepts calls for each beacon
		for(Integer beacon : beaconsToHarvest) {
			CompletableFuture<Integer> beaconCall =
					CompletableFuture.supplyAsync( query.getQueryResultSupplier(beacon), executor );
			
			beaconCallMap.put(beacon, beaconCall);		
		}
	}
	
	/******************************** CONCEPT DETAILS DATA ACCESS *************************************/

	/**
	 * 
	 * @param cliqueId
	 * @param beacons
	 * @return
	 */
	public ServerConceptWithDetails harvestConceptsBeaconDetails(
			ServerConceptWithDetails conceptDetails,
			List<Integer> beacons
	) {
		String cliqueId = conceptDetails.getClique();
		ConceptClique clique = getExactMatchesHandler().getClique(cliqueId);

		if(clique==null) 
			throw new RuntimeException("harvestConceptsBeaconDetails(): clique with ID '"+cliqueId+"' could not be found?") ;

		List<ServerConceptWithDetailsBeaconEntry> entries = conceptDetails.getEntries();

		CompletableFuture<Map<KnowledgeBeacon, List<BeaconConceptWithDetails>>> future = kbs.getConceptDetails(clique, beacons);

		Map<KnowledgeBeacon, List<BeaconConceptWithDetails>> conceptDetailsByBeacon = waitFor(
				future,
				weightedTimeout(beacons,1)
		);

		for (KnowledgeBeacon beacon : conceptDetailsByBeacon.keySet()) {

			for (BeaconConceptWithDetails beaconConceptWithDetails : conceptDetailsByBeacon.get(beacon)) {
				ServerConceptWithDetailsBeaconEntry entry = Translator.translate(beaconConceptWithDetails);
				entry.setBeacon(beacon.getId());
				entries.add(entry);
				
				String name = conceptDetails.getName();
				String category = conceptDetails.getType();
				
				if (name == null || name.isEmpty()) {
					conceptDetails.setName(beaconConceptWithDetails.getName());
				}
				
				if (category == null || category.isEmpty() || category.equals(ontology.getDefault().getName())) {
					conceptDetails.setType(beaconConceptWithDetails.getCategory());
				}
				
			}
		}

		return conceptDetails;
	}
	
	/******************************** STATEMENT EVIDENCE DATA ACCESS *************************************/

	/**
	 * 
	 * @param statementId
	 * @param keywords
	 * @param pageNumber
	 * @param size
	 * @param beacons
	 * @return
	 * @throws BlackboardException
	 */
	public List<ServerAnnotation> harvestEvidence(
			String statementId,
			List<String> keywords, 
			Integer size,
			List<Integer> beacons
	) throws BlackboardException {

		List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();

		try {

			CompletableFuture<Map<KnowledgeBeacon, List<BeaconAnnotation>>> future = 
					kbs.getEvidence(statementId, keywords, size, beacons);

			Map<
			KnowledgeBeacon, 
			List<BeaconAnnotation>
			> evidence = waitFor(
							future,
							weightedTimeout(beacons, size)
						 );

			for (KnowledgeBeacon beacon : evidence.keySet()) {
				for (BeaconAnnotation reference : evidence.get(beacon)) {
					ServerAnnotation translation = ModelConverter.convert(reference, ServerAnnotation.class);
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}

		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return responses;
	}
}
