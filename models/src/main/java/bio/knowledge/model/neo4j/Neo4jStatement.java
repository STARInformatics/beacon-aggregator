package bio.knowledge.model.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;

@NodeEntity(label="Statement")
public class Neo4jStatement {
	
	@Id @GeneratedValue
	private Long dbId;
	
	private String accessionId;
	private String name;
	
	@Relationship(type="SUBJECT", direction = Relationship.OUTGOING)
    private Neo4jConcept subject;
    
	@Relationship(type="RELATION", direction = Relationship.OUTGOING)
    private Neo4jPredicate relation ;

	@Relationship(type="OBJECT", direction = Relationship.OUTGOING)
    private Neo4jConcept object;
	
	@Relationship(type="EVIDENCE", direction = Relationship.OUTGOING)
	protected Neo4jEvidence evidence ;
    
	@Relationship(type="QUERY", direction = Relationship.INCOMING)
	private Set<QueryTracker> queries = new HashSet<QueryTracker>();
	
	@Relationship(type="BEACON_CITATION", direction = Relationship.OUTGOING)
	private Neo4jBeaconCitation beaconCitation;

	public String getId() {
		return this.accessionId;
	}

	public String getName() {
		return this.name;
	}

	public void setId(String id) {
		this.accessionId = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Long getDbId() {
		return dbId;
	}
	
	public void setSubject(Neo4jConcept subject) {
		this.subject = subject;
	}

	public Neo4jConcept getSubject() {
		return this.subject;
	}

	public void setRelation(Neo4jPredicate relation) {
		this.relation = relation;
	}

	public Neo4jPredicate getRelation() {
		return this.relation;
	}

	public void setObject(Neo4jConcept object) {
		this.object = object;
	}

	public Neo4jConcept getObject() {
		return this.object;
	}

	public void setEvidence(Neo4jEvidence evidence) {
		this.evidence = evidence;
	}

	public Neo4jEvidence getEvidence() {
		return this.evidence;
	}
	
	public Neo4jBeaconCitation getBeaconCitation() {
		return this.beaconCitation;
	}

	public Integer getCitingBeacon() {
		Neo4jKnowledgeBeacon beacon = beaconCitation.getBeacon();
		if(beacon!=null) {
			return beacon.getBeaconId();
		} else {
			return null;
		}
	}

	public String getCitedId() {
		return beaconCitation.getObjectId();
	}

	public void setBeaconCitation(Neo4jBeaconCitation citation) {
		this.beaconCitation = citation;
	}

	public void addQuery(QueryTracker queryTracker) {
		this.queries.add(queryTracker);
	}

}
