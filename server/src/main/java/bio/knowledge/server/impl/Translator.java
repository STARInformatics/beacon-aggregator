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

import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptDetail;
import bio.knowledge.server.model.ServerDetail;
import bio.knowledge.server.model.ServerEvidence;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementPredicate;
import bio.knowledge.server.model.ServerStatementSubject;
import bio.knowledge.server.model.ServerSummary;

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
	
	public static ServerConceptDetail translate(bio.knowledge.client.model.BeaconConceptWithDetails r) {
		ServerConceptDetail response = new ServerConceptDetail();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		List<ServerDetail> details = new ArrayList<ServerDetail>();
		for (bio.knowledge.client.model.BeaconConceptDetail d : r.getDetails()) {
			ServerDetail detail = new ServerDetail();
			detail.setTag(d.getTag());
			detail.setValue(d.getValue());
			details.add(detail);
		}
		response.setDetails(details);
		
		return response;
	}
	
	public static ServerConcept translate(bio.knowledge.client.model.BeaconConcept r) {
		ServerConcept response = new ServerConcept();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		return response;
	}

	public static ServerEvidence translate(bio.knowledge.client.model.BeaconEvidence r) {
		ServerEvidence response = new ServerEvidence();
		response.setDate(r.getDate());
		response.setId(r.getId());
		response.setLabel(r.getLabel());
		
		return response;
	}

	public static ServerStatement translate(bio.knowledge.client.model.BeaconStatement r) {
		ServerStatement response = new ServerStatement();
		response.setId(r.getId());
		
		response.setObject(translate(r.getObject()));
		response.setSubject(translate(r.getSubject()));
		response.setPredicate(translate(r.getPredicate()));
		return response;
	}
	
	public static bio.knowledge.server.model.ServerStatementObject translate(bio.knowledge.client.model.BeaconStatementsObject o) {
		ServerStatementObject object = new ServerStatementObject();
		object.setId(o.getId());
		object.setName(o.getName());
		return object;
	}
	
	public static ServerStatementSubject translate(bio.knowledge.client.model.BeaconStatementsSubject s) {
		ServerStatementSubject subject = new ServerStatementSubject();
		subject.setId(s.getId());
		subject.setName(s.getName());
		return subject;
	}
	
	public static ServerStatementPredicate translate(bio.knowledge.client.model.BeaconStatementsPredicate p) {
		ServerStatementPredicate predicate = new ServerStatementPredicate();
		predicate.setId(p.getId());
		predicate.setName(p.getName());
		return predicate;
	}

	public static ServerSummary translate(bio.knowledge.client.model.BeaconSummary r) {
		ServerSummary response = new ServerSummary();
		response.setFrequency(r.getFrequency());
		response.setId(r.getId());
		response.setIdmap(r.getIdmap());
		
		return response;
	}
	
	public static ServerKnowledgeBeacon translate(bio.knowledge.aggregator.KnowledgeBeaconImpl b) {
		
		ServerKnowledgeBeacon beacon = new ServerKnowledgeBeacon();
		beacon.setId(b.getId());
		beacon.setName(b.getName());
		beacon.setUrl(b.getUrl());
		beacon.setDescription(b.getDescription());
		beacon.setContact(b.getContact());
		beacon.setWraps(b.getWraps());
		beacon.setRepo(b.getRepo());
		
		return beacon;
	}
	
	public static ServerLogEntry translate(bio.knowledge.aggregator.LogEntry e) {
	
		ServerLogEntry error = new ServerLogEntry();
		error.setTimestamp(e.getTimestamp());
		error.setQuery(e.getQuery());
		error.setMessage(e.getMessage());
		
		return error;
	}
	
}

