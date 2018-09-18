package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerCliquesQuery
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T16:36:52.027-07:00")

public class ServerCliquesQuery   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("ids")
  private List<String> ids = new ArrayList<String>();

  public ServerCliquesQuery queryId(String queryId) {
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

  public ServerCliquesQuery ids(List<String> ids) {
    this.ids = ids;
    return this;
  }

  public ServerCliquesQuery addIdsItem(String idsItem) {
    this.ids.add(idsItem);
    return this;
  }

   /**
   * 'ids' string parameter to call, echoed back 
   * @return ids
  **/
  @ApiModelProperty(value = "'ids' string parameter to call, echoed back ")
  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerCliquesQuery serverCliquesQuery = (ServerCliquesQuery) o;
    return Objects.equals(this.queryId, serverCliquesQuery.queryId) &&
        Objects.equals(this.ids, serverCliquesQuery.ids);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, ids);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerCliquesQuery {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
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

