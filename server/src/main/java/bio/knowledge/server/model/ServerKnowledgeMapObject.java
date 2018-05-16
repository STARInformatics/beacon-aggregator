package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerKnowledgeMapObject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T15:45:49.260-07:00")

public class ServerKnowledgeMapObject   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("prefixes")
  private List<String> prefixes = new ArrayList<String>();

  public ServerKnowledgeMapObject id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the CURIE designating the concept category of a  statement object 
   * @return id
  **/
  @ApiModelProperty(value = "the CURIE designating the concept category of a  statement object ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerKnowledgeMapObject label(String label) {
    this.label = label;
    return this;
  }

   /**
   * the human readable label of the concept category of a statement object 
   * @return label
  **/
  @ApiModelProperty(value = "the human readable label of the concept category of a statement object ")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ServerKnowledgeMapObject prefixes(List<String> prefixes) {
    this.prefixes = prefixes;
    return this;
  }

  public ServerKnowledgeMapObject addPrefixesItem(String prefixesItem) {
    this.prefixes.add(prefixesItem);
    return this;
  }

   /**
   * Get prefixes
   * @return prefixes
  **/
  @ApiModelProperty(value = "")
  public List<String> getPrefixes() {
    return prefixes;
  }

  public void setPrefixes(List<String> prefixes) {
    this.prefixes = prefixes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerKnowledgeMapObject serverKnowledgeMapObject = (ServerKnowledgeMapObject) o;
    return Objects.equals(this.id, serverKnowledgeMapObject.id) &&
        Objects.equals(this.label, serverKnowledgeMapObject.label) &&
        Objects.equals(this.prefixes, serverKnowledgeMapObject.prefixes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, prefixes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerKnowledgeMapObject {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    prefixes: ").append(toIndentedString(prefixes)).append("\n");
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

