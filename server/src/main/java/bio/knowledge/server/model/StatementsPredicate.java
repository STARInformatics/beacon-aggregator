package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * StatementsPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-19T15:08:40.849-07:00")

public class StatementsPredicate   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  public StatementsPredicate id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of predicate resource 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of predicate resource ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public StatementsPredicate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable label of concept
   * @return name
  **/
  @ApiModelProperty(value = "human readable label of concept")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StatementsPredicate statementsPredicate = (StatementsPredicate) o;
    return Objects.equals(this.id, statementsPredicate.id) &&
        Objects.equals(this.name, statementsPredicate.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StatementsPredicate {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

