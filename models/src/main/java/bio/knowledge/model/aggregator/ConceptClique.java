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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.DomainModelException;
import bio.knowledge.model.core.neo4j.Neo4jAbstractIdentifiedEntity;

@NodeEntity(label="ConceptClique")
public class ConceptClique extends Neo4jAbstractIdentifiedEntity {
	
	public ConceptClique() { }
	
	@Relationship( type="SUBCLIQUE" )
    private Set<BeaconConceptSubClique> subcliques = new HashSet<BeaconConceptSubClique>() ;

	/**
	 * 
	 * @param subcliques Set for several beacons
	 */
	public void setBeaconSubcliques( Set<BeaconConceptSubClique> subcliques ) {
		this.subcliques = subcliques ;
	}
	
	/**
	 * 
	 * @param subclique associated with one beacon
	 */
	public void addBeaconSubclique( BeaconConceptSubClique subclique ) {
		if( subcliques==null)
			subcliques = new HashSet<BeaconConceptSubClique>() ;
		subcliques.add(subclique);
	}
	
	/**
	 * 
	 * @return all the beacon records
	 */
	public Set<BeaconConceptSubClique> getBeaconSubclique() {
		return subcliques;
	}
	
	/**
	 * 
	 * @param beaconId
	 * @param conceptIds
	 */
	public void addConceptIds( String beaconId, List<String> conceptIds ) {
		
		if( beaconId==null || beaconId.isEmpty() ) 
			throw new DomainModelException("ConceptClique() ERROR: null or empty beacon id?");

		BeaconConceptSubClique subclique = new BeaconConceptSubClique(beaconId);
		subclique.setConceptIds(conceptIds);
		addBeaconSubclique(subclique);
	}
	
	/**
	 * @param beaconId
	 * @return the list of identifiers of concepts deemed equivalent by a specified beacon.
	 */
	public List<String> getConceptIds(String beaconId) {
		Set<String> conceptIds = new HashSet<String>();
		for(BeaconConceptSubClique subclique : subcliques ) {
			if(subclique.getId().equals(Aggregator.CURIE(beaconId))) {
				conceptIds.addAll(subclique.getConceptIds());
			}
		}
		return new ArrayList<String>(conceptIds);
	}
	
	/**
	 * 
	 * @return the list of identifiers of concepts deemed equivalent by any beacon.
	 */
	public List<String> getConceptIds() {
		Set<String> conceptIds = new HashSet<String>();
		for(BeaconConceptSubClique subclique : subcliques ) {
			conceptIds.addAll(subclique.getConceptIds());
		}
		return new ArrayList<String>(conceptIds);
	}


}
