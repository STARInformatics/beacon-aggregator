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
package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * ConceptDetail
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-15T11:46:37.748-07:00")

public class ConceptDetail   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("semanticGroup")
  private String semanticGroup = null;

  @JsonProperty("synonyms")
  private List<String> synonyms = null;

  @JsonProperty("definition")
  private String definition = null;

  @JsonProperty("details")
  private List<Detail> details = null;

  @JsonProperty("beacon")
  private String beacon = null;

  public ConceptDetail id(String id) {
    this.id = id;
    return this;
  }

   /**
   * local object identifier for the concept in the specified knowledge source database 
   * @return id
  **/
  @ApiModelProperty(value = "local object identifier for the concept in the specified knowledge source database ")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ConceptDetail name(String name) {
    this.name = name;
    return this;
  }

   /**
   * canonical human readable name of the concept 
   * @return name
  **/
  @ApiModelProperty(value = "canonical human readable name of the concept ")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ConceptDetail semanticGroup(String semanticGroup) {
    this.semanticGroup = semanticGroup;
    return this;
  }

   /**
   * concept semantic type 
   * @return semanticGroup
  **/
  @ApiModelProperty(value = "concept semantic type ")


  public String getSemanticGroup() {
    return semanticGroup;
  }

  public void setSemanticGroup(String semanticGroup) {
    this.semanticGroup = semanticGroup;
  }

  public ConceptDetail synonyms(List<String> synonyms) {
    this.synonyms = synonyms;
    return this;
  }

  public ConceptDetail addSynonymsItem(String synonymsItem) {
    if (this.synonyms == null) {
      this.synonyms = new ArrayList<String>();
    }
    this.synonyms.add(synonymsItem);
    return this;
  }

   /**
   * list of synonyms for concept 
   * @return synonyms
  **/
  @ApiModelProperty(value = "list of synonyms for concept ")


  public List<String> getSynonyms() {
    return synonyms;
  }

  public void setSynonyms(List<String> synonyms) {
    this.synonyms = synonyms;
  }

  public ConceptDetail definition(String definition) {
    this.definition = definition;
    return this;
  }

   /**
   * concept definition 
   * @return definition
  **/
  @ApiModelProperty(value = "concept definition ")


  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public ConceptDetail details(List<Detail> details) {
    this.details = details;
    return this;
  }

  public ConceptDetail addDetailsItem(Detail detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<Detail>();
    }
    this.details.add(detailsItem);
    return this;
  }

   /**
   * Get details
   * @return details
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<Detail> getDetails() {
    return details;
  }

  public void setDetails(List<Detail> details) {
    this.details = details;
  }

  public ConceptDetail beacon(String beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * beacon ID 
   * @return beacon
  **/
  @ApiModelProperty(value = "beacon ID ")


  public String getBeacon() {
    return beacon;
  }

  public void setBeacon(String beacon) {
    this.beacon = beacon;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConceptDetail conceptDetail = (ConceptDetail) o;
    return Objects.equals(this.id, conceptDetail.id) &&
        Objects.equals(this.name, conceptDetail.name) &&
        Objects.equals(this.semanticGroup, conceptDetail.semanticGroup) &&
        Objects.equals(this.synonyms, conceptDetail.synonyms) &&
        Objects.equals(this.definition, conceptDetail.definition) &&
        Objects.equals(this.details, conceptDetail.details) &&
        Objects.equals(this.beacon, conceptDetail.beacon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, semanticGroup, synonyms, definition, details, beacon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConceptDetail {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    semanticGroup: ").append(toIndentedString(semanticGroup)).append("\n");
    sb.append("    synonyms: ").append(toIndentedString(synonyms)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
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

