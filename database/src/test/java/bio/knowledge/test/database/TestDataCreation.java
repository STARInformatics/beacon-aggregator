package bio.knowledge.test.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import bio.knowledge.database.DatabaseConfiguration;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jGeneralStatement;
import bio.knowledge.model.neo4j.Neo4jRelation;
import bio.knowledge.ontology.BiolinkTerm;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DatabaseConfiguration.class)
@Transactional
public class TestDataCreation {
	
	@Autowired ConceptRepository conceptRepository;
	@Autowired StatementRepository statementRepository;
	
	@Test
	@Transactional
	public void testConceptCreationAndRetrieval() {
		
		assertEquals(conceptRepository.count(), 0);
		
		Neo4jConcept[] originalConcepts = new Neo4jConcept[3];
		
		for (int i = 0; i < originalConcepts.length; i++) {
			originalConcepts[i] = makeConcept(i);
			conceptRepository.save(originalConcepts[i]);
		}
		
		for (Neo4jConcept neo4jConcept : conceptRepository.getConcepts()) {
			boolean isAmongOriginals = false;
			for (Neo4jConcept originalConcept : originalConcepts) {
				if (neo4jConcept.getClique().equals(originalConcept.getClique())) {
					isAmongOriginals = true;
					
					assertSameConcept(neo4jConcept, originalConcept);
				}
			}
			
			assertTrue(isAmongOriginals);
		}
	}
	
	@Test
	public void testStatementCreationAndRetrieval() {
		assertEquals(statementRepository.count(), 0);
		
		Neo4jGeneralStatement[] originalStatements = new Neo4jGeneralStatement[3];
		
		for (int i = 0; i < originalStatements.length; i++) {
			String n = Integer.toString(i);
			
			Neo4jConcept object = makeConcept(i, "object");
			Neo4jRelation predicate = new Neo4jRelation();
			Neo4jConcept subject = makeConcept(i, "subject");
			
			List<String> synonyms = new ArrayList<String>();
			synonyms.add("synonym_" + n + "_one");
			synonyms.add("synonym_" + n + "_two");
			synonyms.add("synonym_" + n + "_three");
			
			
			predicate.setId("predicate:" + n);
			predicate.setName("predicate " + n);
//			predicate.setSynonyms("synonyms for predicate " + n);
			predicate.setDescription("description for predicate " + n);
			
			
			originalStatements[i] = 
					new Neo4jGeneralStatement(
							"statement: " + n,
							subject,
							predicate,
							object
							);
			
//			originalStatements[i].setDescription("description " + n);
//			originalStatements[i].setName("name " + n);
//			originalStatements[i].setQueryFoundWith("query_" + n);
//			originalStatements[i].setId("statement:" + n);
		}
		
		for (Neo4jGeneralStatement Neo4jGeneralStatement : statementRepository.findAll()) {
			boolean isAmongOriginals = false;
			for (Neo4jGeneralStatement originalStatement : originalStatements) {
				if (originalStatement.getId().equals(Neo4jGeneralStatement.getId())) {
					isAmongOriginals = true;
					
					Neo4jConcept subject = (Neo4jConcept) Neo4jGeneralStatement.getSubject();
					Neo4jRelation predicate = (Neo4jRelation) Neo4jGeneralStatement.getRelation();
					Neo4jConcept object = (Neo4jConcept) Neo4jGeneralStatement.getObject();
					
					
					assertSameConcept(subject, (Neo4jConcept) originalStatement.getSubject());
					assertSameConcept(object, (Neo4jConcept) originalStatement.getObject());
					
					Neo4jRelation originalPredicate = (Neo4jRelation) originalStatement.getRelation();
					
					assertEquals(predicate.getName(), originalPredicate.getName());
					assertEquals(predicate.getDescription(), originalPredicate.getDescription());
//					assertEquals(predicate.getSynonyms(), originalPredicate.getSynonyms());
					assertEquals(predicate.getId(), originalPredicate.getId());
					
					assertEquals(Neo4jGeneralStatement.getId(), originalStatement.getId());
//					assertEquals(Neo4jGeneralStatement.getDescription(), originalStatement.getDescription());
//					assertEquals(Neo4jGeneralStatement.getQueryFoundWith(), originalStatement.getQueryFoundWith());
				}
			}
			assertTrue(isAmongOriginals);
		}
	}
	
	private void assertSameConcept(Neo4jConcept a, Neo4jConcept b) {
		assertEquals(a.getName(), b.getName());
		assertEquals(a.getClique(), b.getClique());
		assertEquals(a.getDefinition(), b.getDefinition());
		
		// March 26th, 2018: New format of QueryTracker managment 
		// TODO: Need to fix this particular test
		//assertEquals(a.getQueries(), b.getQueries());
		
		assertTrue(a.getSynonyms().containsAll(b.getSynonyms()));
		assertTrue(a.getTypes().containsAll(b.getTypes()));
	}
	
	private Neo4jConcept makeConcept(int i) {
		return makeConcept(i, "");
	}
	
	private Neo4jConcept makeConcept(int i, String tag) {
		String n = tag + Integer.toString(i);
		
		Neo4jConcept concept = new Neo4jConcept();
		concept.setName("concept " + n);
		concept.setClique("clique:" + n);
		concept.setDefinition("definition " + n);
		
		List<String> synonyms = new ArrayList<String>();
		synonyms.add("synonym_" + n + "_one");
		synonyms.add("synonym_" + n + "_two");
		synonyms.add("synonym_" + n + "_three");
		concept.setSynonyms(synonyms);
		
		ConceptTypeEntry GENE       = new ConceptTypeEntry(BiolinkTerm.GENE);
		ConceptTypeEntry DISEASE    = new ConceptTypeEntry(BiolinkTerm.DISEASE);
		ConceptTypeEntry PHYSIOLOGY = new ConceptTypeEntry(BiolinkTerm.PHYSIOLOGY);
		
		Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();
		types.add(GENE);
		types.add(DISEASE);
		types.add(PHYSIOLOGY);
		concept.setTypes(types);
		
		// March 26th, 2018: New format of QueryTracker managment: 
		// TODO: Need to fix this particular test
		//concept.setQueryFoundWith("query_" + n);
		
		return concept;
	}

}
