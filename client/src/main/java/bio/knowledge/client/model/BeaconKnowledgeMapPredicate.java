/*
 * Translator Knowledge Beacon API
 * This is the Translator Knowledge Beacon web service application programming interface (API). 
 *
 * OpenAPI spec version: 1.1.1
 * Contact: richard@starinformatics.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package bio.knowledge.client.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModelProperty;

/**
 * BeaconKnowledgeMapPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-06-26T20:44:41.808Z")
public class BeaconKnowledgeMapPredicate {
  @SerializedName("edge_label")
  private String edgeLabel = null;

  @SerializedName("relation")
  private String relation = null;

  @SerializedName("negated")
  private Boolean negated = null;

  public BeaconKnowledgeMapPredicate edgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
    return this;
  }

   /**
   * Human readable name of the 'minimal' standard Biolink Model predicate relationship name.   See [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of terms. 
   * @return edgeLabel
  **/
  @ApiModelProperty(example = "null", value = "Human readable name of the 'minimal' standard Biolink Model predicate relationship name.   See [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of terms. ")
  public String getEdgeLabel() {
    return edgeLabel;
  }

  public void setEdgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
  }

  public BeaconKnowledgeMapPredicate relation(String relation) {
    this.relation = relation;
    return this;
  }

   /**
   * Human readable name of a 'maximal' Biolink Model or beacon-specific (or Reasoner-specific) predicate relationship name. 
   * @return relation
  **/
  @ApiModelProperty(example = "null", value = "Human readable name of a 'maximal' Biolink Model or beacon-specific (or Reasoner-specific) predicate relationship name. ")
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public BeaconKnowledgeMapPredicate negated(Boolean negated) {
    this.negated = negated;
    return this;
  }

   /**
   * Get negated
   * @return negated
  **/
  @ApiModelProperty(example = "null", value = "")
  public Boolean getNegated() {
    return negated;
  }

  public void setNegated(Boolean negated) {
    this.negated = negated;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconKnowledgeMapPredicate beaconKnowledgeMapPredicate = (BeaconKnowledgeMapPredicate) o;
    return Objects.equals(this.edgeLabel, beaconKnowledgeMapPredicate.edgeLabel) &&
        Objects.equals(this.relation, beaconKnowledgeMapPredicate.relation) &&
        Objects.equals(this.negated, beaconKnowledgeMapPredicate.negated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edgeLabel, relation, negated);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconKnowledgeMapPredicate {\n");
    
    sb.append("    edgeLabel: ").append(toIndentedString(edgeLabel)).append("\n");
    sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
    sb.append("    negated: ").append(toIndentedString(negated)).append("\n");
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

