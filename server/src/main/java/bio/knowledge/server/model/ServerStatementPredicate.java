package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerStatementPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T21:48:57.324-07:00")

public class ServerStatementPredicate   {
  @JsonProperty("edge_label")
  private String edgeLabel = null;

  @JsonProperty("relation")
  private String relation = null;

  @JsonProperty("negated")
  private Boolean negated = null;

  public ServerStatementPredicate edgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
    return this;
  }

   /**
   * The predicate edge label associated with this statement, which should be as published by the /predicates API endpoint and must be taken from the minimal predicate ('slot') list of the [Biolink Model](https://biolink.github.io/biolink-model). 
   * @return edgeLabel
  **/
  @ApiModelProperty(value = "The predicate edge label associated with this statement, which should be as published by the /predicates API endpoint and must be taken from the minimal predicate ('slot') list of the [Biolink Model](https://biolink.github.io/biolink-model). ")
  public String getEdgeLabel() {
    return edgeLabel;
  }

  public void setEdgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
  }

  public ServerStatementPredicate relation(String relation) {
    this.relation = relation;
    return this;
  }

   /**
   * The predicate relation associated with this statement, which should be as published by the /predicates API endpoint with the preferred format being a CURIE where one exists, but strings/labels acceptable. This relation may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation in cases where the source provides more granularity (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447)
   * @return relation
  **/
  @ApiModelProperty(value = "The predicate relation associated with this statement, which should be as published by the /predicates API endpoint with the preferred format being a CURIE where one exists, but strings/labels acceptable. This relation may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation in cases where the source provides more granularity (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447)")
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public ServerStatementPredicate negated(Boolean negated) {
    this.negated = negated;
    return this;
  }

   /**
   * (Optional) a boolean that if set to true, indicates the edge statement is negated i.e. is not true 
   * @return negated
  **/
  @ApiModelProperty(value = "(Optional) a boolean that if set to true, indicates the edge statement is negated i.e. is not true ")
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
    ServerStatementPredicate serverStatementPredicate = (ServerStatementPredicate) o;
    return Objects.equals(this.edgeLabel, serverStatementPredicate.edgeLabel) &&
        Objects.equals(this.relation, serverStatementPredicate.relation) &&
        Objects.equals(this.negated, serverStatementPredicate.negated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edgeLabel, relation, negated);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerStatementPredicate {\n");
    
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

