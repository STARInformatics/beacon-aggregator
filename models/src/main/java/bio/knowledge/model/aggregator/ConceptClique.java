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
import java.util.Set;
import java.util.TreeSet;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.DomainModelException;
import bio.knowledge.model.CURIE;
import bio.knowledge.model.core.neo4j.Neo4jAbstractIdentifiedEntity;

@NodeEntity(label="ConceptClique")
public class ConceptClique extends Neo4jAbstractIdentifiedEntity {
	
	public ConceptClique() { }
	
	@Override
	public String getName() {
		String name = super.getName();
		if(name==null || name.isEmpty())
			return getId();
		return super.getName();
	}

	/* 
	 * Using a TreeSet here is important to order the set
	 * by the accessionId which will be the beacon Id's
	 * This ordering may help us in a few places. 
	 */
	@Relationship( type="SUBCLIQUE" )
    private Set<BeaconConceptSubClique> subcliques = new TreeSet<BeaconConceptSubClique>() ;

	/**
	 * 
	 * @param subcliques Set for several beacons
	 */
	public void setBeaconSubcliques( Set<BeaconConceptSubClique> subcliques ) {
		this.subcliques = subcliques ;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<BeaconConceptSubClique> removeBeaconSubcliques() {
		List<BeaconConceptSubClique> oldSubcliques = 
				new ArrayList<BeaconConceptSubClique>(subcliques);
		subcliques = new TreeSet<BeaconConceptSubClique>() ;
		return oldSubcliques;
	}
	
	/**
	 * 
	 * @param subclique associated with one beacon
	 */
	public void addBeaconSubclique( BeaconConceptSubClique subclique ) {
		if( subcliques==null)
			subcliques = new TreeSet<BeaconConceptSubClique>() ;
		subcliques.add(subclique);
	}
	
	/**
	 * 
	 * @return all the beacon records
	 */
	public Set<BeaconConceptSubClique> getBeaconSubCliques() {
		return subcliques;
	}
	
	/**
	 * @param beaconId String identifier of the beacon 
	 * @return BeaconConceptSubClique corresponding to the given beaconId
	 */
	public BeaconConceptSubClique getBeaconSubClique(String beaconId) {
		String curie = CURIE.makeCurie(CURIE.CONCEPT_QUALIFIER, beaconId);
		for(BeaconConceptSubClique subclique : subcliques ) {
			if(subclique.getId().equals(curie)) {
				return subclique;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param beaconId String beacon identifier of the beacon that asserts the equivalence of these concept identifiers
	 * @param conceptIds concept identifiers to be added
	 */
	public void addConceptIds( String beaconId, List<String> conceptIds ) {
		
		if( beaconId==null || beaconId.isEmpty() ) 
			throw new DomainModelException("ConceptClique() ERROR: null or empty beacon id?");
		
		// retrieve...
		BeaconConceptSubClique subclique = getBeaconSubClique(beaconId);
		if(subclique==null) {
			// or create and add a new beacon subclique, as necessary
			subclique = new BeaconConceptSubClique(beaconId);
			addBeaconSubclique(subclique);
		}
		// then add the new conceptIds
		subclique.addConceptIds(conceptIds);
	}
	
	/**
	 * @param beaconId
	 * @return the list of identifiers of concepts deemed equivalent by a specified beacon.
	 */
	public List<String> getConceptIds(String beaconId) {
		
		BeaconConceptSubClique subclique = getBeaconSubClique(beaconId);
		if(subclique!=null)
			return subclique.getConceptIds();
		
		return new ArrayList<String>(); // empty list
	}
	
	/**
	 * 
	 * @return the list of identifiers of concepts deemed equivalent in at least one beacon subclique.
	 */
	public List<String> getConceptIds() {
		Set<String> conceptIds = new TreeSet<String>();
		for(BeaconConceptSubClique subclique : subcliques ) {
			conceptIds.addAll(subclique.getConceptIds());
		}
		return new ArrayList<String>(conceptIds);
	}


}
