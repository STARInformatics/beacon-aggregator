package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerConceptsQueryStatus
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-11T17:59:49.447Z")

public class ServerConceptsQueryStatus   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("status")
  private List<ServerConceptsQueryBeaconStatus> status = new ArrayList<ServerConceptsQueryBeaconStatus>();

  public ServerConceptsQueryStatus queryId(String queryId) {
    this.queryId = queryId;
    return this;
  }

   /**
   * session identifier of a query previously initiated by /concepts 
   * @return queryId
  **/
  @ApiModelProperty(value = "session identifier of a query previously initiated by /concepts ")
  public String getQueryId() {
    return queryId;
  }

  public void setQueryId(String queryId) {
    this.queryId = queryId;
  }

  public ServerConceptsQueryStatus status(List<ServerConceptsQueryBeaconStatus> status) {
    this.status = status;
    return this;
  }

  public ServerConceptsQueryStatus addStatusItem(ServerConceptsQueryBeaconStatus statusItem) {
    this.status.add(statusItem);
    return this;
  }

   /**
   * array of beacon-specific query status reports 
   * @return status
  **/
  @ApiModelProperty(value = "array of beacon-specific query status reports ")
  public List<ServerConceptsQueryBeaconStatus> getStatus() {
    return status;
  }

  public void setStatus(List<ServerConceptsQueryBeaconStatus> status) {
    this.status = status;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConceptsQueryStatus serverConceptsQueryStatus = (ServerConceptsQueryStatus) o;
    return Objects.equals(this.queryId, serverConceptsQueryStatus.queryId) &&
        Objects.equals(this.status, serverConceptsQueryStatus.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptsQueryStatus {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

