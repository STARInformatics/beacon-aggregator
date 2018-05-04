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

import org.neo4j.ogm.annotation.NodeEntity;

import bio.knowledge.model.core.neo4j.Neo4jAbstractDatabaseEntity;
import bio.knowledge.ontology.BiolinkClass;

/**
 * @author Richard
 * December 14, 2017 Revision: move towards external RDF/OWL data typing of concepts
 *
 */
@NodeEntity(label="ConceptType")
public class ConceptTypeEntry extends Neo4jAbstractDatabaseEntity {

    private String curie;
	private String label;
	private String definition;

	public ConceptTypeEntry() {
		super();
	}

	public ConceptTypeEntry(
			String curie,
			String label,
			String definition
	) {
		this.curie = curie;
		this.label = label;
		this.definition = definition;
	}

	public ConceptTypeEntry(BiolinkClass biolinkClass) {
		this(biolinkClass.getCurie(), biolinkClass.getName(), biolinkClass.getDescription());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ConceptTypeEntry) {
			ConceptTypeEntry o = (ConceptTypeEntry)other;
			return this.getCurie().equals(o.getCurie());
			
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getCurie().hashCode();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getDefinition() {
		return definition;
	}

	public void setCurie(String curie) {
		this.curie = curie;
	}

	public String getCurie() {
		return curie;
	}

	@Override
	public String toString() {
		return label;
	}

}
