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
 * ServerStatementsQuery
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T15:45:49.260-07:00")

public class ServerStatementsQuery   {
  @JsonProperty("queryId")
  private String queryId = null;

  @JsonProperty("source")
  private String source = null;

  @JsonProperty("relations")
  private List<String> relations = new ArrayList<String>();

  @JsonProperty("target")
  private String target = null;

  @JsonProperty("keywords")
  private String keywords = null;

  @JsonProperty("categories")
  private List<String> categories = new ArrayList<String>();

  public ServerStatementsQuery queryId(String queryId) {
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

  public ServerStatementsQuery source(String source) {
    this.source = source;
    return this;
  }

   /**
   * 'source' string parameter to call, echoed back 
   * @return source
  **/
  @ApiModelProperty(value = "'source' string parameter to call, echoed back ")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public ServerStatementsQuery relations(List<String> relations) {
    this.relations = relations;
    return this;
  }

  public ServerStatementsQuery addRelationsItem(String relationsItem) {
    this.relations.add(relationsItem);
    return this;
  }

   /**
   * 'relations' string parameter to call, echoed back 
   * @return relations
  **/
  @ApiModelProperty(value = "'relations' string parameter to call, echoed back ")
  public List<String> getRelations() {
    return relations;
  }

  public void setRelations(List<String> relations) {
    this.relations = relations;
  }

  public ServerStatementsQuery target(String target) {
    this.target = target;
    return this;
  }

   /**
   * 'target' string parameter to call, echoed back 
   * @return target
  **/
  @ApiModelProperty(value = "'target' string parameter to call, echoed back ")
  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public ServerStatementsQuery keywords(String keywords) {
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

  public ServerStatementsQuery categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  public ServerStatementsQuery addCategoriesItem(String categoriesItem) {
    this.categories.add(categoriesItem);
    return this;
  }

   /**
   * 'categories' string parameter to call, echoed back 
   * @return categories
  **/
  @ApiModelProperty(value = "'categories' string parameter to call, echoed back ")
  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerStatementsQuery serverStatementsQuery = (ServerStatementsQuery) o;
    return Objects.equals(this.queryId, serverStatementsQuery.queryId) &&
        Objects.equals(this.source, serverStatementsQuery.source) &&
        Objects.equals(this.relations, serverStatementsQuery.relations) &&
        Objects.equals(this.target, serverStatementsQuery.target) &&
        Objects.equals(this.keywords, serverStatementsQuery.keywords) &&
        Objects.equals(this.categories, serverStatementsQuery.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryId, source, relations, target, keywords, categories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerStatementsQuery {\n");
    
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    relations: ").append(toIndentedString(relations)).append("\n");
    sb.append("    target: ").append(toIndentedString(target)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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

