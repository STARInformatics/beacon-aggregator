package bio.knowledge.model.neo4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class Node {
	@Id @GeneratedValue
	private Long dbId;

	@Labels
	private Set<String> labels = new HashSet<String>();
	
	@Property(name="category")
	private String category;
	
	private String name;
	private String id;
	private String description;
	private String symbol;
	private String uri;
	
	@org.neo4j.ogm.annotation.Relationship
	Set<Edge> edges = new HashSet<Edge>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setCategory(String category) {
		this.category = category;
		this.labels.clear();
		this.labels.add(category);
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Set<Edge> edges() {
		return Collections.unmodifiableSet(edges);
	}
	
	public boolean addEdge(Node object, String edgeLabel) {
		Edge edge = new Edge(this, object, edgeLabel);
		return this.edges.add(edge);
	}
	
	public String name() {
		return name;
	}
	
	public String id() {
		return id;
	}
	
	public String category() {
		System.out.println(this.labels);
		return category;
	}
	
	public String description() {
		return description;
	}
	
	public String symbol() {
		return this.symbol;
	}
	
	public String uri() {
		return this.uri;
	}
}
