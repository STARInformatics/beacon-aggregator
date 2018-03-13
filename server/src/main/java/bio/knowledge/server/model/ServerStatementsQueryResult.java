package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.ServerStatement;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * ServerStatementsQueryResult
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-12T22:15:54.933-07:00")

public class ServerStatementsQueryResult   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("beacons")
  private List<Integer> beacons = new ArrayList<Integer>();

  @JsonProperty("pageNumber")
  private Integer pageNumber = null;

  @JsonProperty("pageSize")
  private Integer pageSize = null;

  @JsonProperty("results")
  private List<ServerStatement> results = new ArrayList<ServerStatement>();

  public ServerStatementsQueryResult queryId(String queryId) {
    this.queryId = queryId;
    return this;
  }

   /**
   * session identifier of the query returning the results 
   * @return queryId
  **/
  @ApiModelProperty(value = "session identifier of the query returning the results ")
  public String getQueryId() {
    return queryId;
  }

  public void setQueryId(String queryId) {
    this.queryId = queryId;
  }

  public ServerStatementsQueryResult beacons(List<Integer> beacons) {
    this.beacons = beacons;
    return this;
  }

  public ServerStatementsQueryResult addBeaconsItem(Integer beaconsItem) {
    this.beacons.add(beaconsItem);
    return this;
  }

   /**
   * Get beacons
   * @return beacons
  **/
  @ApiModelProperty(value = "")
  public List<Integer> getBeacons() {
    return beacons;
  }

  public void setBeacons(List<Integer> beacons) {
    this.beacons = beacons;
  }

  public ServerStatementsQueryResult pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

   /**
   * session identifier of the query returning the results 
   * @return pageNumber
  **/
  @ApiModelProperty(value = "session identifier of the query returning the results ")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public ServerStatementsQueryResult pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

   /**
   * session identifier of the query returning the results 
   * @return pageSize
  **/
  @ApiModelProperty(value = "session identifier of the query returning the results ")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public ServerStatementsQueryResult results(List<ServerStatement> results) {
    this.results = results;
    return this;
  }

  public ServerStatementsQueryResult addResultsItem(ServerStatement resultsItem) {
    this.results.add(resultsItem);
    return this;
  }

   /**
   * Get results
   * @return results
  **/
  @ApiModelProperty(value = "")
  public List<ServerStatement> getResults() {
    return results;
  }

  public void setResults(List<ServerStatement> results) {
    this.results = results;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerStatementsQueryResult serverStatementsQueryResult = (ServerStatementsQueryResult) o;
    return Objects.equals(this.queryId, serverStatementsQueryResult.queryId) &&
        Objects.equals(this.beacons, serverStatementsQueryResult.beacons) &&
        Objects.equals(this.pageNumber, serverStatementsQueryResult.pageNumber) &&
        Objects.equals(this.pageSize, serverStatementsQueryResult.pageSize) &&
        Objects.equals(this.results, serverStatementsQueryResult.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, beacons, pageNumber, pageSize, results);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerStatementsQueryResult {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    beacons: ").append(toIndentedString(beacons)).append("\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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

