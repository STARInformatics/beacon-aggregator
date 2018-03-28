package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerLogEntry
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T22:57:55.565-07:00")

public class ServerLogEntry   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("timestamp")
  private String timestamp = null;

  @JsonProperty("beacon")
  private Integer beacon = null;

  @JsonProperty("query")
  private String query = null;

  @JsonProperty("message")
  private String message = null;

  public ServerLogEntry queryId(String queryId) {
    this.queryId = queryId;
    return this;
  }

   /**
   * session identifier of initiated query 
   * @return queryId
  **/
  @ApiModelProperty(value = "session identifier of initiated query ")
  public String getQueryId() {
    return queryId;
  }

  public void setQueryId(String queryId) {
    this.queryId = queryId;
  }

  public ServerLogEntry timestamp(String timestamp) {
    this.timestamp = timestamp;
    return this;
  }

   /**
   * timestamp 
   * @return timestamp
  **/
  @ApiModelProperty(value = "timestamp ")
  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public ServerLogEntry beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * aggregator assigned beacon index identifier 
   * @return beacon
  **/
  @ApiModelProperty(value = "aggregator assigned beacon index identifier ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
    this.beacon = beacon;
  }

  public ServerLogEntry query(String query) {
    this.query = query;
    return this;
  }

   /**
   * URL of the API call executed by the aggregator 
   * @return query
  **/
  @ApiModelProperty(value = "URL of the API call executed by the aggregator ")
  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public ServerLogEntry message(String message) {
    this.message = message;
    return this;
  }

   /**
   * error message 
   * @return message
  **/
  @ApiModelProperty(value = "error message ")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerLogEntry serverLogEntry = (ServerLogEntry) o;
    return Objects.equals(this.queryId, serverLogEntry.queryId) &&
        Objects.equals(this.timestamp, serverLogEntry.timestamp) &&
        Objects.equals(this.beacon, serverLogEntry.beacon) &&
        Objects.equals(this.query, serverLogEntry.query) &&
        Objects.equals(this.message, serverLogEntry.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, timestamp, beacon, query, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerLogEntry {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    query: ").append(toIndentedString(query)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

