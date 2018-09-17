package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * Single local concept categories from a given beacon 
 */
@ApiModel(description = "Single local concept categories from a given beacon ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-28T14:42:53.737-07:00")

public class ServerBeaconConceptCategory   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("category")
  private String category = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public ServerBeaconConceptCategory id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the 'local' CURIE-encoded identifier of the given concept category, as published by the given beacon 
   * @return id
  **/
  @ApiModelProperty(value = "the 'local' CURIE-encoded identifier of the given concept category, as published by the given beacon ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerBeaconConceptCategory uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * the 'local' URI of the given concept category,  as published by the given beacon 
   * @return uri
  **/
  @ApiModelProperty(value = "the 'local' URI of the given concept category,  as published by the given beacon ")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public ServerBeaconConceptCategory category(String category) {
    this.category = category;
    return this;
  }

   /**
   * the 'local' human readable label of the given concept category, as published by the given beacon 
   * @return category
  **/
  @ApiModelProperty(value = "the 'local' human readable label of the given concept category, as published by the given beacon ")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public ServerBeaconConceptCategory frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of instances of the specified concept category is used in statements within the given beacon 
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of instances of the specified concept category is used in statements within the given beacon ")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerBeaconConceptCategory serverBeaconConceptCategory = (ServerBeaconConceptCategory) o;
    return Objects.equals(this.id, serverBeaconConceptCategory.id) &&
        Objects.equals(this.uri, serverBeaconConceptCategory.uri) &&
        Objects.equals(this.category, serverBeaconConceptCategory.category) &&
        Objects.equals(this.frequency, serverBeaconConceptCategory.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, category, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerBeaconConceptCategory {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
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

