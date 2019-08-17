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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.Util;
import bio.knowledge.aggregator.Curie;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.ontology.Ontology;
import bio.knowledge.client.model.BeaconConceptCategory;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconKnowledgeMapStatement;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatementWithDetails;
import bio.knowledge.database.repository.EvidenceRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.model.neo4j.Neo4jEvidence;
import bio.knowledge.model.neo4j.Neo4jStatement;
import bio.knowledge.ontology.BeaconBiolinkModel;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkSlot;
import bio.knowledge.ontology.mapping.NameSpace;
import bio.knowledge.ontology.utils.Utils;
import bio.knowledge.server.blackboard.BeaconCall.ReportableSupplier;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerBeaconConceptCategory;
import bio.knowledge.server.model.ServerBeaconPredicate;
import bio.knowledge.server.model.ServerConceptCategoriesByBeacon;
import bio.knowledge.server.model.ServerConceptCategory;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptWithDetailsBeaconEntry;
import bio.knowledge.server.model.ServerKnowledgeMap;
import bio.knowledge.server.model.ServerKnowledgeMapStatement;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerPredicatesByBeacon;

@Service
public class BeaconHarvestService implements SystemTimeOut, Util, Curie {

	private static Logger _logger = LoggerFactory.getLogger(BeaconHarvestService.class);

	@Autowired private KnowledgeBeaconRegistry registry;
	@Autowired private KnowledgeBeaconService kbs;
	@Autowired private Ontology ontology;
	@Autowired private EvidenceRepository evidenceRepository;
	@Autowired private StatementRepository statementRepository;
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

	/**
	 * 
	 */
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
	
	private void indexConceptType( BeaconConceptCategory bct, Integer beaconId ) {

		/*
		 *	Concept Types are now drawn from the Biolink Model
		 *	(https://github.com/biolink/biolink-model) which
		 *  guarantees globally unique names. Thus, we index 
		 *  Concept Types by exact name string (only).
		 */
		String bcId = bct.getId() ;
		String bcCategory = bct.getCategory();
		
		BiolinkClass biolinkClass = 
				ontology.lookupCategory( beaconId, bcId, bcCategory );
		
		String id          = biolinkClass.getCurie();
		String uri         = biolinkClass.getUri();
		String category    = biolinkClass.getName();
		String description = biolinkClass.getDescription();

		ServerConceptCategory scc;

		Map<String,ServerConceptCategory> conceptTypes = 
				metadataRegistry.getConceptCategoriesMap();

		if(!conceptTypes.containsKey(category)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  concept type, then create it!
			 */
			scc = new ServerConceptCategory();
			
			scc.setCategory(category);
			scc.setDescription(description);
			
			conceptTypes.put(category, scc);

		} else {
			scc = conceptTypes.get(category);
		}

		//Set term id, as needed?
		String sccId = scc.getId();
		if(nullOrEmpty(sccId)) {
			scc.setId(id);
		}

		//Set term IRI, as needed?
		String sccUri = scc.getUri();
		if(nullOrEmpty(sccUri)) {
			scc.setUri(uri);
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
		sbp.setUri(NameSpace.makeIri(bct.getId()));
		sbp.setCategory(bct.getCategory());
		sbp.setFrequency(bct.getFrequency());

		/* Backup for empty Biolink Model category descriptions */
		description = bct.getDescription();
		if(  nullOrEmpty(scc.getDescription()) && 
		   ! nullOrEmpty(description) ) 
			scc.setDescription(description);
		
		beaconConceptTypes.add(sbp);
	}

	/**
	 * 
	 */
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
		
		// TODO: minimum Biolink Predicate; What about the 'relations' field?
		
		String id  = bpt.getId() ;
		String uri = bpt.getUri();
		String edgeLabel = bpt.getEdgeLabel();
		String relation  = bpt.getRelation();
		String description = bpt.getDescription();

		/*
		 *  Failing that, try to the Biolink Model minimal predicate 
		 *  from the beacon returned relation, if available
		 */
		
		if(nullOrEmpty(edgeLabel)) {
			edgeLabel = relation;
		}
		
		// Last resort
		if(nullOrEmpty(edgeLabel)) {
			edgeLabel = "relation" ;
		}
		
		if(nullOrEmpty(id)) {
			id = BeaconBiolinkModel.BIOLINK_MODEL_NAMESPACE+":"+edgeLabel;
		}

		// need to convert from snake_case
		String bcPredicate = String.join(" ", edgeLabel.split("_"));
		
		// Normalize to Biolink Model minimal predicate from identifier, if available
		BiolinkSlot predicate = ontology.lookupPredicate( beaconId, id, bcPredicate );
		if(predicate != null) {
			id          = predicate.getCurie();
			uri         = predicate.getUri();
			edgeLabel   = Utils.toSnakeCase(predicate.getName());
			description = predicate.getDescription();
		}
		
		ServerPredicate p;

		Map<String,ServerPredicate> edgeLabelMap = metadataRegistry.getPredicatesMap();

		if( ! edgeLabelMap.containsKey(edgeLabel)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  predicate, then create it!
			 */
			p = new ServerPredicate();
			
			p.setId(id);
			p.setUri(uri);
			p.setEdgeLabel(edgeLabel);
			p.setDescription(description);
			
			edgeLabelMap.put(edgeLabel, p);

		} else {
			p = edgeLabelMap.get(edgeLabel);
		}		

		// Set ServerPredicate primary Id, if needed?
		String spId = p.getId();
		if(nullOrEmpty(spId)) {
			p.setId(id);  
		}

		// Set ServerPredicate primary IRI, if needed?
		String spUri = p.getUri();
		if(nullOrEmpty(spUri)) {
			p.setUri(uri);
		}

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
		
		sbp.setId(bpt.getLocalId());
		sbp.setUri(bpt.getLocalUri());
		sbp.setRelation(bpt.getLocalRelation());
		sbp.setFrequency(bpt.getFrequency());

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
					
					/* Since kmap retrieval may be time consuming, 
					 * Scale timeout proportionately to the
					 * number of beacons and a fake page size of 100 
					 */
					weightedTimeout(beacons,100) 
				 );  
	
		for (KnowledgeBeacon beacon : kmaps.keySet()) {
			
			Integer beaconId = beacon.getId();
			
			ServerKnowledgeMap knowledgeMap = new ServerKnowledgeMap();
			
			knowledgeMap.setBeacon(beaconId);
			
			List<ServerKnowledgeMapStatement> statements = knowledgeMap.getStatements();
			
			for (BeaconKnowledgeMapStatement beaconMapStatement : kmaps.get(beacon)) {
				try {
					ServerKnowledgeMapStatement translation = Translator.translate(beaconMapStatement, beaconId, ontology);
					statements.add(translation);
				} catch(Exception e) {
					_logger.error("BeaconHarvestService.getKnowledgeMap(Beacon: "+beaconId+") ERROR: "+e.getMessage());
				}
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
	 * Also creates a QueryTracker
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

		for(Integer beacon : beaconsToHarvest) {
			ReportableSupplier<Integer> supplier = query.getQueryResultSupplier(beacon);
			BeaconCall<Integer> call = new BeaconCall<Integer>(supplier, executor);
			query.putBeaconCall(beacon, call);
		}
	}
	
	/**
	 * Performs a call on all beacons that gives a single CompletableFuture. Also creates a QueryTracker
	 * @param query
	 */
	public void initiateHarvestOnAllQueriedBeacons(AbstractQuery<?,?,?> query) {
		List<Integer> beaconsToHarvest = query.getBeaconsToHarvest();

		for (Integer beaconId : beaconsToHarvest) {
			ReportableSupplier<Integer> supplier = query.getQueryResultSupplier(0);
			
			BeaconCall<Integer> call = new BeaconCall<Integer>(supplier, executor);
			
			query.putBeaconCall(beaconId, call);
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
		Neo4jConceptClique clique = getExactMatchesHandler().getClique(cliqueId);

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
				List<String> categories = conceptDetails.getCategories();
				
				if (name == null || name.isEmpty()) {
					conceptDetails.setName(beaconConceptWithDetails.getName());
				}
				
				if (categories == null || categories.isEmpty() || categories.get(0).equals(ontology.getDefaultCategory().getName())) {
					conceptDetails.setCategories(beaconConceptWithDetails.getCategories());
				}
				
			}
		}

		return conceptDetails;
	}
	
	/******************************** STATEMENT EVIDENCE DATA ACCESS *************************************/

	
//	public ServerStatementDetails harvestEvidence(String statementId, List<String> keywords, Integer pageSize, Integer pageNumber) 
//			throws BlackboardException {
//
//		try {
//
//			CompletableFuture<List<BeaconStatementWithDetails>> future = 
//					kbs.getStatementDetails(statementId, keywords, pageSize);
//
//			List<BeaconStatementWithDetails> evidence = future.get(weightedTimeout(pageSize), BEACON_TIMEOUT_UNIT);
//
//			if (evidence.size() != 1) {
//				throw new BlackboardException("Number of statements related to given evidence is not equal to one; Actual number: " + evidence.size());
//			} else {
//				BeaconStatementWithDetails details = evidence.get(0);
//				ServerStatementDetails result = new ServerStatementDetails();
//				result.setId(details.getId());
//				result.setIsDefinedBy(details.getIsDefinedBy());
//				result.setProvidedBy(details.getProvidedBy());
//				result.setKeywords(keywords);
//				result.setPageNumber(pageNumber);
//				result.setPageSize(pageSize);
//				result.setQualifiers(details.getQualifiers());
//				result.setAnnotation(Translator.translateAnnotation(details.getAnnotation()));
//				result.setEvidence(Translator.translateEvidence(details.getEvidence()));
//				
//				return result;
//			}
//			
//		} catch (Exception e) {
//			throw new BlackboardException(e);
//		}
//	}
	
	/**
	 * Populates statement with information about is_defined_by, provided_by, qualifiers, annotations, and evidence. Saves evidence into repository
	 * If is_defined_by is null, changes is_defined_by to an empty string to indicate this statement has already been harvested before
	 * @param statement
	 * @param statementId
	 * @param keywords
	 * @param pageSize
	 * @return
	 * @throws BlackboardException
	 */
	public Neo4jStatement harvestAndSaveEvidence(Neo4jStatement statement, String statementId, List<String> keywords, Integer pageSize) 
			throws BlackboardException {

		try {

			CompletableFuture<List<BeaconStatementWithDetails>> future = 
					kbs.getStatementDetails(statementId, keywords, pageSize);

			List<BeaconStatementWithDetails> beaconEvidence = future.get(weightedTimeout(pageSize), BEACON_TIMEOUT_UNIT);

			if (beaconEvidence.size() != 1) {
				throw new BlackboardException("Number of statements related to given evidence is not equal to one; Actual number: " + beaconEvidence.size());
			} else {
				BeaconStatementWithDetails details = beaconEvidence.get(0);
				
				String isDefinedBy = details.getIsDefinedBy();
				if (isDefinedBy == null) {
					statement.setIsDefinedBy("");
				} else {
					statement.setIsDefinedBy(isDefinedBy);
				}
				
				statement.setProvidedBy(details.getProvidedBy());
				statement.setQualifiers(details.getQualifiers());
				statement.setAnnotations(Translator.translate(details.getAnnotation()));
				
				statementRepository.setStatementDetails(
						statementId,
						statement.getIsDefinedBy(), 
						statement.getProvidedBy(),
						statement.getQualifiers(),
						statement.getAnnotations());
				
				for (Neo4jEvidence evidence : Translator.translateEvidence(details.getEvidence())) {
					evidence.addStatement(statement);
					evidenceRepository.save(evidence);
				}
				
				return statement;
			}
			
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
	}
	
}
