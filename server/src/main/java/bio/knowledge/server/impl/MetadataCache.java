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

import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.Util;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.blackboard.Blackboard;
import bio.knowledge.client.model.BeaconConceptType;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.server.model.ServerBeaconConceptType;
import bio.knowledge.server.model.ServerBeaconPredicate;
import bio.knowledge.server.model.ServerConceptType;
import bio.knowledge.server.model.ServerPredicate;

/**
 * @author richard
 *
 */
@Service
public class MetadataCache implements Util {

	@Autowired private KnowledgeBeaconRegistry registry;

	@Autowired private Blackboard blackboard;
	
/************************** Beacon Descriptions **************************/
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<KnowledgeBeacon> getKnowledgeBeacons() {
		 return (List<KnowledgeBeacon>)(List)registry.getKnowledgeBeacons();
	}

/************************** Concept Type Cache **************************/
	
	private Map<String,ServerConceptType> conceptTypes = new HashMap<String,ServerConceptType>();
	
	private final String BIOLINK_BASE_URI = "http://bioentity.io/vocab/" ;
	
	private String makeIri(String name) {
		return BIOLINK_BASE_URI + WordUtils.capitalizeFully(name,null).replaceAll(" ", "");
	}
	
	/**
	 * 
	 * @param bct
	 * @param beaconId
	 */
	public void indexConceptType( BeaconConceptType bct, String beaconId ) {
		
		/*
		 *	Concept Types are now drawn from the Biolink Model
		 *	(https://github.com/biolink/biolink-model) which
		 *  guarantees globally unique names. Thus, we index 
		 *  Concept Types by exact name string (only).
		 */
		String name = bct.getId();  // temporary impedence mismatch between Beacon API and KBA API...
		
		/*
		 *  sanity check... ignore "beacon concept type" 
		 *  records without proper names?
		 */
		if( name==null || name.isEmpty() ) return ; 

		ServerConceptType sct;
		
		if(!conceptTypes.containsKey(name)) {
			/*
			 *  If a record by this name 
			 *  doesn't yet exist for this
			 *  concept type, then create it!
			 */
			sct = new ServerConceptType();
			sct.setName(name);
			conceptTypes.put(name, sct);
			
		} else {
			sct = conceptTypes.get(name);
		}
		
		//Set IRI, if needed?
		String iri = sct.getIri();
		if(nullOrEmpty(iri)) sct.setIri(makeIri(name));
		
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
		currentBeacon.setId(bct.getId());
		currentBeacon.setFrequency(bct.getFrequency());

	}
	
	private void loadConceptTypes() {
		
		/*
		 * TODO: perhaps read in and store types.csv file here, 
		 * perhaps for full list of valid type names with descriptions?
		 */
		
		Map<
			KnowledgeBeacon, 
			List<BeaconConceptType>
		> conceptTypes = blackboard.getAllConceptTypes();
		
		for (KnowledgeBeacon beacon : conceptTypes.keySet()) {
			for (BeaconConceptType conceptType : conceptTypes.get(beacon)) {
				indexConceptType( conceptType, beacon.getId() );
			}
		}
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
	
	private Map<String,ServerPredicate> predicates = new HashMap<String,ServerPredicate>();
	
	/**
	 * 
	 * @param bp
	 * @param beaconId
	 */
	public void indexPredicate(BeaconPredicate bp, String beaconId) {
		
		/*
		 *	Predicate relations are now drawn from the Biolink Model
		 *	(https://github.com/biolink/biolink-model) which
		 *  guarantees globally unique names. Thus, we index 
		 *  Concept Types by exact name string (only).
		 */
		String id = bp.getId();
		String name = bp.getName();
		
		/*
		 *  sanity check... ignore "beacon predicate" 
		 *  records without proper names?
		 */
		if( name==null || name.isEmpty() ) return ; 
		
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
		
		//Set IRI, if needed?
		String iri = p.getIri();
		if(nullOrEmpty(iri)) p.setIri(makeIri(name));
		
		/*
		 * TODO: Predicate description may need to be
		 * loaded from Biolink Model / types.csv file?
		 * For now, use the first non-null beacon definition seen?
		 */
		if( p.getDescription().isEmpty() &&
		  !bp.getDefinition().isEmpty()	) 
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
		
		// predicate resource CURIE
		currentBeacon.setId(id);
		
		/*
		 * BeaconPredicate API needs to be fixed 
		 * to return the predicate usage frequency?
		 */
		currentBeacon.setFrequency(0);
		
	}
	
	private void loadPredicates() {
		
		/*
		 * TODO: perhaps read in and store types.csv file here, 
		 * perhaps for full list of valid type names with descriptions?
		 */
		
		Map<
			KnowledgeBeacon, 
			List<BeaconPredicate>
		> predicates = blackboard.getAllPredicates();
		
		for (KnowledgeBeacon beacon : predicates.keySet()) {
			
			for (BeaconPredicate response : predicates.get(beacon)) {
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
