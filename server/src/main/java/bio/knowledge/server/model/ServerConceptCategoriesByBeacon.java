package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerConceptCategoriesByBeacon
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T11:38:46.026-07:00")

public class ServerConceptCategoriesByBeacon   {
  @JsonProperty("beacon")
  private Integer beacon = null;

  @JsonProperty("categories")
  private List<ServerBeaconConceptCategory> categories = new ArrayList<ServerBeaconConceptCategory>();

  public ServerConceptCategoriesByBeacon beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * Aggregator index identifier of the given beacon 
   * @return beacon
  **/
  @ApiModelProperty(value = "Aggregator index identifier of the given beacon ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
    this.beacon = beacon;
  }

  public ServerConceptCategoriesByBeacon categories(List<ServerBeaconConceptCategory> categories) {
    this.categories = categories;
    return this;
  }

  public ServerConceptCategoriesByBeacon addCategoriesItem(ServerBeaconConceptCategory categoriesItem) {
    this.categories.add(categoriesItem);
    return this;
  }

   /**
   * Get categories
   * @return categories
  **/
  @ApiModelProperty(value = "")
  public List<ServerBeaconConceptCategory> getCategories() {
    return categories;
  }

  public void setCategories(List<ServerBeaconConceptCategory> categories) {
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
    ServerConceptCategoriesByBeacon serverConceptCategoriesByBeacon = (ServerConceptCategoriesByBeacon) o;
    return Objects.equals(this.beacon, serverConceptCategoriesByBeacon.beacon) &&
        Objects.equals(this.categories, serverConceptCategoriesByBeacon.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beacon, categories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptCategoriesByBeacon {\n");
    
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
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

