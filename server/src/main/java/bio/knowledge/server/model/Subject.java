/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 	STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * 					2017	NIH National Center for Advancement of Translational Sciences
 * 				 2015-17	Scripps Institute (USA) - Dr. Benjamin Good
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
package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Subject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-15T11:46:37.748-07:00")

public class Subject   {

	@JsonProperty("clique")
	private String clique = null;

	@JsonProperty("id")
	private String id = null;

	@JsonProperty("name")
	private String name = null;

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Subject id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * CURIE-encoded canonical identifier of equivalent concept clique 
	 * @return id
	 **/
	@ApiModelProperty(value = "CURIE-encoded canonical identifier of associated equivalent concept clique")

	/**
	 * 
	 * @return
	 */
	public String getClique() {
		return clique;
	}

	/**
	 * 
	 * @param clique
	 */
	public void setClique(String clique) {
		this.clique = clique;
	}

	/**
	 * CURIE-encoded identifier of concept 
	 * @return id
	 **/
	@ApiModelProperty(value = "CURIE-encoded identifier of subject concept ")

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Subject name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * human readable label of subject concept
	 * @return name
	 **/
	@ApiModelProperty(value = "human readable label of subject concept")


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Subject subject = (Subject) o;
		return Objects.equals(this.id, subject.id) &&
				Objects.equals(this.name, subject.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Subject {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}

