/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
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
package bio.knowledge.server.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.server.model.ServerPredicate;

/**
 * @author richard
 *
 */
@Component
public class PredicatesRegistry extends HashMap<String,ServerPredicate> {

	private static final long serialVersionUID = 8806767012821677361L;
	
	/**
	 * 
	 * @param translation
	 * @param beaconId
	 */
	public void indexPredicate(BeaconPredicate translation, String beaconId) {
		
		/*
		 *  Index predicates by exact name string (only).
		 *  ALthough it is conceivable that distinct
		 *  beacons will have identically named relations
		 *  that mean something different, we take this 
		 *  as a community curation challenge we can't
		 *  (and won't try to) solve here.
		 */
		String name = translation.getName().toLowerCase();

		if(!containsKey(name)) {
			translation.setName(name); // sanity check to enforce case insensitivity...
			//put(name, translation);
		}
		
		ServerPredicate predicate = get(name);
		
		// Attempt a merge of information between old and new
		
		// Accession Ids?

		// Definitions?
		
		// Beacons: Want to treat the beacons List as a Set collection
		//List<String> beacons = predicate.getBeacons();
		//if(!beacons.contains(beaconId)) {
		//	beacons.add(beaconId);
		//}
	}

}
