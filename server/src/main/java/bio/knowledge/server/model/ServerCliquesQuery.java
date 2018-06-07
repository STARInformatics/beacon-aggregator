package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * ServerCliquesQuery
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-06T22:00:04.615Z")

public class ServerCliquesQuery   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("identifiers")
  private List<String> identifiers = new ArrayList<String>();

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

  public ServerCliquesQuery identifiers(List<String> identifiers) {
    this.identifiers = identifiers;
    return this;
  }

  public ServerCliquesQuery addIdentifiersItem(String identifiersItem) {
    this.identifiers.add(identifiersItem);
    return this;
  }

   /**
   * 'identifiers' string parameter to call, echoed back 
   * @return identifiers
  **/
  @ApiModelProperty(value = "'identifiers' string parameter to call, echoed back ")
  public List<String> getIdentifiers() {
    return identifiers;
  }

  public void setIdentifiers(List<String> identifiers) {
    this.identifiers = identifiers;
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
        Objects.equals(this.identifiers, serverCliquesQuery.identifiers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, identifiers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerCliquesQuery {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    identifiers: ").append(toIndentedString(identifiers)).append("\n");
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

