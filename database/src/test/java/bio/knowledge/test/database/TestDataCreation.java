package bio.knowledge.test.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import bio.knowledge.database.DatabaseConfiguration;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jPredicate;
import bio.knowledge.model.neo4j.Neo4jStatement;
import bio.knowledge.model.neo4j.Neo4jGeneralStatement;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DatabaseConfiguration.class)
@Transactional
@Ignore // Tests may be broken given the latest models... need to revisit!
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
		
		Neo4jStatement[] originalStatements = new Neo4jStatement[3];
		
		for (int i = 0; i < originalStatements.length; i++) {
			String n = Integer.toString(i);
			
			Neo4jConcept object = makeConcept(i, "object");
			Neo4jPredicate predicate = new Neo4jPredicate();
			Neo4jConcept subject = makeConcept(i, "subject");
			
			List<String> synonyms = new ArrayList<String>();
			synonyms.add("synonym_" + n + "_one");
			synonyms.add("synonym_" + n + "_two");
			synonyms.add("synonym_" + n + "_three");
			
			predicate.setEdgeLabel("Edge: " + n);
			predicate.setRelation("Relation:" + n);
			
			originalStatements[i] = new Neo4jStatement();
			originalStatements[i].setName("statement: " + n);
			originalStatements[i].setSubject(subject);
			originalStatements[i].setRelation(predicate);
			originalStatements[i].setObject(object);
			
//			originalStatements[i].setDescription("description " + n);
//			originalStatements[i].setName("name " + n);
//			originalStatements[i].setQueryFoundWith("query_" + n);
//			originalStatements[i].setId("statement:" + n);
		}
		
		for (Neo4jStatement Neo4jGeneralStatement : statementRepository.findAll()) {
			boolean isAmongOriginals = false;
			for (Neo4jStatement originalStatement : originalStatements) {
				if (originalStatement.getId().equals(Neo4jGeneralStatement.getId())) {
					isAmongOriginals = true;
					
					Neo4jConcept subject = (Neo4jConcept) Neo4jGeneralStatement.getSubject();
					Neo4jPredicate predicate = (Neo4jPredicate) Neo4jGeneralStatement.getRelation();
					Neo4jConcept object = (Neo4jConcept) Neo4jGeneralStatement.getObject();
					
					
					assertSameConcept(subject, (Neo4jConcept) originalStatement.getSubject());
					assertSameConcept(object, (Neo4jConcept) originalStatement.getObject());
					
					Neo4jPredicate originalPredicate = (Neo4jPredicate) originalStatement.getRelation();
					
					assertEquals(predicate.getEdgeLabel(), originalPredicate.getEdgeLabel());
					assertEquals(predicate.getRelation(), originalPredicate.getRelation());
					
					assertEquals(Neo4jGeneralStatement.getId(), originalStatement.getId());
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
//		concept.setClique("clique:" + n);
		concept.setDefinition("definition " + n);
		
		List<String> synonyms = new ArrayList<String>();
		synonyms.add("synonym_" + n + "_one");
		synonyms.add("synonym_" + n + "_two");
		synonyms.add("synonym_" + n + "_three");
		concept.setSynonyms(synonyms);
		
//		ConceptTypeEntry GENE       = new ConceptTypeEntry(BiolinkTerm.GENE);
//		ConceptTypeEntry DISEASE    = new ConceptTypeEntry(BiolinkTerm.DISEASE);
//		ConceptTypeEntry PHYSIOLOGY = new ConceptTypeEntry(BiolinkTerm.PHYSIOLOGY);
		
		Set<Neo4jConceptCategory> types = new HashSet<Neo4jConceptCategory>();
//		types.add(GENE);
//		types.add(DISEASE);
//		types.add(PHYSIOLOGY);
		concept.setTypes(types);
		
		// March 26th, 2018: New format of QueryTracker managment: 
		// TODO: Need to fix this particular test
		//concept.setQueryFoundWith("query_" + n);
		
//		return concept;
		throw new RuntimeException("fail");
	}

}
