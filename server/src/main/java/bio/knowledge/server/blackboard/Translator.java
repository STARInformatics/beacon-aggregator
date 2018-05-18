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
package bio.knowledge.server.blackboard;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.LogEntry;
import bio.knowledge.aggregator.ontology.Ontology;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptDetail;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconKnowledgeMapObject;
import bio.knowledge.client.model.BeaconKnowledgeMapPredicate;
import bio.knowledge.client.model.BeaconKnowledgeMapStatement;
import bio.knowledge.client.model.BeaconKnowledgeMapSubject;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconStatementObject;
import bio.knowledge.client.model.BeaconStatementPredicate;
import bio.knowledge.client.model.BeaconStatementSubject;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptDetail;
import bio.knowledge.server.model.ServerConceptWithDetailsBeaconEntry;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerKnowledgeMapObject;
import bio.knowledge.server.model.ServerKnowledgeMapPredicate;
import bio.knowledge.server.model.ServerKnowledgeMapStatement;
import bio.knowledge.server.model.ServerKnowledgeMapSubject;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementPredicate;
import bio.knowledge.server.model.ServerStatementSubject;

/**
 * This class is a factory for building the server model classes from client
 * model classes. As of now it seems that Swagger does not allow you to generate
 * a server and client project that share a model. But, as far as I can tell,
 * the names of the classes coincide. This may not always be the case, though.
 * 
 * @author Lance Hannestad
 *
 */
public class Translator {

	private static Logger _logger = LoggerFactory.getLogger(Translator.class);
	
	public static ServerConcept translate(BeaconConcept r) {
		ServerConcept response = new ServerConcept();
		response.setName(r.getName());
		response.setType(r.getCategory());
		return response;
	}
	
	public static ServerKnowledgeBeacon translate(KnowledgeBeacon b) {
		
		ServerKnowledgeBeacon beacon = new ServerKnowledgeBeacon();
		beacon.setBeacon(b.getId());
		beacon.setName(b.getName());
		beacon.setUrl(b.getUrl());
		beacon.setDescription(b.getDescription());
		beacon.setContact(b.getContact());
		beacon.setWraps(b.getWraps());
		beacon.setRepo(b.getRepo());
		
		return beacon;
	}
	
	public static ServerLogEntry translate(LogEntry e) {
	
		ServerLogEntry error = new ServerLogEntry();
		error.setTimestamp(e.getTimestamp());
		error.setQuery(e.getQuery());
		error.setMessage(e.getMessage());
		
		return error;
	}

	/**
	 * 
	 * @param beaconStatement
	 * @param ontology
	 * @return
	 */
	public static ServerKnowledgeMapStatement translate( BeaconKnowledgeMapStatement beaconStatement, Integer beaconId, Ontology ontology ) {

		ServerKnowledgeMapStatement statement = new ServerKnowledgeMapStatement();

		/*
		 * Statement Subject
		 */
		BeaconKnowledgeMapSubject beaconSubject = beaconStatement.getSubject();

		ServerKnowledgeMapSubject subject = new ServerKnowledgeMapSubject();

		String subjectCategory = beaconSubject.getCategory();
		BiolinkClass biolinkClass = ontology.lookupCategory( beaconId, subjectCategory, subjectCategory );
		subject.setCategory(biolinkClass.getName());

		List<String> prefixes = subject.getPrefixes() ;
		prefixes.addAll(beaconSubject.getPrefixes());

		statement.setSubject(subject);	

		/*
		 * Statement Predicate Relation
		 */
		BeaconKnowledgeMapPredicate beaconPredicate = beaconStatement.getPredicate();
		
		ServerKnowledgeMapPredicate predicate = new ServerKnowledgeMapPredicate();

		// TODO: maybe the Knowledge Beacon API needs to return this?
		predicate.setEdgeLabel(beaconPredicate.getRelation());

		predicate.setRelation(beaconPredicate.getRelation());

		statement.setPredicate(predicate);

		/*
		 * Statement Object
		 */
		BeaconKnowledgeMapObject beaconObject = beaconStatement.getObject();
		
		ServerKnowledgeMapObject object = new ServerKnowledgeMapObject();

		String objectCategory = beaconObject.getCategory();
		biolinkClass = ontology.lookupCategory( beaconId, objectCategory, objectCategory );
		object.setCategory(biolinkClass.getName());

		prefixes = object.getPrefixes() ;
		prefixes.addAll(beaconObject.getPrefixes());

		statement.setObject(object);

		statement.setFrequency(beaconStatement.getFrequency());

		statement.setDescription(beaconStatement.getDescription());

		return statement;
	}

	public static ServerConceptWithDetailsBeaconEntry translate(BeaconConceptWithDetails r) {
		
		ServerConceptWithDetailsBeaconEntry entry = new ServerConceptWithDetailsBeaconEntry();
		
		entry.setId(r.getId());
		entry.setSynonyms(r.getSynonyms());
		entry.setDefinition(r.getDefinition());
		
		List<ServerConceptDetail> translatedDetails =  entry.getDetails();
		
		for(BeaconConceptDetail detail : r.getDetails()) {
			
			ServerConceptDetail serverConceptDetail = new ServerConceptDetail();
			
			String tag   = detail.getTag();
			String value = detail.getValue();
			serverConceptDetail.setTag(tag);
			serverConceptDetail.setValue(value);
			translatedDetails.add(serverConceptDetail);
			
			_logger.debug("getConceptDetails() details - Tag: '"+tag+
					      "' = Value: '"+value.toString()+"'");
		}
		
		entry.setDetails(translatedDetails);
		
		return entry;
	}

	public static ServerAnnotation translate(BeaconAnnotation r) {
		ServerAnnotation response = new ServerAnnotation();
		response.setDate(r.getDate());
		response.setId(r.getId());
		response.setLabel(r.getLabel());
		
		return response;
	}

	public static ServerStatement translate(BeaconStatement r) {
		
		ServerStatement response = new ServerStatement();
		response.setId(r.getId());
		response.setObject(translate(r.getObject()));
		response.setSubject(translate(r.getSubject()));
		response.setPredicate(translate(r.getPredicate()));
		return response;
	}
	
	public static ServerStatementSubject translate(BeaconStatementSubject s) {
		ServerStatementSubject subject = new ServerStatementSubject();
		subject.setId(s.getId());
		subject.setName(s.getName());
		subject.setType(s.getCategory());
		return subject;
	}
	
	public static bio.knowledge.server.model.ServerStatementObject translate(BeaconStatementObject o) {
		ServerStatementObject object = new ServerStatementObject();
		object.setId(o.getId());
		object.setName(o.getName());
		object.setType(o.getCategory());
		return object;
	}
	
	public static ServerStatementPredicate translate(BeaconStatementPredicate p) {
		ServerStatementPredicate predicate = new ServerStatementPredicate();
		predicate.setId(p.getRelation());
		predicate.setName(p.getEdgeLabel());
		return predicate;
	}
}

