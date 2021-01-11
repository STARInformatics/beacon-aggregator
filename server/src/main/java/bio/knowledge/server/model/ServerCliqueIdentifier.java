package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerCliqueIdentifier
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-01T20:11:14.227Z")

public class ServerCliqueIdentifier   {
  @JsonProperty("inputId")
  private String inputId = null;

  @JsonProperty("cliqueId")
  private String cliqueId = null;

  public ServerCliqueIdentifier inputId(String inputId) {
    this.inputId = inputId;
    return this;
  }

   /**
   * CURIE identifying the concept whose concept clique is being searched for 
   * @return inputId
  **/
  @ApiModelProperty(value = "CURIE identifying the concept whose concept clique is being searched for ")
  public String getInputId() {
    return inputId;
  }

  public void setInputId(String inputId) {
    this.inputId = inputId;
  }

  public ServerCliqueIdentifier cliqueId(String cliqueId) {
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
    ServerCliqueIdentifier serverCliqueIdentifier = (ServerCliqueIdentifier) o;
    return Objects.equals(this.inputId, serverCliqueIdentifier.inputId) &&
        Objects.equals(this.cliqueId, serverCliqueIdentifier.cliqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inputId, cliqueId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerCliqueIdentifier {\n");
    
    sb.append("    inputId: ").append(toIndentedString(inputId)).append("\n");
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

