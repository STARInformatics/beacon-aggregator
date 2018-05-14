package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerKnowledgeMapPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T14:44:00.373-07:00")

public class ServerKnowledgeMapPredicate   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("label")
  private String label = null;

  public ServerKnowledgeMapPredicate id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the CURIE of the predicate of the given relationship
   * @return id
  **/
  @ApiModelProperty(value = "the CURIE of the predicate of the given relationship")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerKnowledgeMapPredicate label(String label) {
    this.label = label;
    return this;
  }

   /**
   * the human readable label of the  predicate ofthe given relationship
   * @return label
  **/
  @ApiModelProperty(value = "the human readable label of the  predicate ofthe given relationship")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
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
    return Objects.equals(this.id, serverKnowledgeMapPredicate.id) &&
        Objects.equals(this.label, serverKnowledgeMapPredicate.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerKnowledgeMapPredicate {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
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

