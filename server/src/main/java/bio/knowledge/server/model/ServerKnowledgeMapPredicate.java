package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerKnowledgeMapPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T11:38:46.026-07:00")

public class ServerKnowledgeMapPredicate   {
  @JsonProperty("edge_label")
  private String edgeLabel = null;

  @JsonProperty("relation")
  private String relation = null;

  public ServerKnowledgeMapPredicate edgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
    return this;
  }

   /**
   * the human readable edge label of the 'minimal'  predicate of the given relationship
   * @return edgeLabel
  **/
  @ApiModelProperty(value = "the human readable edge label of the 'minimal'  predicate of the given relationship")
  public String getEdgeLabel() {
    return edgeLabel;
  }

  public void setEdgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
  }

  public ServerKnowledgeMapPredicate relation(String relation) {
    this.relation = relation;
    return this;
  }

   /**
   * the human readable label of the 'maximal'  predicate of the given relationship
   * @return relation
  **/
  @ApiModelProperty(value = "the human readable label of the 'maximal'  predicate of the given relationship")
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerKnowledgeMapPredicate serverKnowledgeMapPredicate = (ServerKnowledgeMapPredicate) o;
    return Objects.equals(this.edgeLabel, serverKnowledgeMapPredicate.edgeLabel) &&
        Objects.equals(this.relation, serverKnowledgeMapPredicate.relation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edgeLabel, relation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerKnowledgeMapPredicate {\n");
    
    sb.append("    edgeLabel: ").append(toIndentedString(edgeLabel)).append("\n");
    sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
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

