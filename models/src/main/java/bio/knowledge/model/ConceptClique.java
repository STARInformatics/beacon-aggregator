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
package bio.knowledge.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import bio.knowledge.model.core.neo4j.Neo4jAbstractAnnotatedEntity;

public class ConceptClique extends Neo4jAbstractAnnotatedEntity {
	
	public static boolean notDisjoint(ConceptClique clique1, ConceptClique clique2) {
		return ! Collections.disjoint(clique1.getConceptIds(), clique2.getConceptIds());
	}
	
	public static Set<String> unionOfConceptIds(Collection<ConceptClique> cliques) {
		return cliques.stream().map(
				clique -> { return clique.getConceptIds(); }
		).flatMap(List::stream).collect(Collectors.toSet());
	}
	
	private Set<String> conceptIds = new HashSet<String>();
	
	public boolean isEmpty() {
		return conceptIds.isEmpty();
	}
	
	public int size() {
		return conceptIds.size();
	}
	
	public boolean addAll(Collection<String> collection) {
		return conceptIds.addAll(collection);
	}
	
	public boolean addAll(ConceptClique clique) {
		return conceptIds.addAll(clique.conceptIds);
	}
	
	public ConceptClique() {
		
	}
	
	public ConceptClique(Collection<String> conceptIds) {
		this.conceptIds.addAll(conceptIds);
	}
	
	public ConceptClique(String[] conceptIds) {
		this(Arrays.asList(conceptIds));
	}
	
	public List<String> getConceptIds() {
		return new ArrayList<String>(conceptIds);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
