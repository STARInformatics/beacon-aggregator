package bio.knowledge.server.model;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 * An edge in the thought subgraph linking two nodes
 */
@ApiModel(description = "An edge in the thought subgraph linking two nodes")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-12T21:13:13.403Z[GMT]")
public class Edge extends HashMap<String, Object> implements Serializable {
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
    sb.append("class Edge {\n");
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
