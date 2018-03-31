package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * CliqueIdentifier
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-06T00:02:30.369-08:00")

public class CliqueIdentifier   {
  @JsonProperty("cliqueId")
  private String cliqueId = null;

  public CliqueIdentifier cliqueId(String cliqueId) {
    this.cliqueId = cliqueId;
    return this;
  }

   /**
   * CURIE identifying the equivalent concept clique to which the  input concept CURIE belongs. 
   * @return cliqueId
  **/
  @ApiModelProperty(value = "CURIE identifying the equivalent concept clique to which the  input concept CURIE belongs. ")
  public String getCliqueId() {
    return cliqueId;
  }

  public void setCliqueId(String cliqueId) {
    this.cliqueId = cliqueId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CliqueIdentifier cliqueIdentifier = (CliqueIdentifier) o;
    return Objects.equals(this.cliqueId, cliqueIdentifier.cliqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cliqueId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CliqueIdentifier {\n");
    
    sb.append("    cliqueId: ").append(toIndentedString(cliqueId)).append("\n");
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
