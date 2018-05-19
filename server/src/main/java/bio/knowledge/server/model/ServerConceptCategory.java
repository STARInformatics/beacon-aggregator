package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerConceptCategory
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T11:38:46.026-07:00")

public class ServerConceptCategory   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("category")
  private String category = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("beacons")
  private List<ServerConceptCategoriesByBeacon> beacons = new ArrayList<ServerConceptCategoriesByBeacon>();

  public ServerConceptCategory id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the CURIE of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)
   * @return id
  **/
  @ApiModelProperty(value = "the CURIE of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerConceptCategory uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * the URI of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)  for the full list of URI)
   * @return uri
  **/
  @ApiModelProperty(value = "the URI of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)  for the full list of URI)")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public ServerConceptCategory category(String category) {
    this.category = category;
    return this;
  }

   /**
   * the human readable label of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model) for  the full list of concept categories) 
   * @return category
  **/
  @ApiModelProperty(value = "the human readable label of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model) for  the full list of concept categories) ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public ServerConceptCategory description(String description) {
    this.description = description;
    return this;
  }

   /**
   * human readable definition assigned by the beacon for the specified concept category 
   * @return description
  **/
  @ApiModelProperty(value = "human readable definition assigned by the beacon for the specified concept category ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ServerConceptCategory beacons(List<ServerConceptCategoriesByBeacon> beacons) {
    this.beacons = beacons;
    return this;
  }

  public ServerConceptCategory addBeaconsItem(ServerConceptCategoriesByBeacon beaconsItem) {
    this.beacons.add(beaconsItem);
    return this;
  }

   /**
   * list of metadata for beacons that support the use of this concept category 
   * @return beacons
  **/
  @ApiModelProperty(value = "list of metadata for beacons that support the use of this concept category ")
  public List<ServerConceptCategoriesByBeacon> getBeacons() {
    return beacons;
  }

  public void setBeacons(List<ServerConceptCategoriesByBeacon> beacons) {
    this.beacons = beacons;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConceptCategory serverConceptCategory = (ServerConceptCategory) o;
    return Objects.equals(this.id, serverConceptCategory.id) &&
        Objects.equals(this.uri, serverConceptCategory.uri) &&
        Objects.equals(this.category, serverConceptCategory.category) &&
        Objects.equals(this.description, serverConceptCategory.description) &&
        Objects.equals(this.beacons, serverConceptCategory.beacons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, category, description, beacons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptCategory {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    beacons: ").append(toIndentedString(beacons)).append("\n");
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

