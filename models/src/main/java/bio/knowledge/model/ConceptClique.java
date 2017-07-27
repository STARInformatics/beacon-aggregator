package bio.knowledge.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bio.knowledge.model.core.neo4j.Neo4jAbstractAnnotatedEntity;

public class ConceptClique extends Neo4jAbstractAnnotatedEntity {
	private Set<String> conceptIds = new HashSet<>();
	
	public ConceptClique() {
		
	}
	
	public ConceptClique(Collection<String> conceptIds) {
		this.conceptIds.addAll(conceptIds);
	}
	
	public ConceptClique(String[] conceptIds) {
		this(Arrays.asList(conceptIds));
	}
	
	public List<String> getConceptIds() {
		return new ArrayList<String>(conceptIds);
	}
}
