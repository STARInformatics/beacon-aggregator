package bio.knowledge.model.neo4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.Annotation;
import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;

@NodeEntity(label="Statement")
public class Neo4jStatement {
	
	@Id @GeneratedValue
	private Long dbId;
	
	private String accessionId;
	private String name;
	private String isDefinedBy;
	private String providedBy;
	private List<String> qualifiers;
	private List<Annotation> annotations;
	
	@Relationship(type="SUBJECT", direction = Relationship.OUTGOING)
    private Neo4jConcept subject;
    
	@Relationship(type="RELATION", direction = Relationship.OUTGOING)
    private Neo4jPredicate relation ;

	@Relationship(type="OBJECT", direction = Relationship.OUTGOING)
    private Neo4jConcept object;
	
	@Relationship(type="EVIDENCE", direction = Relationship.OUTGOING)
	protected Set<Neo4jEvidence> evidence = new HashSet<Neo4jEvidence>();
    
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

	public void addEvidence(Neo4jEvidence evidence) {
		this.evidence.add(evidence);
	}

	public Set<Neo4jEvidence> getEvidence() {
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

	public String getIsDefinedBy() {
		return isDefinedBy;
	}

	public void setIsDefinedBy(String isDefinedBy) {
		this.isDefinedBy = isDefinedBy;
	}

	public String getProvidedBy() {
		return providedBy;
	}

	public void setProvidedBy(String providedBy) {
		this.providedBy = providedBy;
	}

	public List<String> getQualifiers() {
		return qualifiers;
	}

	public void setQualifiers(List<String> qualifiers) {
		this.qualifiers = qualifiers;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

}
