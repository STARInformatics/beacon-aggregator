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
 * ServerStatementObject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-28T14:42:53.737-07:00")

public class ServerStatementObject   {
  @JsonProperty("clique")
  private String clique = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("categories")
  private List<String> categories = new ArrayList<String>();

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

  public ServerStatementObject categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  public ServerStatementObject addCategoriesItem(String categoriesItem) {
    this.categories.add(categoriesItem);
    return this;
  }

   /**
   * Semantic categories of the object concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories). 
   * @return categories
  **/
  @ApiModelProperty(value = "Semantic categories of the object concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories). ")
  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
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
        Objects.equals(this.categories, serverStatementObject.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clique, id, name, categories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerStatementObject {\n");
    
    sb.append("    clique: ").append(toIndentedString(clique)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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

