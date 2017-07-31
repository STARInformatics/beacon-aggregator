package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Beacon
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-31T10:50:26.572-07:00")

public class Beacon   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("description")
  private String description = null;

  public Beacon id(String id) {
    this.id = id;
    return this;
  }

   /**
   * beacon ID 
   * @return id
  **/
  @ApiModelProperty(value = "beacon ID ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Beacon name(String name) {
    this.name = name;
    return this;
  }

   /**
   * beacon name 
   * @return name
  **/
  @ApiModelProperty(value = "beacon name ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Beacon url(String url) {
    this.url = url;
    return this;
  }

   /**
   * URL used to execute API calls on the beacon 
   * @return url
  **/
  @ApiModelProperty(value = "URL used to execute API calls on the beacon ")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Beacon description(String description) {
    this.description = description;
    return this;
  }

   /**
   * beacon description 
   * @return description
  **/
  @ApiModelProperty(value = "beacon description ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Beacon beacon = (Beacon) o;
    return Objects.equals(this.id, beacon.id) &&
        Objects.equals(this.name, beacon.name) &&
        Objects.equals(this.url, beacon.url) &&
        Objects.equals(this.description, beacon.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, url, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Beacon {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

