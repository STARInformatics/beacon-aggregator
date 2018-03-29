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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.Util;
import bio.knowledge.aggregator.BeaconConceptWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.Curie;
import bio.knowledge.aggregator.Harvester.BeaconInterface;
import bio.knowledge.aggregator.Harvester.RelevanceTester;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.Timer;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptType;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconKnowledgeMapStatement;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.model.BioNameSpace;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.umls.Category;
import bio.knowledge.ontology.BiolinkModel;
import bio.knowledge.ontology.mapping.NameSpace;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerBeaconConceptType;
import bio.knowledge.server.model.ServerBeaconPredicate;
import bio.knowledge.server.model.ServerConceptType;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptWithDetailsBeaconEntry;
import bio.knowledge.server.model.ServerKnowledgeMap;
import bio.knowledge.server.model.ServerKnowledgeMapStatement;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementSubject;

@Service
public class BeaconHarvestService implements SystemTimeOut, Util, Curie {

	private static Logger _logger = LoggerFactory.getLogger(BeaconHarvestService.class);

	@Autowired private KnowledgeBeaconRegistry registry;
	@Autowired private KnowledgeBeaconService kbs;
	
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

	@Autowired private ConceptTypeService conceptTypeService;
	//@Autowired private TaskExecutor executor;
	Executor executor;
	
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

	private final String KEYWORD_DELIMINATOR = " ";

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
			List<BeaconConceptType>
		>
		> future = kbs.getConceptTypes();

		Map<
			KnowledgeBeacon, 
			List<BeaconConceptType>
		> conceptTypes = waitFor(future);

		for (KnowledgeBeacon beacon : conceptTypes.keySet()) {
			for (BeaconConceptType conceptType : conceptTypes.get(beacon)) {
				indexConceptType( conceptType, beacon.getId() );
			}
		}
	}

	public void indexConceptType( BeaconConceptType bct, Integer beaconId ) {

		/*
		 *	Concept Types are now drawn from the Biolink Model
		 *	(https://github.com/biolink/biolink-model) which
		 *  guarantees globally unique names. Thus, we index 
		 *  Concept Types by exact name string (only).
		 */
		String bcId = bct.getId() ;
		String name = BiolinkModel.lookUp( beaconId, bcId); 

		/*
		 *  sanity check... ignore "beacon concept type" 
		 *  records without proper names?
		 */
		if( name==null || name.isEmpty() ) return ; 

		ServerConceptType sct;

		Map<String,ServerConceptType> conceptTypes = metadataRegistry.getConceptTypes();

		if(!conceptTypes.containsKey(name)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  concept type, then create it!
			 */
			sct = new ServerConceptType();
			sct.setLabel(name);
			conceptTypes.put(name, sct);

		} else {
			sct = conceptTypes.get(name);
		}

		//Set Curie, if needed?
		String curie = sct.getId();
		if(nullOrEmpty(curie)) {
			???String bct_iri = bct.getIri(); xxxx this is wrong. Need to lookup BIolink version now?
			if(!nullOrEmpty(bct_iri)) {
				sct.setId(???);
			} else {
				sct.setIri(NameSpace.makeCurie(name));
			}
		}

		//Set IRI, if needed?
		String iri = sct.getIri();
		if(nullOrEmpty(iri)) {
			String bct_iri = bct.getIri(); xxxx this is wrong. Need to lookup BIolink version now?
			if(!nullOrEmpty(bct_iri)) {
				sct.setIri(bct_iri);
			} else {
				sct.setIri(NameSpace.makeIri(name));
			}
		}

		/*
		 * NOTE: Concept Type description may need to be
		 * loaded from Biolink Model / types.csv file?
		 */

		// Search for meta-data for the specific beacons
		List<ServerBeaconConceptType> beacons = sct.getBeacons() ;
		ServerBeaconConceptType currentBeacon = null;

		// Search for existing beacon entry?
		for( ServerBeaconConceptType b : beacons ) {
			if(b.getBeacon().equals(beaconId)) {
				currentBeacon = b;
				break;
			}
		}

		/*
		 * It will be quite common during system initialisation 
		 * that the current beacon will not yet have been loaded...
		 */
		if( currentBeacon == null ) {
			/*
			 *  If it doesn't already exist, then 
			 *  create a new Beacon meta-data entry
			 */
			currentBeacon = new ServerBeaconConceptType();
			currentBeacon.setBeacon(beaconId);

			beacons.add(currentBeacon);
		}

		// Set other beacon-specific concept type metadata
		// False assumption that each beacon only has one mapping to a given class?
		currentBeacon.setId(bcId);
		currentBeacon.setIri(bct.getIri());
		currentBeacon.setLabel(bct.getLabel());
		currentBeacon.setFrequency(bct.getFrequency());

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

	private void indexPredicate(BeaconPredicate bp, Integer beaconId) {

		/*
		 *	Predicate relations are now drawn from the Biolink Model
		 *	(https://github.com/biolink/biolink-model) which
		 *  guarantees globally unique names. Thus, we index 
		 *  Predicate by exact name string (only).
		 */
		String bpId = bp.getId();  
		String id = BiolinkModel.lookUp( beaconId, bpId ); 
		
		String name = bp.getName();

		/*
		 *  sanity check... ignore "beacon predicate" 
		 *  records without proper names?
		 */
		if( name==null || name.isEmpty() ) return ; 

		name = name.toLowerCase();

		ServerPredicate p;

		Map<String,ServerPredicate> predicates = metadataRegistry.getPredicates();

		if(!predicates.containsKey(name)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  predicate, then create it!
			 */
			p = new ServerPredicate();
			p.setLabel(name);
			predicates.put(name, p);

		} else {
			p = predicates.get(name);
		}		

		// Set ServerPredicate primary CURIE, if needed?
		String spid = p.getId();
		if(nullOrEmpty(spid)) {
			p.setId(id);  
		}

		// Set ServerPredicate primary IRI, if needed?
		String iri = p.getIri();
		if(nullOrEmpty(iri)) {
			p.setIri(NameSpace.makeIri(id));
		}

		/*
		 * TODO: Predicate description may need to be
		 * loaded from Biolink Model / types.csv file?
		 * For now, use the first non-null beacon definition seen?
		 */
		if( nullOrEmpty(p.getDescription()) && ! nullOrEmpty(bp.getDefinition()))
			p.setDescription(bp.getDefinition());

		// Search for meta-data for the specific beacons
		List<ServerBeaconPredicate> beacons = p.getBeacons() ;
		ServerBeaconPredicate currentBeacon = null;
		// Search for existing beacon entry?
		for( ServerBeaconPredicate b : beacons ) {
			if(b.getBeacon().equals(beaconId)) {
				currentBeacon = b;
				break;
			}
		}

		if( currentBeacon == null ) {
			/*
			 *  If it doesn't already exist, then 
			 *  create a new Beacon meta-data entry
			 */
			currentBeacon = new ServerBeaconPredicate();
			currentBeacon.setBeacon(beaconId);
			beacons.add(currentBeacon);
		}

		// Store or overwrite current beacon meta-data

		// Beacon-specific predicate resource identification
		currentBeacon.setId(bp.getId());
		currentBeacon.setIri(NameSpace.makeIri(id));
		currentBeacon.setLabel(bp.getName());

		/*
		 * BeaconPredicate API needs to be fixed 
		 * to return the predicate usage frequency?
		 */
		currentBeacon.setFrequency(0);

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
	
	/******************************** CONCEPT Data Access *************************************/

	/**
	 * 
	 * @param cliqueId
	 * @param beacons
	 * @return
	 */
	public ServerConceptWithDetails harvestConceptsWithDetails(
			String cliqueId, 
			List<Integer> beacons
	) {

		ServerConceptWithDetails conceptDetails = null;

		ConceptClique clique = getExactMatchesHandler().getClique(cliqueId);

		if(clique==null) 
			throw new RuntimeException("getConceptDetails(): '"+cliqueId+"' could not be found?") ;

		conceptDetails = new ServerConceptWithDetails();

		conceptDetails.setClique(cliqueId);

		/* 
		 * Defer name setting below; 
		 * clique name seems to be the 
		 * same as the cliqueId right now... 
		 * not sure if that is correct?
		 * 
		 * conceptDetails.setName(ecc.getName()); 
		 */
		conceptDetails.setType(clique.getConceptType());
		conceptDetails.setAliases(clique.getConceptIds());

		List<ServerConceptWithDetailsBeaconEntry> entries = conceptDetails.getEntries();

		CompletableFuture<
			Map<
				KnowledgeBeacon, 
				List<BeaconConceptWithDetails>
			>
		> future = kbs.getConceptDetails(clique, beacons);

		Map<
			KnowledgeBeacon, 
			List<BeaconConceptWithDetails>
		> conceptDetailsByBeacon = waitFor(
										future,
										weightedTimeout(beacons,1)
								   );  // Scale timeout proportionately to the number of beacons only?

		for (KnowledgeBeacon beacon : conceptDetailsByBeacon.keySet()) {

			for (BeaconConceptWithDetails response : conceptDetailsByBeacon.get(beacon)) {

				/*
				 * Simple heuristic to set the name to something sensible.
				 * Since beacon-to-beacon names may diverge, may not always
				 * give the "best" name (if such a thing exists...)
				 */
				if( conceptDetails.getName() == null )
					conceptDetails.setName(response.getName());

				ServerConceptWithDetailsBeaconEntry entry = Translator.translate(response);
				entry.setBeacon(beacon.getId());
				entries.add(entry);
			}
		}

		return conceptDetails;
	}

	/******************************** STATEMENTS Data Access *************************************/

	/*
	 * @param conceptId
	 * @param conceptName
	 * @param identifiers
	 * @return
	 */
	private Boolean matchToList(String conceptId, String conceptName, List<String> identifiers ) {

		String idPattern = "(?i:"+conceptId+")";

		/*
		 *  Special test for the presence of 
		 *  Human Gene Nomenclature Consortium (and geneCards) symbols.
		 *  Case insensitive match to non-human species symbols
		 *  which may have difference letter case?
		 */
		String hgncSymbolPattern = "HGNC.SYMBOL:(?i:"+conceptName.toUpperCase()+")";
		String genecardsPattern = "GENECARDS:(?i:"+conceptName.toUpperCase()+")";
		String umlsPattern = "UMLS:(?i:"+conceptName.toUpperCase()+")";

		for(String id : identifiers) {

			if(id.matches(idPattern)) 
				return true;

			if(id.matches(hgncSymbolPattern)) 
				return true;

			if(id.matches(genecardsPattern)) 
				return true;

			if(id.matches(umlsPattern)) 
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param queryId 
	 * @param queryId
	 * @return
	 */
	@Deprecated
	public List<ServerStatement> harvestStatements(
			String source, String relations, String target, 
			String keywords, String conceptTypes, 
			Integer pageNumber, Integer pageSize,
			List<Integer> beacons, String queryId
	) {
		List<ServerStatement> statements = new ArrayList<ServerStatement>();
		
		ConceptClique sourceClique = getExactMatchesHandler().getClique(source);
		if(sourceClique==null) {
			_logger.error("getStatements(): source clique '"+source+"' could not be found?") ;
			return statements; // empty result
		}

		ConceptClique targetClique = null;
		if(!target.isEmpty()) {
			targetClique = getExactMatchesHandler().getClique(target);
			if(targetClique==null) {
				_logger.error("getStatements(): target clique '"+target+"' could not be found?") ;
				return statements; // empty result
			}
		}

		CompletableFuture<Map<KnowledgeBeacon, List<BeaconStatement>>> future = 
				kbs.getStatements( sourceClique, relations, targetClique, keywords, conceptTypes, pageNumber, pageSize, beacons, queryId );

		Map<
			KnowledgeBeacon, 
			List<BeaconStatement>
		> beaconStatements = waitFor(
								future,
								weightedTimeout(beacons, pageSize)
							);
		
		for (KnowledgeBeacon beacon : beaconStatements.keySet()) {

			Integer beaconId = beacon.getId();

			_logger.debug("ctrl.getStatements(): processing beacon '"+beaconId+"'...");

			for ( BeaconStatement response : beaconStatements.get(beacon)) {

				/*
				 * Sanity check: to get around the fact that some beacons 
				 * (like Biolink) will sometimes send back statements
				 *  with a null *%$@?!?!!! subject or object 
				 */
				if( response.getSubject()==null || response.getObject() == null ) continue;

				ServerStatement translation = Translator.translate(response);
				translation.setBeacon(beaconId);

				// Heuristic: need to somehow tag the equivalent concept here?
				ServerStatementSubject subject  = translation.getSubject();
				String subjectId = subject.getId();
				String subjectName = subject.getName();

				/*
				 * The existing beacons may not send the semantic group 
				 * back as a CURIE, thus coerce it accordingly
				 */
				String subjectTypeId = subject.getType();

				Set<ConceptTypeEntry> subjectTypes = 
						conceptTypeService.lookUp(beaconId,subjectTypeId);

				subject.setType(curieSet(subjectTypes));

				ConceptClique subjectEcc = 
						getExactMatchesHandler().getExactMatches(
								beacon,
								subjectId,
								subjectName,
								subjectTypes
								);

				ServerStatementObject object = translation.getObject();
				String objectId = object.getId();
				String objectName = object.getName();

				/*
				 * The existing beacons may not send the semantic group 
				 * back as a CURIE, thus coerce it accordingly
				 */
				String objectTypeId = object.getType();

				Set<ConceptTypeEntry> objectTypes = 
						conceptTypeService.lookUpByIdentifier(objectTypeId);

				object.setType(curieSet(objectTypes));

				ConceptClique objectEcc = 
						getExactMatchesHandler().getExactMatches(
								beacon,
								objectId,
								objectName,
								objectTypes
								);

				/*
				 * Need to refresh the ecc clique in case either 
				 * subject or object id was discovered to belong 
				 * to it during the exact matches operations above?
				 */
				sourceClique = getExactMatchesHandler().getClique(source);

				List<String> conceptIds = sourceClique.getConceptIds(beaconId);

				_logger.debug("ctrl.getStatements(): processing statement '"+translation.getId()
							+ " from beacon '"+beaconId + "' "
							+ "with subject id '"+subjectId + "' "
							+ "and object id '"+objectId+"'"
							+ " matched against conceptIds: '"+String.join(",",conceptIds)+"'"
						);

				if( matchToList( subjectId, subjectName, conceptIds ) ) {

					subject.setClique(sourceClique.getId());
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement subject semantic groups?
					 */
					String ssg = subject.getType();
					if( ( ssg==null || ssg.isEmpty() || ssg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && sourceClique != null )
						subject.setType(sourceClique.getConceptType());

					object.setClique(objectEcc.getId());
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement object semantic groups?
					 */
					String osg = object.getType();
					if( ( osg==null || osg.isEmpty() || osg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && objectEcc != null )
						object.setType(objectEcc.getConceptType());

				} else if( matchToList( objectId, objectName, conceptIds ) ) {

					object.setClique(sourceClique.getId()) ;
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement object semantic groups?
					 */
					String objectConceptType = object.getType();
					if( ( objectConceptType==null ||
							objectConceptType.isEmpty() || 
							objectConceptType.equals(Category.DEFAULT_SEMANTIC_GROUP)) && sourceClique != null
							)
						object.setType(sourceClique.getConceptType());

					subject.setClique(subjectEcc.getId());
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement subject semantic groups?
					 */
					String subjectConceptType = subject.getType();
					if( ( subjectConceptType==null || 
							subjectConceptType.isEmpty() || 
							subjectConceptType.equals(Category.DEFAULT_SEMANTIC_GROUP)) && subjectEcc != null 
							)
						object.setType(subjectEcc.getConceptType());	

				} else {

					_logger.warn("ctrl.getStatements() WARNING: "
							+ "clique is unknown (null) "
							+ "for statement '"+translation.getId()
							+ "from beacon '"+beaconId
							+"' with subject '"+subject.getName()
							+"' ["+subjectId+"]"
							+ " and object '"+object.getName()
							+"' ["+objectId+"]"
							+ " matched against conceptIds: '"+
							String.join(",",conceptIds)+"'"
							);
					continue;
				}

				/*
				 *  Heuristic workaround for beacons which have not yet properly 
				 *  implemented the tagging of semantic groups of concepts,
				 *  to try to set their semantic group type
				 */
				if( subject.getClique() != null && 
						subject.getType() == null) {

					subject.setType(
							BioNameSpace.defaultConceptType( subject.getClique() ).getCurie()
							);
				}

				if( object.getClique() != null && 
						object.getType() == null) {

					object.setType(
							BioNameSpace.defaultConceptType( object.getClique() ).getCurie()
							);
				}

				statements.add(translation);
			}
		}

		if( ! relations.isEmpty() ) {
			final String relationFilter = relations;
			statements = statements.stream()
					.filter(
							s -> s.getPredicate().getId().equals(relationFilter) ? true : false 
							).collect(Collectors.toList());
		}
		
		return statements;
	}

	public List<ServerAnnotation> harvestEvidence(
			String statementId, String keywords, 
			Integer pageNumber, Integer pageSize, 
			List<Integer> beacons
	) throws BlackboardException {

		List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();

		try {

			CompletableFuture<Map<KnowledgeBeacon, List<BeaconAnnotation>>> future = 
					kbs.getEvidence(statementId, keywords, pageNumber, pageSize, beacons);

			Map<
			KnowledgeBeacon, 
			List<BeaconAnnotation>
			> evidence = waitFor(
							future,
							weightedTimeout(beacons, pageSize)
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

	private RelevanceTester<BeaconConcept> buildRelevanceTester( ConceptsQuery query ) {
		return new RelevanceTester<BeaconConcept>() {

			@Override
			public boolean isItemRelevant(BeaconItemWrapper<BeaconConcept> beaconItemWrapper) {
				BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
				BeaconConcept concept = conceptWrapper.getItem();

				String[] keywordsArray = query.getKeywords().split(KEYWORD_DELIMINATOR);
				
				List<String> conceptTypes = query.getConceptTypes();
				final String thisConceptType = concept.getType().toLowerCase();
				if (!nullOrEmpty(conceptTypes)) {
					if( !conceptTypes.stream().anyMatch(s-> s.toLowerCase().equals(thisConceptType)) )
						return false;
				}

				for (String keyword : keywordsArray) {
					if (concept.getName().toLowerCase().contains(keyword.toLowerCase())) {
						return true;
					}
				}

				return false;
			}

		};
	}

	private BeaconInterface<BeaconConcept>  buildBeaconInterface( ConceptsQuery query ) {
		// ConceptsQuery wraps all the parameters of this call: queryId, keywords, etc.
		// 
		return new BeaconInterface<BeaconConcept>() {

			@Override
			public Map<KnowledgeBeacon, List<BeaconItemWrapper<BeaconConcept>>> getDataFromBeacons(Integer pageNumber,
					Integer pageSize) throws InterruptedException, ExecutionException, TimeoutException {
				Timer.setTime("Search concept: " + query.getKeywords());
				CompletableFuture<Map<KnowledgeBeacon, List<BeaconItemWrapper<BeaconConcept>>>>
				future = 
					kbs.getConcepts(
						query.getKeywords(),
						query.getConceptTypes(), 
						query.getPageNumber(), 
						query.getPageSize(), 
						query.getQueryBeacons(), 
						query.getQueryId()
					);
				return future.get(
						KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,
						KnowledgeBeaconService.BEACON_TIMEOUT_UNIT
						);
			}
		};
	}

}
