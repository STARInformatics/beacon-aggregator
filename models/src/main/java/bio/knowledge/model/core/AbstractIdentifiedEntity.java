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
package bio.knowledge.model.core;

import bio.knowledge.model.CURIE;

/**
 * @author Richard
 * 
 * Should be abstract, since this class is NOT generally directly instantiated, 
 * but Neo4j doesn't really like abstract classes for @NodeEntity tagged classes
 */
public class AbstractIdentifiedEntity implements IdentifiedEntity {

	protected Long dbid = 0L;
	
    /**
     */
	private String uri = "";

    /**
     * The CURIE of the object
     */
	private String id = "";

    /**
     */
	private String name = "";

    /**
     */
    private String description = "";
    
    /**
     * synonyms: a tab delimited string in the first iteration (for expediency)
     */
    private String synonyms = "" ;
    
	public AbstractIdentifiedEntity() {
        super();
    }
    
    public AbstractIdentifiedEntity( String name ) {
    	super() ;
        this.name = name;
    }

    public AbstractIdentifiedEntity(String name, String description) {
    	this(name) ;
        this.description = description ;
    }
    
    public AbstractIdentifiedEntity( String id, String name, String description ) {
    	this(name,description) ;
        this.id = id ;
        this.uri = CURIE.resolveUri(id);
    }

    /* (non-Javadoc)
	 * @see bio.knowledge.model.core.IdentifiedEntity#setId(java.lang.String)
	 */
    @Override
	public void setUri(String uri) {
        this.uri = uri;
    }

	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.Identification#getId()
	 */
	@Override
	public String getUri() { 
		return this.uri;
	}

    /* (non-Javadoc)
	 * @see bio.knowledge.model.core.IdentifiedEntity#setId(java.lang.String)
	 */
    @Override
	public void setId(String id) {
        this.id = id;
    }

	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.Identification#getId()
	 */
	@Override
	public String getId() { 
		return id;
	}
	
    /* (non-Javadoc)
	 * @see bio.knowledge.model.core.IdentifiedEntity#setName(java.lang.String)
	 */
    @Override
	public void setName(String name) {
        this.name = name;
    }

	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.Identification#getName()
	 */
	@Override
	public String getName() { 
		return name;
	}
	
    /* (non-Javadoc)
	 * @see bio.knowledge.model.core.IdentifiedEntity#setDescription(java.lang.String)
	 */
    @Override
	public void setDescription(String description) {
        this.description = description;
    }

	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.general.Identification#getDescription()
	 */
	@Override
	public String getDescription() {
        return this.description;
    }
	
	
	
	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.IdentifiedEntity#toString()
	 */
	@Override
	public String toString() { return this.getName() ; }

    /* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * IdentifiedEntity are ordered to one another by id (only)
	 * if available; otherwise, order by name.
	 * 
	 * Obviously, neither the id nor the name of 
	 * the current IdentifiedEntity instance 
	 * nor that of the "other" IdentifiedEntity, 
	 * should be null as this would generate a null pointer exception!
	 */
	@Override
	public int compareTo(IdentifiedEntity other) {
		if(!( 
				id==null || 
				id.isEmpty() ||
				other.getId()==null ||
				other.getId().isEmpty())
		)
			return id.compareTo(other.getId());
		else
			return name.compareTo(other.getName());
	}

	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.Identification#getSynonyms()
	 */
	@Override
	public String getSynonyms() {
		return synonyms;
	}

	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.IdentifiedEntity#setSynonyms(java.lang.String)
	 */
	@Override
	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
	}

	private Integer version;
	@Override
	public Integer getVersion() {
		return version;
	}

	@Override
	public void setVersion(Integer version) {
		this.version = version;
	}

	private long versionDate = 0;
	
	@Override
	public long getVersionDate() {
		return versionDate;
	}

	@Override
	public void setVersionDate(long versionDate) {
		this.versionDate = versionDate;
	}

	@Override
	public Long getDbId() {
		return dbid;
	}

	@Override
	public void setDbId(Long id) {
		this.dbid = id;
	}
}
