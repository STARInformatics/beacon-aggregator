package bio.knowledge.model.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Node")
public class TkgNode {
	@Id @GeneratedValue
	private Long dbId;

	@Labels
	private Set<String> labels = new HashSet<String>();
	
	private String category;
	private String name;
	private String id;
	private String description;
	private String symbol;
	private String uri;
	private String non_biolink_category;
	
	public void setNonBiolinkCategory(String category) {
		this.non_biolink_category = category;
	}
	
	public String nonBiolinkCategory() {
		return this.non_biolink_category;
	}
	
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
	
	public String name() {
		return name;
	}
	
	public String id() {
		return id;
	}
	
	public String category() {
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
