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

import bio.knowledge.model.CURIE;
import bio.knowledge.model.core.neo4j.Neo4jAbstractIdentifiedEntity;

@NodeEntity(label="BeaconConceptSubClique")
public class BeaconConceptSubClique 
	extends Neo4jAbstractIdentifiedEntity {
	
	private Set<String> conceptIds = new TreeSet<>();
	
	public BeaconConceptSubClique( ) {}
	
	public BeaconConceptSubClique( String beaconId ) {
		super(
				CURIE.makeCurie( CURIE.CONCEPT_QUALIFIER, beaconId ),
				"Beacon Subclique: "+beaconId,
				"Beacon Equivalent Concept Subclique"
			);
	}
	
	public void addConceptIds(List<String> conceptIds) {
		this.conceptIds.addAll(conceptIds);
	}
	
	public List<String> getConceptIds() {
		return new ArrayList<String>(conceptIds);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		} else if (! (other instanceof BeaconConceptSubClique)) {
			return false;
		} else {
			BeaconConceptSubClique otherSubclique = (BeaconConceptSubClique) other;
			
			// assert equivalence by Beacon id's
			return this.getId().equals(otherSubclique.getId());
		}		
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	
}
