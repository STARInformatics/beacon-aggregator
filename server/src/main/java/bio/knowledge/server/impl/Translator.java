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
import java.util.List;

import bio.knowledge.server.model.Annotation;
import bio.knowledge.server.model.Concept;
import bio.knowledge.server.model.ConceptDetail;
import bio.knowledge.server.model.Detail;
import bio.knowledge.server.model.KnowledgeBeacon;
import bio.knowledge.server.model.LogEntry;
import bio.knowledge.server.model.ServerObject;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.Statement;
import bio.knowledge.server.model.ServerSubject;
import bio.knowledge.server.model.Summary;

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
	
	public static ConceptDetail translate(bio.knowledge.client.model.ConceptDetail r) {
		ConceptDetail response = new ConceptDetail();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		List<Detail> details = new ArrayList<Detail>();
		for (bio.knowledge.client.model.Detail d : r.getDetails()) {
			Detail detail = new Detail();
			detail.setTag(d.getTag());
			detail.setValue(d.getValue());
			details.add(detail);
		}
		response.setDetails(details);
		
		return response;
	}
	
	public static Concept translate(bio.knowledge.client.model.Concept r) {
		Concept response = new Concept();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		return response;
	}

	public static Annotation translate(bio.knowledge.client.model.Annotation r) {
		Annotation response = new Annotation();
		response.setDate(r.getDate());
		response.setId(r.getId());
		response.setLabel(r.getLabel());
		
		return response;
	}

	public static Statement translate(bio.knowledge.client.model.Statement r) {
		Statement response = new Statement();
		response.setId(r.getId());
		
		response.setObject(translate(r.getObject()));
		response.setSubject(translate(r.getSubject()));
		response.setPredicate(translate(r.getPredicate()));
		return response;
	}
	
	public static bio.knowledge.server.model.ServerObject translate(bio.knowledge.client.model.StatementsObject o) {
		ServerObject object = new ServerObject();
		object.setId(o.getId());
		object.setName(o.getName());
		return object;
	}
	
	public static ServerSubject translate(bio.knowledge.client.model.StatementsSubject s) {
		ServerSubject subject = new ServerSubject();
		subject.setId(s.getId());
		subject.setName(s.getName());
		return subject;
	}
	
	public static ServerPredicate translate(bio.knowledge.client.model.StatementsPredicate p) {
		ServerPredicate predicate = new ServerPredicate();
		predicate.setId(p.getId());
		predicate.setName(p.getName());
		return predicate;
	}

	public static Summary translate(bio.knowledge.client.model.Summary r) {
		Summary response = new Summary();
		response.setFrequency(r.getFrequency());
		response.setId(r.getId());
		response.setIdmap(r.getIdmap());
		
		return response;
	}
	
	public static KnowledgeBeacon translate(bio.knowledge.aggregator.KnowledgeBeaconImpl b) {
		
		KnowledgeBeacon beacon = new KnowledgeBeacon();
		beacon.setId(b.getId());
		beacon.setName(b.getName());
		beacon.setUrl(b.getUrl());
		beacon.setDescription(b.getDescription());
		beacon.setContact(b.getContact());
		beacon.setWraps(b.getWraps());
		beacon.setRepo(b.getRepo());
		
		return beacon;
	}
	
	public static LogEntry translate(bio.knowledge.aggregator.LogEntry e) {
	
		LogEntry error = new LogEntry();
		error.setTimestamp(e.getTimestamp());
		error.setQuery(e.getQuery());
		error.setMessage(e.getMessage());
		
		return error;
	}
	
}

