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
package bio.knowledge.server.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.blackboard.Blackboard;
import bio.knowledge.client.model.BeaconConceptType;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.server.model.ServerConceptType;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerPredicateBeacon;

/**
 * @author richard
 *
 */
@Service
public class MetadataCache  {

	@Autowired private KnowledgeBeaconRegistry registry;

	@Autowired private Blackboard blackboard;
	
/************************** Beacon Descriptions **************************/
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<KnowledgeBeacon> getKnowledgeBeacons() {
		 return (List<KnowledgeBeacon>)(List)registry.getKnowledgeBeacons();
	}

/************************** Concept Type Cache **************************/
	
	private Map<String,ServerConceptType> conceptTypes = new HashMap<String,ServerConceptType>();
	
	/**
	 * 
	 * 
	 * @param translation
	 * @param beaconId
	 */
	public void indexConceptType( BeaconConceptType bct, String beaconId ) {
		
		/*
		 *  Index predicates by exact name string (only).
		 *  ALthough it is conceivable that distinct
		 *  beacons will have identically named relations
		 *  that mean something different, we take this 
		 *  as a community curation challenge we can't
		 *  (and won't try to) solve here.
		 */
		String name = bct.getId();
		
		/*
		 *  sanity check... ignore "beacon concept type" 
		 *  records without proper names?
		 */
		if(name==null ||name.isEmpty()) return ; 

		ServerConceptType sct;
		
		if(!conceptTypes.containsKey(name)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  predicate, then create it!
			 */
			sct = new ServerConceptType();
			sct.setId(name);
			conceptTypes.put(name, sct);
			
		} else {
			sct = conceptTypes.get(name);
		}
		
		// Search for meta-data for the specific beacons
		List<ServerPredicateBeacon> beacons = sct.getBeacons() ;
		ServerPredicateBeacon currentBeacon = null;
		// Search for existing beacon entry?
		for( ServerPredicateBeacon b : beacons ) {
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
			currentBeacon = new ServerPredicateBeacon();
			currentBeacon.setBeacon(beaconId);
			beacons.add(currentBeacon);
		}
	}
	
	private void loadConceptTypes() {
		
		Map<
			KnowledgeBeacon, 
			List<BeaconConceptType>
		> predicates = blackboard.getConceptTypes();
		
		for (KnowledgeBeacon beacon : predicates.keySet()) {
			
			for (BeaconConceptType response : predicates.get(beacon)) {
				/*
				 *  No "conversion" here, but response 
				 *  handled by the indexPredicate function
				 */
				indexConceptType( response, beacon.getId() );
			}
		}
		
		/*			Map<
				KnowledgeBeacon, 
				List<BeaconConceptType>
			
			> types = blackboard.getConceptTypes( beacons, sessionId );
			
			for (KnowledgeBeacon beacon : types.keySet()) {
				
				for (BeaconConceptType conceptType : types.get(beacon)) {
					
					ServerConceptType translation = ModelConverter.convert(conceptType, ServerConceptType.class);
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
		 * */
		
	}

	/**
	 * TODO: We don't currently filter out nor log the Concept Types retrieval (beacons and sessionId parameters ignored)
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return Server Concept Type records
	 */
	public Collection<? extends ServerConceptType> getConceptTypes(List<String> beacons, String sessionId) {
		
		// Sanity check: is the Server Concept Type cache loaded?
		
		if(conceptTypes.isEmpty()) loadConceptTypes();

		return conceptTypes.values();
	}

/************************** Predicate Registry **************************/
	
	private Map<String,ServerPredicate> predicates = 
				new HashMap<String,ServerPredicate>();
	
	/**
	 * 
	 * 
	 * @param translation
	 * @param beaconId
	 */
	public void indexPredicate(BeaconPredicate bp, String beaconId) {
		
		/*
		 *  Index predicates by exact name string (only).
		 *  ALthough it is conceivable that distinct
		 *  beacons will have identically named relations
		 *  that mean something different, we take this 
		 *  as a community curation challenge we can't
		 *  (and won't try to) solve here.
		 */
		String id = bp.getId();
		String name = bp.getName();
		
		/*
		 *  sanity check... ignore "beacon predicate" 
		 *  records without proper names?
		 */
		if(name==null ||name.isEmpty()) return ; 
		
		name = name.toLowerCase();

		ServerPredicate p;
		
		if(!predicates.containsKey(name)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  predicate, then create it!
			 */
			p = new ServerPredicate();
			p.setName(name);
			predicates.put(name, p);
			
		} else {
			p = predicates.get(name);
		}
		
		// Search for meta-data for the specific beacons
		List<ServerPredicateBeacon> beacons = p.getBeacons() ;
		ServerPredicateBeacon currentBeacon = null;
		// Search for existing beacon entry?
		for( ServerPredicateBeacon b : beacons ) {
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
			currentBeacon = new ServerPredicateBeacon();
			currentBeacon.setBeacon(beaconId);
			beacons.add(currentBeacon);
		}
			
		
		// Store or overwrite current beacon meta-data
		
		// predicate resource CURIE
		currentBeacon.setId(id);
		currentBeacon.setDefinition(bp.getDefinition()); 
	}
	
	private void loadPredicates() {
		
		Map<
			KnowledgeBeacon, 
			List<BeaconPredicate>
		> predicates = blackboard.getAllPredicates();
		
		for (KnowledgeBeacon beacon : predicates.keySet()) {
			
			for (BeaconPredicate response : predicates.get(beacon)) {
				/*
				 *  No "conversion" here, but response 
				 *  handled by the indexPredicate function
				 */
				indexPredicate( response, beacon.getId() );
			}
		}
	}

	/**
	 * TODO: We don't currently filter out nor log the Predicates retrieval (beacons and sessionId parameters ignored)
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return Server Predicate entries
	 */
	public Collection<? extends ServerPredicate> getPredicates(List<String> beacons, String sessionId) {
		
		// Sanity check: is the Server Predicate cache loaded?
		
		if(predicates.isEmpty()) loadPredicates();

		return predicates.values();
	}


}
