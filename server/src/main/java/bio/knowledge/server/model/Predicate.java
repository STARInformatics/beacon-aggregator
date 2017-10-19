package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.PredicateBeacon;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * Predicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-19T15:48:00.887-07:00")

public class Predicate   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("beacons")
  private List<PredicateBeacon> beacons = new ArrayList<PredicateBeacon>();

  public Predicate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * exact unique human readable name of predicate relation 
   * @return name
  **/
  @ApiModelProperty(value = "exact unique human readable name of predicate relation ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Predicate beacons(List<PredicateBeacon> beacons) {
    this.beacons = beacons;
    return this;
  }

  public Predicate addBeaconsItem(PredicateBeacon beaconsItem) {
    this.beacons.add(beaconsItem);
    return this;
  }

   /**
   * list of metadata for beacons that support the use of this predicate relation 
   * @return beacons
  **/
  @ApiModelProperty(value = "list of metadata for beacons that support the use of this predicate relation ")
  public List<PredicateBeacon> getBeacons() {
    return beacons;
  }

  public void setBeacons(List<PredicateBeacon> beacons) {
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
    Predicate predicate = (Predicate) o;
    return Objects.equals(this.name, predicate.name) &&
        Objects.equals(this.beacons, predicate.beacons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, beacons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Predicate {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

