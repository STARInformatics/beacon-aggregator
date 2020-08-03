package bio.knowledge.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="ConceptQuery")
public class ConceptQuery {
	private final String queryId;
	
	private List<String> keywords = new ArrayList<>();
	private List<String> categories = new ArrayList<>();
	private List<Integer> beacons = new ArrayList<>();
	
	public ConceptQuery(String queryId) {
		this.queryId = queryId;
	}
	
	public List<String> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	public List<String> getCategories() {
		return categories;
	}
	
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public List<Integer> getBeacons() {
		return beacons;
	}
	
	public void setBeacons(List<Integer> beacons) {
		this.beacons = beacons;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Stream.of(keywords, categories, beacons).flatMap(Collection::stream).toArray());
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ConceptQuery) {
			ConceptQuery that = (ConceptQuery) other;
			return this.keywords.equals(that.keywords)
					&& this.categories.equals(that.categories)
					&& this.beacons.equals(that.beacons);
		} else {
			return false;
		}
	}

	public String getQueryId() {
		return queryId;
	}
}
