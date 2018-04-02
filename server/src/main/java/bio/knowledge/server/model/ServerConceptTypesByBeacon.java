package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.ServerBeaconConceptType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerConceptTypesByBeacon
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-02T11:38:45.201-07:00")

public class ServerConceptTypesByBeacon   {
  @JsonProperty("beacon")
  private Integer beacon = null;

  @JsonProperty("types")
  private ServerBeaconConceptType types = null;

  public ServerConceptTypesByBeacon beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * aggregator index identifier of the given beacon 
   * @return beacon
  **/
  @ApiModelProperty(value = "aggregator index identifier of the given beacon ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
    this.beacon = beacon;
  }

  public ServerConceptTypesByBeacon types(ServerBeaconConceptType types) {
    this.types = types;
    return this;
  }

   /**
   * Get types
   * @return types
  **/
  @ApiModelProperty(value = "")
  public ServerBeaconConceptType getTypes() {
    return types;
  }

  public void setTypes(ServerBeaconConceptType types) {
    this.types = types;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConceptTypesByBeacon serverConceptTypesByBeacon = (ServerConceptTypesByBeacon) o;
    return Objects.equals(this.beacon, serverConceptTypesByBeacon.beacon) &&
        Objects.equals(this.types, serverConceptTypesByBeacon.types);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beacon, types);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptTypesByBeacon {\n");
    
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    types: ").append(toIndentedString(types)).append("\n");
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

