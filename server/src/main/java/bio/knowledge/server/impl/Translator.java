package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.List;

import bio.knowledge.server.model.KnowledgeBeacon;
import bio.knowledge.server.model.Detail;
import bio.knowledge.server.model.Summary;
import bio.knowledge.server.model.ConceptDetail;
import bio.knowledge.server.model.Concept;
import bio.knowledge.server.model.Statement;
import bio.knowledge.server.model.Annotation;
import bio.knowledge.server.model.Object;
import bio.knowledge.server.model.Predicate;
import bio.knowledge.server.model.Subject;

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
	public static ConceptDetail translate(bio.knowledge.client.model.InlineResponse2001 r) {
		ConceptDetail response = new ConceptDetail();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		List<Detail> details = new ArrayList<Detail>();
		for (bio.knowledge.client.model.ConceptsconceptIdDetails d : r.getDetails()) {
			Detail detail = new Detail();
			detail.setTag(d.getTag());
			detail.setValue(d.getValue());
			details.add(detail);
		}
		response.setDetails(details);
		
		return response;
	}
	
	public static Concept translate(bio.knowledge.client.model.InlineResponse2002 r) {
		Concept response = new Concept();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		return response;
	}

	public static Annotation translate(bio.knowledge.client.model.InlineResponse2004 r) {
		Annotation response = new Annotation();
		response.setDate(r.getDate());
		response.setId(r.getId());
		response.setLabel(r.getLabel());
		
		return response;
	}

	public static Statement translate(bio.knowledge.client.model.InlineResponse2003 r) {
		Statement response = new Statement();
		response.setId(r.getId());
		response.setObject(translate(r.getObject()));
		response.setSubject(translate(r.getSubject()));
		response.setPredicate(translate(r.getPredicate()));
		return response;
	}
	
	public static Object translate(bio.knowledge.client.model.StatementsObject o) {
		Object object = new Object();
		object.setId(o.getId());
		object.setName(o.getName());
		return object;
	}
	
	public static Subject translate(bio.knowledge.client.model.StatementsSubject s) {
		Subject subject = new Subject();
		subject.setId(s.getId());
		subject.setName(s.getName());
		return subject;
	}
	
	public static Predicate translate(bio.knowledge.client.model.StatementsPredicate p) {
		Predicate predicate = new Predicate();
		predicate.setId(p.getId());
		predicate.setName(p.getName());
		return predicate;
	}

	public static Summary translate(bio.knowledge.client.model.InlineResponse200 r) {
		Summary response = new Summary();
		response.setFrequency(r.getFrequency());
		response.setId(r.getId());
		response.setIdmap(r.getIdmap());
		
		return response;
	}
	
	public static KnowledgeBeacon translate(bio.knowledge.aggregator.KnowledgeBeacon b) {
		
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
	
	
}
