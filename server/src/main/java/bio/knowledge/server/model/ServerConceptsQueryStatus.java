package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerConceptsQueryStatus
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-12T16:26:47.889-07:00")

public class ServerConceptsQueryStatus   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("keywords")
  private String keywords = null;

  @JsonProperty("types")
  private String types = null;

  @JsonProperty("status")
  private List<ServerConceptsQueryStatusStatus> status = new ArrayList<ServerConceptsQueryStatusStatus>();

  public ServerConceptsQueryStatus queryId(String queryId) {
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

  public ServerConceptsQueryStatus keywords(String keywords) {
    this.keywords = keywords;
    return this;
  }

   /**
   * 'keywords' string parameter to call, echoed back 
   * @return keywords
  **/
  @ApiModelProperty(value = "'keywords' string parameter to call, echoed back ")
  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public ServerConceptsQueryStatus types(String types) {
    this.types = types;
    return this;
  }

   /**
   * 'types' string parameter to call, echoed back 
   * @return types
  **/
  @ApiModelProperty(value = "'types' string parameter to call, echoed back ")
  public String getTypes() {
    return types;
  }

  public void setTypes(String types) {
    this.types = types;
  }

  public ServerConceptsQueryStatus status(List<ServerConceptsQueryStatusStatus> status) {
    this.status = status;
    return this;
  }

  public ServerConceptsQueryStatus addStatusItem(ServerConceptsQueryStatusStatus statusItem) {
    this.status.add(statusItem);
    return this;
  }

   /**
   * array of beacon-specific query status reports 
   * @return status
  **/
  @ApiModelProperty(value = "array of beacon-specific query status reports ")
  public List<ServerConceptsQueryStatusStatus> getStatus() {
    return status;
  }

  public void setStatus(List<ServerConceptsQueryStatusStatus> status) {
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
        Objects.equals(this.keywords, serverConceptsQueryStatus.keywords) &&
        Objects.equals(this.types, serverConceptsQueryStatus.types) &&
        Objects.equals(this.status, serverConceptsQueryStatus.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, keywords, types, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptsQueryStatus {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
    sb.append("    types: ").append(toIndentedString(types)).append("\n");
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

