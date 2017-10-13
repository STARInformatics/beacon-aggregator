package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-12T22:40:43.205-07:00")

public class ServerPredicate   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("definition")
  private String definition = null;

  @JsonProperty("beacons")
  private List<String> beacons = new ArrayList<String>();

  public ServerPredicate id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of predicate resource 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of predicate resource ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerPredicate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable name of predicate 
   * @return name
  **/
  @ApiModelProperty(value = "human readable name of predicate ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServerPredicate definition(String definition) {
    this.definition = definition;
    return this;
  }

   /**
   * human readable definition for the predicate relation 
   * @return definition
  **/
  @ApiModelProperty(value = "human readable definition for the predicate relation ")
  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public ServerPredicate beacons(List<String> beacons) {
    this.beacons = beacons;
    return this;
  }

  public ServerPredicate addBeaconsItem(String beaconsItem) {
    this.beacons.add(beaconsItem);
    return this;
  }

   /**
   * list of aggregator indices for beacons that support the use of this predicate relation 
   * @return beacons
  **/
  @ApiModelProperty(value = "list of aggregator indices for beacons that support the use of this predicate relation ")
  public List<String> getBeacons() {
    return beacons;
  }

  public void setBeacons(List<String> beacons) {
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
    ServerPredicate predicate = (ServerPredicate) o;
    return Objects.equals(this.id, predicate.id) &&
        Objects.equals(this.name, predicate.name) &&
        Objects.equals(this.definition, predicate.definition) &&
        Objects.equals(this.beacons, predicate.beacons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, definition, beacons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerPredicate {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
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
