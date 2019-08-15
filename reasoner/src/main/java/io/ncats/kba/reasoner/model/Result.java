package io.ncats.kba.reasoner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * One of potentially several results or answers for a query
 */
@ApiModel(description = "One of potentially several results or answers for a query")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-12T21:13:13.403Z[GMT]")
public class Result  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("node_bindings")
  @Valid
  private List<NodeBinding> nodeBindings = null;

  @JsonProperty("edge_bindings")
  @Valid
  private List<EdgeBinding> edgeBindings = null;

  public Result nodeBindings(List<NodeBinding> nodeBindings) {
    this.nodeBindings = nodeBindings;
    return this;
  }

  public Result addNodeBindingsItem(NodeBinding nodeBindingsItem) {
    if (this.nodeBindings == null) {
      this.nodeBindings = new ArrayList<NodeBinding>();
    }
    this.nodeBindings.add(nodeBindingsItem);
    return this;
  }

  /**
   * List of QNode-KNode bindings.
   * @return nodeBindings
  **/
  @ApiModelProperty(value = "List of QNode-KNode bindings.")
  @Valid
  public List<NodeBinding> getNodeBindings() {
    return nodeBindings;
  }

  public void setNodeBindings(List<NodeBinding> nodeBindings) {
    this.nodeBindings = nodeBindings;
  }

  public Result edgeBindings(List<EdgeBinding> edgeBindings) {
    this.edgeBindings = edgeBindings;
    return this;
  }

  public Result addEdgeBindingsItem(EdgeBinding edgeBindingsItem) {
    if (this.edgeBindings == null) {
      this.edgeBindings = new ArrayList<EdgeBinding>();
    }
    this.edgeBindings.add(edgeBindingsItem);
    return this;
  }

  /**
   * List of QEdge-KEdge bindings.
   * @return edgeBindings
  **/
  @ApiModelProperty(value = "List of QEdge-KEdge bindings.")
  @Valid
  public List<EdgeBinding> getEdgeBindings() {
    return edgeBindings;
  }

  public void setEdgeBindings(List<EdgeBinding> edgeBindings) {
    this.edgeBindings = edgeBindings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Result result = (Result) o;
    return Objects.equals(this.nodeBindings, result.nodeBindings) &&
        Objects.equals(this.edgeBindings, result.edgeBindings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodeBindings, edgeBindings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Result {\n");
    
    sb.append("    nodeBindings: ").append(toIndentedString(nodeBindings)).append("\n");
    sb.append("    edgeBindings: ").append(toIndentedString(edgeBindings)).append("\n");
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
