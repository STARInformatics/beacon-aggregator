package bio.knowledge.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;

/**
 * A map-like object that holds arbitrary key/value pairs
 *
 */
@NodeEntity(label="ConceptDetail")
public class Neo4jConceptDetail {
	@Id @GeneratedValue
	private Long id;
	
	private String key;
	private String value;
	
	@Relationship(type="SOURCE_BEACON", direction=Relationship.OUTGOING)
	private Neo4jKnowledgeBeacon sourceBeacon;
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Sets the beacon that this piece of data originated from
	 */
	public void setSourceBeacon(Neo4jKnowledgeBeacon beacon) {
		this.sourceBeacon = beacon;
	}
	
	/**
	 * Gets the beacon that this piece of data originated from
	 */
	public Neo4jKnowledgeBeacon getSourceBeacon() {
		return this.sourceBeacon;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Neo4jConceptDetail) {
			Neo4jConceptDetail o = (Neo4jConceptDetail) other;
			return this.sourceBeacon.equals(o.getSourceBeacon()) && this.key.equals(o.key);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(key).append(sourceBeacon.hashCode()).build();
	}

}
