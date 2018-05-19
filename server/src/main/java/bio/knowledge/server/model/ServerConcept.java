package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerConcept
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T15:02:51.082-07:00")

public class ServerConcept   {
  @JsonProperty("clique")
  private String clique = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("category")
  private String category = null;

  public ServerConcept clique(String clique) {
    this.clique = clique;
    return this;
  }

   /**
   * CURIE identifying the inferred equivalent concept clique to which the concept belongs. This is assigned by an identifier precedence heuristic by the beacon-aggregator 
   * @return clique
  **/
  @ApiModelProperty(value = "CURIE identifying the inferred equivalent concept clique to which the concept belongs. This is assigned by an identifier precedence heuristic by the beacon-aggregator ")
  public String getClique() {
    return clique;
  }

  public void setClique(String clique) {
    this.clique = clique;
  }

  public ServerConcept name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Canonical human readable name of the concept 
   * @return name
  **/
  @ApiModelProperty(value = "Canonical human readable name of the concept ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServerConcept category(String category) {
    this.category = category;
    return this;
  }

   /**
   * Concept category associated with the given concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories). 
   * @return category
  **/
  @ApiModelProperty(value = "Concept category associated with the given concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of categories). ")
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
    ServerConcept serverConcept = (ServerConcept) o;
    return Objects.equals(this.clique, serverConcept.clique) &&
        Objects.equals(this.name, serverConcept.name) &&
        Objects.equals(this.category, serverConcept.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clique, name, category);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConcept {\n");
    
    sb.append("    clique: ").append(toIndentedString(clique)).append("\n");
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

