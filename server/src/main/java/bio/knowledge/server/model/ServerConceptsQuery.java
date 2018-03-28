package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerConceptsQuery
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T22:57:55.565-07:00")

public class ServerConceptsQuery   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("keywords")
  private String keywords = null;

  @JsonProperty("types")
  private String types = null;

  public ServerConceptsQuery queryId(String queryId) {
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

  public ServerConceptsQuery keywords(String keywords) {
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

  public ServerConceptsQuery types(String types) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConceptsQuery serverConceptsQuery = (ServerConceptsQuery) o;
    return Objects.equals(this.queryId, serverConceptsQuery.queryId) &&
        Objects.equals(this.keywords, serverConceptsQuery.keywords) &&
        Objects.equals(this.types, serverConceptsQuery.types);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, keywords, types);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptsQuery {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
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

