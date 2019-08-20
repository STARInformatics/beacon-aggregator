package bio.knowledge.server.model;

import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 * A graph intended to be the thought path to be followed by a reasoner to answer the question. This graph is a representation of a question.
 */
@ApiModel(description = "A graph intended to be the thought path to be followed by a reasoner to answer the question. This graph is a representation of a question.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-12T21:13:13.403Z[GMT]")
public class QueryGraph extends HashMap<String, Object> implements Serializable {
  private static final long serialVersionUID = 1L;


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryGraph {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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
