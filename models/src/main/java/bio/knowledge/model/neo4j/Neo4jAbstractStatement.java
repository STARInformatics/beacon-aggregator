/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.model.neo4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import bio.knowledge.model.Concept;
import bio.knowledge.model.Evidence;
import bio.knowledge.model.Predicate;
import bio.knowledge.model.Statement;
import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.core.neo4j.Neo4jAbstractIdentifiedEntity;

public abstract class Neo4jAbstractStatement 
	extends Neo4jAbstractIdentifiedEntity 
	implements Statement {
	
	@Relationship( type="SUBJECT" )
    private List<Concept> subjects = new ArrayList<Concept>() ;
    
	@Relationship( type="RELATION" )
    private Predicate relation ;

	@Relationship( type="OBJECT" )
    private List<Concept> objects = new ArrayList<Concept>() ;
	
	/*
	 *  The Transient subject and object attributes here
	 *  are only loaded when the Statement POJO is used 
	 *  as a DTO for transferring data to the "Relations" table.
	 */
	
	@Transient
	private Concept subject ;
	
	@Transient
	private Concept object ;

    @Relationship( type="EVIDENCE" )
	protected Evidence evidence ;
    
	@Relationship(type="QUERY", direction = Relationship.INCOMING)
	private Set<QueryTracker> queries = new HashSet<QueryTracker>();
	
	@Relationship(type="BEACON_CITATION", direction = Relationship.OUTGOING)
	private Neo4jBeaconCitation beaconCitation;
   
	/*
	 * Constructors
	 */
    protected Neo4jAbstractStatement() {}
    
	/**
	 * 
	 * @param name
	 */
	protected Neo4jAbstractStatement(String name) {
    	super(name);
    }
    
	
    /**
     * Constructor creates a new Statement (by Predicate)
     * but defers setting of related concepts.
     * 
     * 
     * @param accessionId
     * @param predicate
     */
    protected Neo4jAbstractStatement(
    		String accessionId,
    		Predicate predicate
    ) {
    	super(accessionId,predicate.getName(),"") ;
    	setRelation(predicate);
    }
	
    /**
     * Constructor creates a new Statement (by Predicate)
     * but defers setting of related concepts.
     * 
     * 
     * @param accessionId
     * @param type
     * @param predicate
     */
    protected Neo4jAbstractStatement(
    		String accessionId,
    		Concept subject,
    		Predicate predicate,
    		Concept object
    ) {
    	super(accessionId,subject.getName()+" - "+predicate.getName()+" -> "+object.getName(),"") ;
    	setSubject(subject);
    	setObject(object);
    	setRelation(predicate);
    }
	
    /**
     * Constructor creates a new Statement (by Predicate.name)
     * but defers setting of related concepts.
     * 
     * @param accessionId
     * @param type
     * @param predicateName
     */
    protected Neo4jAbstractStatement(
    		String accessionId,
    		String predicateName
    ) {
    	super(accessionId,predicateName,"") ;
    }

    /*
     * New Query Tracking in Translator Knowledge Beacon architecture
     */
	
    /**
     * 
     * @param query
     * @return
     */
	public boolean addQuery(QueryTracker query) {
		return this.queries.add(query);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<QueryTracker> getQueries() {
		return Collections.unmodifiableSet(queries);
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#addSubject(bio.knowledge.model.neo4j.Neo4jConcept)
	 */
	@Override
	public void addSubject(Concept subject) {
		if(subjects==null)
			subjects = new ArrayList<Concept>() ;
		subjects.add(subject);
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#setSubjects(java.util.List)
	 */
	@Override
	public void setSubjects(List<Concept> subjects) {
		this.subjects = subjects;
	}

	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getSubjects()
	 */
	@Override
	public List<Concept> getSubjects() {
		return subjects;
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#setSubject(bio.knowledge.model.neo4j.Neo4jConcept)
	 */
	@Override
	public  void setSubject(Concept subject) {
		addSubject(subject);
		this.subject = subject ;
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getSubject()
	 */
	@Override
	public Concept getSubject() {
		return subject ;
	}

	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#setRelation(bio.knowledge.model.neo4j.Neo4jPredicate)
	 */
	public void setRelation(Predicate relation) {
		this.relation = relation;
	}

    /* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getRelation()
	 */
	
	public Predicate getRelation() {
		return relation;
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#addObject(bio.knowledge.model.neo4j.Neo4jConcept)
	 */
	@Override
	public void addObject(Concept object) {
		if(objects==null)
			objects = new ArrayList<Concept>() ;
		objects.add(object);
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#setObjects(java.util.List)
	 */
	@Override
	public void setObjects(List<Concept> objects) {
		this.objects = objects;
	}

	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getObjects()
	 */
	@Override
	public List<Concept> getObjects() {
		return objects;
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#setObject(bio.knowledge.model.neo4j.Neo4jConcept)
	 */
	@Override
	public void setObject(Concept object) {
		addObject(object);
		this.object = object ;
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getObject()
	 */
	@Override
	public Concept getObject() {
		return object ;
	}
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#setEvidence(bio.knowledge.model.neo4j.Neo4jEvidence)
	 */
	@Override
	public void setEvidence( Evidence evidence ) {
		this.evidence = evidence;
	}

	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getEvidence()
	 */
	@Override
	public Evidence getEvidence() {
		return evidence;
	}
	
	/**
	 * 
	 */
	public void setBeaconCitation(Neo4jBeaconCitation beaconCitation) {
		this.beaconCitation = beaconCitation;
	}
	
	/**
	 * 
	 */
	public Neo4jBeaconCitation getBeaconCitation() {
		return beaconCitation;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getCitingBeacon()
	 */
	@Override
	public Integer getCitingBeacon() {
		Neo4jKnowledgeBeacon beacon = beaconCitation.getBeacon();
		if(beacon!=null)
			return beacon.getBeaconId();
		else
			return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.model.Statement#getCitedId()
	 */
	@Override
	public String getCitedId() {
		return beaconCitation.getObjectId();
	}
	
    /*
     * (non-Javadoc)
     * @see bio.knowledge.model.core.neo4j.Neo4jIdentifiedEntity#toString()
     */
	/* (non-Javadoc)
	 * @see bio.knowledge.model.Statement#toString()
	 */
	@Override
    public String toString() {
    	return  "( subject:Concept "+subjects.toString()+
    			")-["+getRelation().getName()+":Predicate"+
    			"]->( object:Concept "+objects.toString()+")" ;
    	
    }
}
