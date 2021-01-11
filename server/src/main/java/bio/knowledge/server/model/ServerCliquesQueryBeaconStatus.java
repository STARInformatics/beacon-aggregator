package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import bio.knowledge.server.blackboard.BeaconStatusInterface;
import io.swagger.annotations.ApiModelProperty;
/**
 * ServerCliquesQueryBeaconStatus
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T21:48:57.324-07:00")

public class ServerCliquesQueryBeaconStatus implements BeaconStatusInterface {
  @JsonProperty("beacon")
  private Integer beacon = null;

  @JsonProperty("status")
  private Integer status = null;

  @JsonProperty("count")
  private Integer count = null;

  public ServerCliquesQueryBeaconStatus beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * Index number of beacon providing these concept details 
   * @return beacon
  **/
  @ApiModelProperty(value = "Index number of beacon providing these concept details ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
    this.beacon = beacon;
  }

  public ServerCliquesQueryBeaconStatus status(Integer status) {
    this.status = status;
    return this;
  }

   /**
   * Http code status of beacon API - 200 means 'data ready', 102 means 'query in progress', other codes (e.g. 500) are server errors. Once a beacon has a '200' success code, then the /cliques/data  endpoint may be used to retrieve it 
   * @return status
  **/
  @ApiModelProperty(value = "Http code status of beacon API - 200 means 'data ready', 102 means 'query in progress', other codes (e.g. 500) are server errors. Once a beacon has a '200' success code, then the /cliques/data  endpoint may be used to retrieve it ")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public ServerCliquesQueryBeaconStatus count(Integer count) {
    this.count = count;
    return this;
  }

   /**
   * When a 200 status code is returned, this integer designates  the number of ids matched by the query for the given beacon. 
   * @return count
  **/
  @ApiModelProperty(value = "When a 200 status code is returned, this integer designates  the number of ids matched by the query for the given beacon. ")
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerCliquesQueryBeaconStatus serverCliquesQueryBeaconStatus = (ServerCliquesQueryBeaconStatus) o;
    return Objects.equals(this.beacon, serverCliquesQueryBeaconStatus.beacon) &&
        Objects.equals(this.status, serverCliquesQueryBeaconStatus.status) &&
        Objects.equals(this.count, serverCliquesQueryBeaconStatus.count);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beacon, status, count);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerCliquesQueryBeaconStatus {\n");
    
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
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

@Override
public void setDiscovered(Integer discovered) {
	// TODO Auto-generated method stub
	
}

@Override
public void setProcessed(Integer processed) {
	// TODO Auto-generated method stub
	
}
}

