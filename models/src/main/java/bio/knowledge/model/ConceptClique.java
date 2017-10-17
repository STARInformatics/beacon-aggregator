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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bio.knowledge.model.core.neo4j.Neo4jAbstractAnnotatedEntity;

@NodeEntity(label="ConceptClique")
public class ConceptClique extends Neo4jAbstractAnnotatedEntity {
	
	private static Logger _logger = LoggerFactory.getLogger(ConceptClique.class);
			
	public static boolean notDisjoint(ConceptClique clique1, ConceptClique clique2) {
		return ! Collections.disjoint(clique1.getConceptIds(), clique2.getConceptIds());
	}
	
	public static Set<String> unionOfConceptIds(Collection<ConceptClique> cliques) {
		return cliques.stream().map(
				clique -> { return clique.getConceptIds(); }
		).flatMap(List::stream).collect(Collectors.toSet());
	}
	
	private List<String> conceptIds = new ArrayList<String>();
	
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
		assignAccessionId();
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
	
	@Transient
	private Boolean normalizedAccessionId = false;

	/**
	 * 
	 */
	public void assignAccessionId() {
		
		// Heuristic in Java code to set a reasonable "equivalent concept clique" canonical identifier
		// (See also bio.knowledge.database.repository.ConceptCliqueRepository.accessionIdFilter)
		
		if(conceptIds.isEmpty()) {
			_logger.error("assignAccession(): clique set of concept ids is empty??!");
			return ;
		}
		
		String accessionId = null ;
		
		// Detect matches in the BioNameSpace in order of precedence?
		for (BioNameSpace namespace : BioNameSpace.values()) {
			/*
			 * Need to scan all the identifiers 
			 * for the first match to the given prefix.
			 * 
			 * First match past the gate wins 
			 * (probably faulty heuristic, but alas...)
			 * Prefix normalized to lower case... 
			 * Case sensitivity of prefix id's is a thorny issue!
			 */
			for ( String id : conceptIds ) {
				
				if(id.indexOf(":")<=0) continue; // not a valid CURIE? Ignore?
				
				String[] idPart = id.split(":");
				
				// ignore case for namespace detection
				if( idPart[0].equalsIgnoreCase(namespace.name()) 
				) {
					/*
					 *  RMB Oct 17, 2017 Design decision:
					 *  Use whichever candidate CURIE first passes 
					 *  the namespace test here *without* normalizing 
					 *  string case to the BioNameSpace recorded case.
					 *  
					 *  This won't solve the problem of different
					 *  beacons using different string case for 
					 *  essentially the same namespace...
					 *  but will ensure that at least one 
					 *  beacon recognizes the identifier?
					 *  
					 *  TODO: We'll somehow need to deal with this
					 *  case issue somewhere else, for 
					 *  concept details, statement retrievals, etc.
					 */
					accessionId = id;
					break;
				}
			}
			
			/* 
			 * We found a candidate canonical clique id? 
			 * No need to screen further namespaces?
			 */
			if(accessionId!=null) break; 
		}
		
		if( accessionId==null ) {
			/*
			 * Just take the first one in the list.
			 * Less satisfying heuristic butbetter than returning null?
			 */
			accessionId = conceptIds.get(0);
		}
		
		// Best guess accessionId is set here
		super.setId(accessionId);
		
		normalizedAccessionId = true;
	}
	
	/**
	 * 
	 */
	@Override
	public void setId(String accessionId) {
		// Normalize every time you set the accessionId before you set it
		assignAccessionId();
	}

	/*
	 * We override the ConceptClique id() function to ensure 
	 * that a normalized CURIE is always used.
	 * 
	 * (non-Javadoc)
	 * @see bio.knowledge.model.core.neo4j.Neo4jAbstractIdentifiedEntity#getId()
	 */
	@Override
	public String getId() {
		String id = super.getId();
		if( id==null || id.isEmpty() || !normalizedAccessionId ) assignAccessionId();
		return super.getId();
	}

}
