package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.OneOfEdgeBindingKgId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * EdgeBinding
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-20T20:17:56.260Z[GMT]")
public class EdgeBinding   {
  @JsonProperty("qg_id")
  private String qgId = null;

  @JsonProperty("kg_id")
  private OneOfEdgeBindingKgId kgId = null;

  public EdgeBinding qgId(String qgId) {
    this.qgId = qgId;
    return this;
  }

  /**
   * Query-graph edge id, i.e. the `edge_id` of a QEdge
   * @return qgId
  **/
  @ApiModelProperty(required = true, value = "Query-graph edge id, i.e. the `edge_id` of a QEdge")
  @NotNull

  public String getQgId() {
    return qgId;
  }

  public void setQgId(String qgId) {
    this.qgId = qgId;
  }

  public EdgeBinding kgId(OneOfEdgeBindingKgId kgId) {
    this.kgId = kgId;
    return this;
  }

  /**
   * One or more knowledge-graph edge ids, i.e. the `id` of a KEdge
   * @return kgId
  **/
  @ApiModelProperty(required = true, value = "One or more knowledge-graph edge ids, i.e. the `id` of a KEdge")
  @NotNull

  public OneOfEdgeBindingKgId getKgId() {
    return kgId;
  }

  public void setKgId(OneOfEdgeBindingKgId kgId) {
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
    EdgeBinding edgeBinding = (EdgeBinding) o;
    return Objects.equals(this.qgId, edgeBinding.qgId) &&
        Objects.equals(this.kgId, edgeBinding.kgId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qgId, kgId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EdgeBinding {\n");
    
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
