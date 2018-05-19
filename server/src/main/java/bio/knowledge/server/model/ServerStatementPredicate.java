package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerStatementPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T11:38:46.026-07:00")

public class ServerStatementPredicate   {
  @JsonProperty("edge_label")
  private String edgeLabel = null;

  @JsonProperty("relation")
  private String relation = null;

  public ServerStatementPredicate edgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
    return this;
  }

   /**
   * Relationship predicate drawn from the Biolink Model based list of Translator minimal predicate ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of minimal predicates). 
   * @return edgeLabel
  **/
  @ApiModelProperty(value = "Relationship predicate drawn from the Biolink Model based list of Translator minimal predicate ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of minimal predicates). ")
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
   * Predicate relation; should be drawn Biolink Model based list of Translator minimal predicate ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of minimal predicates)., but reasoner-specific extensions allowed. Preferred format is a CURIE, where one exists, but strings/labels acceptable. 
   * @return relation
  **/
  @ApiModelProperty(value = "Predicate relation; should be drawn Biolink Model based list of Translator minimal predicate ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of minimal predicates)., but reasoner-specific extensions allowed. Preferred format is a CURIE, where one exists, but strings/labels acceptable. ")
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
    ServerStatementPredicate serverStatementPredicate = (ServerStatementPredicate) o;
    return Objects.equals(this.edgeLabel, serverStatementPredicate.edgeLabel) &&
        Objects.equals(this.relation, serverStatementPredicate.relation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edgeLabel, relation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerStatementPredicate {\n");
    
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

