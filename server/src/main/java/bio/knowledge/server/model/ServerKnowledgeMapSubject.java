package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * ServerKnowledgeMapSubject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T14:35:19.924-07:00")

public class ServerKnowledgeMapSubject   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("prefixes")
  private List<String> prefixes = new ArrayList<String>();

  public ServerKnowledgeMapSubject id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the CURIE designating the concept type of a  statement subject 
   * @return id
  **/
  @ApiModelProperty(value = "the CURIE designating the concept type of a  statement subject ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerKnowledgeMapSubject label(String label) {
    this.label = label;
    return this;
  }

   /**
   * the human readable label of the concept type of a statement subject 
   * @return label
  **/
  @ApiModelProperty(value = "the human readable label of the concept type of a statement subject ")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ServerKnowledgeMapSubject prefixes(List<String> prefixes) {
    this.prefixes = prefixes;
    return this;
  }

  public ServerKnowledgeMapSubject addPrefixesItem(String prefixesItem) {
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
    ServerKnowledgeMapSubject serverKnowledgeMapSubject = (ServerKnowledgeMapSubject) o;
    return Objects.equals(this.id, serverKnowledgeMapSubject.id) &&
        Objects.equals(this.label, serverKnowledgeMapSubject.label) &&
        Objects.equals(this.prefixes, serverKnowledgeMapSubject.prefixes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, prefixes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerKnowledgeMapSubject {\n");
    
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

