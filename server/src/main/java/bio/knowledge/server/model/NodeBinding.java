package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.OneOfNodeBindingKgId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NodeBinding
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-20T20:17:56.260Z[GMT]")
public class NodeBinding   {
  @JsonProperty("qg_id")
  private String qgId = null;

  @JsonProperty("kg_id")
  private OneOfNodeBindingKgId kgId = null;

  public NodeBinding qgId(String qgId) {
    this.qgId = qgId;
    return this;
  }

  /**
   * Query-graph node id, i.e. the `node_id` of a QNode
   * @return qgId
  **/
  @ApiModelProperty(required = true, value = "Query-graph node id, i.e. the `node_id` of a QNode")
  @NotNull

  public String getQgId() {
    return qgId;
  }

  public void setQgId(String qgId) {
    this.qgId = qgId;
  }

  public NodeBinding kgId(OneOfNodeBindingKgId kgId) {
    this.kgId = kgId;
    return this;
  }

  /**
   * One or more knowledge-graph node ids, i.e. the `id` of a KNode
   * @return kgId
  **/
  @ApiModelProperty(required = true, value = "One or more knowledge-graph node ids, i.e. the `id` of a KNode")
  @NotNull

  public OneOfNodeBindingKgId getKgId() {
    return kgId;
  }

  public void setKgId(OneOfNodeBindingKgId kgId) {
    this.kgId = kgId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeBinding nodeBinding = (NodeBinding) o;
    return Objects.equals(this.qgId, nodeBinding.qgId) &&
        Objects.equals(this.kgId, nodeBinding.kgId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qgId, kgId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NodeBinding {\n");
    
    sb.append("    qgId: ").append(toIndentedString(qgId)).append("\n");
    sb.append("    kgId: ").append(toIndentedString(kgId)).append("\n");
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
