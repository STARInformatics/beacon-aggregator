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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.Util;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.LogEntry;
import bio.knowledge.server.model.ServerConceptType;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerKnowledgeMap;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerPredicate;

/**
 * This class manages a cache of Knowledge Beacon network metadata 
 * such as concept data types, predicate relations and the
 * "Knowledge Map" subject type-predicate-object type combinations thereof.
 * 
 * This class also wraps access to global beacon metadata and 
 * (session indexed) API call system logs.
 * 
 * @author richard
 *
 */
@Service
public class MetadataService implements Util {

	@Autowired private KnowledgeBeaconRegistry registry;
	@Autowired private KnowledgeBeaconService kbs;
	@Autowired private BeaconHarvestService beaconHarvestService;
	@Autowired private MetadataRegistry metadataRegistry;

/************************** Beacon Descriptions **************************/

	public List<ServerKnowledgeBeacon> getKnowledgeBeacons()  throws BlackboardException {
		
		List<ServerKnowledgeBeacon> responses = new ArrayList<ServerKnowledgeBeacon>();
		
		try {
			
			List<KnowledgeBeacon> beacons = 
					registry.getKnowledgeBeacons();
			
			for (KnowledgeBeacon beacon : beacons) {
				responses.add(Translator.translate(beacon));
			}
			
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return responses;
	}
	
/************************** Error tracking **************************/
	
	/**
	 * 
	 * @param sessionId
	 * @param beacon
	 * @param query
	 * @param message
	 */
	public void logError(String sessionId, Integer beacon, String query, String message) {
		kbs.logError(sessionId, beacon, query, message);
	}

	/**
	 * 
	 * @param queryId
	 * @return
	 */
	public List<ServerLogEntry> getErrors(String queryId) throws BlackboardException {
		
		List<ServerLogEntry> responses = new ArrayList<>();
		
		try {
			List<LogEntry> entries = kbs.getErrors(queryId);
			
			for (LogEntry entry : entries) {
				if (entry != null) {
					responses.add(ModelConverter.convert(entry, ServerLogEntry.class));
				}
			}
		} catch (Exception e) {
			throw new BlackboardException(e);
		}

		return responses;
	}
	
/************************** Concept Type Cache **************************/
	



	/**
	 * TODO: We don't currently filter out nor log the Concept Types retrieval (beacons and sessionId parameters ignored)
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return Server Concept Type records
	 */
	public Collection<? extends ServerConceptType> 
			getConceptTypes( List<Integer> beacons )  throws BlackboardException {

		Collection<? extends ServerConceptType> types = null;
		
		try {
			
			Map<String,ServerConceptType> conceptTypes = metadataRegistry.getConceptTypes();
					
			// Sanity check: is the Server Concept Type cache loaded?
			if(conceptTypes.isEmpty()) 
				beaconHarvestService.loadConceptTypes();
	
			types = conceptTypes.values();
			
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
		
		return types;
	}

/************************** Predicate Registry **************************/
		
	/**
	 * TODO: We don't currently filter out nor log the Predicates retrieval (beacons and sessionId parameters ignored)
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return Server Predicate entries
	 */
	public Collection<? extends ServerPredicate> getPredicates(List<Integer> beacons) throws BlackboardException {
		
		Collection<? extends ServerPredicate> response = null;
		
		try {
			
			Map<String,ServerPredicate> predicates = metadataRegistry.getPredicates() ;
			
			// Sanity check: is the Server Predicate cache loaded?
			if(predicates.isEmpty()) 
				beaconHarvestService.loadPredicates();
	
			response =  predicates.values();
			
		} catch(Exception e) {
			throw new BlackboardException(e);
		}
	
		return response;
	}

/************************** Knowledge Map **************************/
			
	/**
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public List<ServerKnowledgeMap> getKnowledgeMap(List<Integer> beacons) throws BlackboardException { 
		
		List<ServerKnowledgeMap> kmaps = null;
		
		try {
			
			kmaps = beaconHarvestService.getKnowledgeMap(beacons);
			
			/*
			 * TODO: need to cache the knowledge map here?
			 */

		} catch(Exception e) {
			throw new BlackboardException(e);
		}
		
		return kmaps;
	}
}
