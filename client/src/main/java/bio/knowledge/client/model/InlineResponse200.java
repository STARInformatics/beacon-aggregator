/*
 * Translator Knowledge Beacon API
 * This is the Translator Knowledge Beacon web service application programming interface (API).  This OpenAPI (\"Swagger\") document may be used as the input specification into a tool like [Swagger-Codegen](https://github.com/swagger-api/swagger-codegen/blob/master/README.md) to generate client and server code stubs implementing the API, in any one of several supported computer languages and frameworks. In order to customize usage to your web site, you should change the 'host' directive below to your hostname. 
 *
 * OpenAPI spec version: 1.0.11
 * Contact: richard@starinformatics.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package bio.knowledge.client.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModelProperty;

/**
 * InlineResponse200
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-05-19T15:08:41.571-07:00")
public class InlineResponse200 {
  @SerializedName("id")
  private String id = null;

  @SerializedName("idmap")
  private String idmap = null;

  @SerializedName("frequency")
  private Integer frequency = null;

  public InlineResponse200 id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the type  
   * @return id
  **/
  @ApiModelProperty(example = "null", value = "the type  ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public InlineResponse200 idmap(String idmap) {
    this.idmap = idmap;
    return this;
  }

   /**
   * The URL to execute the exactmatches API call on the id
   * @return idmap
  **/
  @ApiModelProperty(example = "null", value = "The URL to execute the exactmatches API call on the id")
  public String getIdmap() {
    return idmap;
  }

  public void setIdmap(String idmap) {
    this.idmap = idmap;
  }

  public InlineResponse200 frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of instances of the specified type 
   * @return frequency
  **/
  @ApiModelProperty(example = "null", value = "the number of instances of the specified type ")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse200 inlineResponse200 = (InlineResponse200) o;
    return Objects.equals(this.id, inlineResponse200.id) &&
        Objects.equals(this.idmap, inlineResponse200.idmap) &&
        Objects.equals(this.frequency, inlineResponse200.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, idmap, frequency);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse200 {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    idmap: ").append(toIndentedString(idmap)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
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

