package bio.knowledge.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import bio.knowledge.model.core.neo4j.Neo4jAbstractAnnotatedEntity;

public class ConceptClique extends Neo4jAbstractAnnotatedEntity {
	
	public static boolean notDisjoint(ConceptClique clique1, ConceptClique clique2) {
		return ! Collections.disjoint(clique1.getConceptIds(), clique2.getConceptIds());
	}
	
	public static Set<String> unionOfConceptIds(Collection<ConceptClique> cliques) {
		return cliques.stream().map(
				clique -> { return clique.getConceptIds(); }
		).flatMap(List::stream).collect(Collectors.toSet());
	}
	
	private Set<String> conceptIds = new HashSet<String>();
	
	public boolean isEmpty() {
		return conceptIds.isEmpty();
	}
	
	public int size() {
		return conceptIds.size();
	}
	
	public boolean addAll(Collection collection) {
		return conceptIds.addAll(collection);
	}
	
	public boolean addAll(ConceptClique clique) {
		return conceptIds.addAll(clique.conceptIds);
	}
	
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
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
