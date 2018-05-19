package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerStatementObject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T11:38:46.026-07:00")

public class ServerStatementObject   {
  @JsonProperty("clique")
  private String clique = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("category")
  private String category = null;

  public ServerStatementObject clique(String clique) {
    this.clique = clique;
    return this;
  }

   /**
   * CURIE-encoded canonical identifier of \"equivalent concepts clique\" of the object concept 
   * @return clique
  **/
  @ApiModelProperty(value = "CURIE-encoded canonical identifier of \"equivalent concepts clique\" of the object concept ")
  public String getClique() {
    return clique;
  }

  public void setClique(String clique) {
    this.clique = clique;
  }

  public ServerStatementObject id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of the object concept 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of the object concept ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerStatementObject name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable label of the object concept
   * @return name
  **/
  @ApiModelProperty(value = "human readable label of the object concept")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServerStatementObject category(String category) {
    this.category = category;
    return this;
  }

   /**
   * Semantic category of the object concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories). 
   * @return category
  **/
  @ApiModelProperty(value = "Semantic category of the object concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories). ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerStatementObject serverStatementObject = (ServerStatementObject) o;
    return Objects.equals(this.clique, serverStatementObject.clique) &&
        Objects.equals(this.id, serverStatementObject.id) &&
        Objects.equals(this.name, serverStatementObject.name) &&
        Objects.equals(this.category, serverStatementObject.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clique, id, name, category);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerStatementObject {\n");
    
    sb.append("    clique: ").append(toIndentedString(clique)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
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

