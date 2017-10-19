package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * PredicateBeacon
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-19T15:48:00.887-07:00")

public class PredicateBeacon   {
  @JsonProperty("beacon")
  private String beacon = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("definition")
  private String definition = null;

  public PredicateBeacon beacon(String beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * aggregator index of the given beacon 
   * @return beacon
  **/
  @ApiModelProperty(value = "aggregator index of the given beacon ")
  public String getBeacon() {
    return beacon;
  }

  public void setBeacon(String beacon) {
    this.beacon = beacon;
  }

  public PredicateBeacon id(String id) {
    this.id = id;
    return this;
  }

   /**
   * unique CURIE-encoded identifier of predicate aggregator indices of beacons in the given beacon 
   * @return id
  **/
  @ApiModelProperty(value = "unique CURIE-encoded identifier of predicate aggregator indices of beacons in the given beacon ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PredicateBeacon definition(String definition) {
    this.definition = definition;
    return this;
  }

   /**
   * human readable definition assigned by the beacon for the predicate relation 
   * @return definition
  **/
  @ApiModelProperty(value = "human readable definition assigned by the beacon for the predicate relation ")
  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PredicateBeacon predicateBeacon = (PredicateBeacon) o;
    return Objects.equals(this.beacon, predicateBeacon.beacon) &&
        Objects.equals(this.id, predicateBeacon.id) &&
        Objects.equals(this.definition, predicateBeacon.definition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beacon, id, definition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PredicateBeacon {\n");
    
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
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

