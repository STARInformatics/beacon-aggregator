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
package bio.knowledge.model.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.NodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bio.knowledge.model.DomainModelException;
import bio.knowledge.model.core.neo4j.Neo4jAbstractIdentifiedEntity;

/**
 * This version of ConceptClique stores beacon subcliques 
 * as a pair of List objects: a list of (case sensitive)
 * exact match concept ids and a corresponding list of
 * strings encoding a comma separated list of beaconIds, 
 * where the identical index position in each list, links
 * the list of beaconids with their corresponding matched
 * concept id (which they recognize exactly)
 * 
 * @author Richard
 *
 */
@NodeEntity(label="ConceptClique")
public class ConceptClique extends Neo4jAbstractIdentifiedEntity {
	
	private static Logger _logger = LoggerFactory.getLogger(ConceptClique.class);
	
	// delimiter of conceptIds in beacon subcliques
	private static final String QDELIMITER = ";";
	
	/*
	 * Biolink Model defined Concept Type
	 */
	private String conceptType;

	/**
	 * 
	 */
	public ConceptClique() { }
	
	/**
	 * 
	 * @param conceptType
	 */
	public ConceptClique(String conceptType) {
		this.conceptType = conceptType;
	}
	
	/**
	 * Primary Concept Type of clique concept 
	 * tagged using label name of type 
	 * from the consensus Biolink Model.
	 * 
	 * @param conceptType
	 */
	public void setConceptType(String conceptType) {
		this.conceptType = conceptType;
	}
	
	/**
	 * 
	 * @return Biolink Concept Type (label) best characterizing this clique
	 */
	public String getConceptType() {
		return conceptType;
	}
	
	/*
	 * Master list of all identifiers recorded in this clique.
	 * Original concept identifier letter case varians is 
	 * preserved to ease their exact match use to recover 
	 * associated concepts in the beacons which use those variants. 
	 * Thus, duplication of identifiers in the master list
	 * (when viewed from a case insensitive vantage point) 
	 * is not avoided.
	 */
	private List<String> conceptIds  = new ArrayList<String>();
	
	/*
	 * A list of strings encoding a comma-separated list of
	 * integer indices into the master list of concept identifiers, 
	 * corresponding to each beacon which knows about that
	 * subclique of equivalent identifiers from the master list.
	 * 
	 * The index position of each entry in the list is
	 * guaranteed to correspond to the the beacon aggregator
	 * assigned beacon id for a given beacon.
	 */
	private List<String> beaconSubcliques = new ArrayList<String>();
	
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.model.core.neo4j.Neo4jAbstractIdentifiedEntity#getName()
	 
	 */
	@Override
	public String getName() {
		String name = super.getName();
		if(name==null || name.isEmpty())
			return getId();
		return super.getName();
	}
	
	public String getSuperName() {
		return super.getName();
	}
	
	/**
	 * Returns true if there are no conceptIds in this clique
	 */
	public boolean isEmpty() {
		return this.conceptIds.isEmpty();
	}
	
	/**
	 * Returns the number of conceptIds in this clique
	 */
	public int size() {
		return this.conceptIds.size();
	}
	
	/**
	 * 
	 * @param beaconId of the beacon that asserts the equivalence of these concept identifiers
	 * @param conceptIds concept identifiers to be added
	 * @throws DomainModelException if the beaconId is null or empty
	 */
	public void addConceptIds( Integer beaconId, List<String> subclique ) {
		
		if( beaconId==null ) 
			throw new DomainModelException("ConceptClique() ERROR: null or empty beacon id?");
		
		for(String id : subclique ) {
			int cid = 0;
			if(!conceptIds.contains(id)) {
				conceptIds.add(id);
			}
			cid = conceptIds.indexOf(id);
			
			addToSubClique(beaconId,cid);
		}
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param conceptId
	 */
	public void addConceptId( Integer beaconId, String conceptId ) {
		
		if( beaconId==null ) 
			throw new DomainModelException("ConceptClique() ERROR: null or empty beacon id?");
		
		if(!conceptIds.contains(conceptId)) {
			conceptIds.add(conceptId);
		}
		
		int cid = conceptIds.indexOf(conceptId);
		addToSubClique(beaconId,cid);
	}
	
	/*
	 * Add a concept integer index to a beacon subclique
	 */
	private void addToSubClique(Integer beaconId, Integer cid) {
		List<Integer> subclique = getBeaconSubClique(beaconId) ;
		if(! subclique.contains(cid) ) subclique.add(cid);
		setBeaconSubClique(beaconId,subclique);
	}
	
	/*
	 * Private accessor to internal beacon subclique data structure, which expands as needed by beacons provided
	 */
	private List<String> _beaconSubcliques(Integer bid) {
		
		/*
		 *  Expand the beaconSubclique master list 
		 *  if necessary (probably initially then rarely)
		 */
		if( bid >= beaconSubcliques.size()) {
			
			_logger.warn("setBeaconSubClique(): expanding the subcliques array for beaconId '"+bid+"'");
			
			/*
			 * Allocate a larger beaconSubcliques list 
			 * size then copy over the old subcliques
			 */
			List<String> bigger = new ArrayList<String>(bid+1);
			for( Integer i=0 ; i <= bid ; i++ ) {
				// Add empty strings for every beacon...
				bigger.add("");
				if(i<beaconSubcliques.size())
					/*
					 * ... but overwrite the entry with 
					 * the current values of the old subclique 
					 * (may still be empty?)
					 */
					bigger.set(i, beaconSubcliques.get(i));
			}
			beaconSubcliques = bigger;
		}
		
		return beaconSubcliques;
	}

	/*
	 * Access and convert an internal String representation of a beacon subclique, 
	 * into a List of integer indices into the master list of concept identifiers. 
	 * I don't do much explicit type checking here since I (hope to) completely 
	 * control the data integrity of the internal data structure.
	 */ 
	private void setBeaconSubClique(Integer beaconId, List<Integer> subclique) {
		
		/*
		 *  This had better be a non-null valid beaconId
		 *  otherwise a numeric exception will be thrown!
		 */
		Integer bid = new Integer(beaconId);

		// Rebuild the beacon subclique entry
		String entry = "";
		for(Integer cid : subclique)
			if(entry.isEmpty())
				entry = cid.toString();
			else
				entry += QDELIMITER+cid.toString();
		
		// Reset the beacon subclique to the new entry
		_beaconSubcliques(bid).set(bid,entry);
	}

	/* 
	 * @param beaconId
	 * @return current list of subclique concept ids encoded as a List of integer indices into the master concept id list
	 */
	private List<Integer> getBeaconSubClique(Integer beaconId) {
		
		/*
		 *  This had better be a non-null valid beaconId
		 *  otherwise a numeric exception will be thrown!
		 */
		
		String entry = _beaconSubcliques(beaconId).get(beaconId);
		
		List<Integer> subclique = new ArrayList<Integer>();
		
		if(!entry.isEmpty()) {
			String[] cids = entry.split(QDELIMITER);
			for(String cid : cids) 
				subclique.add(new Integer(cid));
		}
		
		return subclique;
	}
	
	/**
	 * @param beaconId
	 * @return the list of identifiers of concepts deemed equivalent by a specified beacon.
	 */
	public Boolean hasConceptIds(Integer beaconId) {
		/*
		 *  This had better be a non-null valid beaconId
		 *  otherwise a numeric exception will be thrown!
		 */		
		String entry = _beaconSubcliques(beaconId).get(beaconId);
		
		return !entry.isEmpty() ;
	}
	
	/**
	 * @param beaconId
	 * @return the list of identifiers of concepts deemed equivalent by a specified beacon.
	 */
	public List<String> getConceptIds(Integer beaconId) {
		List<Integer> subclique = getBeaconSubClique(beaconId);
		return getConceptIds(subclique);
	}
	
	/*
	 * We construct the conceptId list for a given identifier subclique dynamically on the fly
	 * @param subclique list of integer indices to entries in the master List of concept identifiers
	 */
	private List<String> getConceptIds(List<Integer> subclique) {
		List<String> cids = new ArrayList<String>();
		for(Integer cidx : subclique)
			cids.add(conceptIds.get(cidx));
		return cids; // Note: will be empty if the subclique indice list is empty
	}

	/**
	 * This function should not normally be directly called by the code 
	 * except during loading of a well formed ConceptClique record from the database
	 * 
	 * @return set the master list of (exact match, case sensitive) identifiers of all concepts deemed equivalent in this clique.
	 */
	public void setConceptIds(List<String> cids) {
		conceptIds = cids;
	}
	
	/**
	 * 
	 * @return the master list of (exact match, case sensitive) identifiers of all concepts deemed equivalent in this clique.
	 */
	public List<String> getConceptIds() {
		return new ArrayList<String>(conceptIds);
	}
	
	/**
	 * This function should not normally be directly called by the code 
	 * except during loading of a well formed ConceptClique record from the database
	 * 
	 * @return set the master list of beacon subcliques (see above)
	 */
	public void setBeaconSubcliques(List<String> subcliques) {
		this.beaconSubcliques = subcliques;
	}
	
	/**
	 * This function should not normally be directly called by the code 
	 * except during the saving of a well formed ConceptClique record to the database
	 * 
	 * @return get the master list of beacon subcliques (see above)
	 */
	public List<String> getBeaconSubcliques() {
		return new ArrayList<String>(beaconSubcliques);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ConceptClique) {
			ConceptClique otherClique = (ConceptClique) other;
			return this.getId().equals(otherClique.getId());
		} else {
			return false;
		}
	}

}
