package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerPredicatesByBeacon
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T21:48:57.324-07:00")

public class ServerPredicatesByBeacon   {
  @JsonProperty("beacon")
  private Integer beacon = null;

  @JsonProperty("predicates")
  private List<ServerBeaconPredicate> predicates = new ArrayList<ServerBeaconPredicate>();

  public ServerPredicatesByBeacon beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * aggregator index of the given beacon 
   * @return beacon
  **/
  @ApiModelProperty(value = "aggregator index of the given beacon ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
    this.beacon = beacon;
  }

  public ServerPredicatesByBeacon predicates(List<ServerBeaconPredicate> predicates) {
    this.predicates = predicates;
    return this;
  }

  public ServerPredicatesByBeacon addPredicatesItem(ServerBeaconPredicate predicatesItem) {
    this.predicates.add(predicatesItem);
    return this;
  }

   /**
   * Get predicates
   * @return predicates
  **/
  @ApiModelProperty(value = "")
  public List<ServerBeaconPredicate> getPredicates() {
    return predicates;
  }

  public void setPredicates(List<ServerBeaconPredicate> predicates) {
    this.predicates = predicates;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerPredicatesByBeacon serverPredicatesByBeacon = (ServerPredicatesByBeacon) o;
    return Objects.equals(this.beacon, serverPredicatesByBeacon.beacon) &&
        Objects.equals(this.predicates, serverPredicatesByBeacon.predicates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beacon, predicates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerPredicatesByBeacon {\n");
    
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    predicates: ").append(toIndentedString(predicates)).append("\n");
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

