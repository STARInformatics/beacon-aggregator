package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.List;

import bio.knowledge.server.model.ConceptsconceptIdDetails;
import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;
import bio.knowledge.server.model.StatementsObject;
import bio.knowledge.server.model.StatementsPredicate;
import bio.knowledge.server.model.StatementsSubject;

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
	public static InlineResponse2001 translate(bio.knowledge.client.model.InlineResponse2001 r) {
		InlineResponse2001 response = new InlineResponse2001();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		List<ConceptsconceptIdDetails> details = new ArrayList<ConceptsconceptIdDetails>();
		for (bio.knowledge.client.model.ConceptsconceptIdDetails d : r.getDetails()) {
			ConceptsconceptIdDetails detail = new ConceptsconceptIdDetails();
			detail.setTag(d.getTag());
			detail.setValue(d.getValue());
			details.add(detail);
		}
		response.setDetails(details);
		
		return response;
	}
	
	public static InlineResponse2002 translate(bio.knowledge.client.model.InlineResponse2002 r) {
		InlineResponse2002 response = new InlineResponse2002();
		response.setDefinition(r.getDefinition());
		response.setId(r.getId());
		response.setName(r.getName());
		response.setSemanticGroup(r.getSemanticGroup());
		response.setSynonyms(r.getSynonyms());
		
		return response;
	}

	public static InlineResponse2004 translate(bio.knowledge.client.model.InlineResponse2004 r) {
		InlineResponse2004 response = new InlineResponse2004();
		response.setDate(r.getDate());
		response.setId(r.getId());
		response.setLabel(r.getLabel());
		
		return response;
	}

	public static InlineResponse2003 translate(bio.knowledge.client.model.InlineResponse2003 r) {
		InlineResponse2003 response = new InlineResponse2003();
		response.setId(r.getId());
		response.setObject(translate(r.getObject()));
		response.setSubject(translate(r.getSubject()));
		response.setPredicate(translate(r.getPredicate()));
		return response;
	}
	
	public static StatementsObject translate(bio.knowledge.client.model.StatementsObject o) {
		StatementsObject object = new StatementsObject();
		object.setId(o.getId());
		object.setName(o.getName());
		return object;
	}
	
	public static StatementsSubject translate(bio.knowledge.client.model.StatementsSubject s) {
		StatementsSubject subject = new StatementsSubject();
		subject.setId(s.getId());
		subject.setName(s.getName());
		return subject;
	}
	
	public static StatementsPredicate translate(bio.knowledge.client.model.StatementsPredicate p) {
		StatementsPredicate predicate = new StatementsPredicate();
		predicate.setId(p.getId());
		predicate.setName(p.getName());
		return predicate;
	}

	public static InlineResponse200 translate(bio.knowledge.client.model.InlineResponse200 r) {
		InlineResponse200 response = new InlineResponse200();
		response.setFrequency(r.getFrequency());
		response.setId(r.getId());
		response.setIdmap(r.getIdmap());
		
		return response;
	}
	
	
}
