package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * Detail
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-10T12:47:04.653-07:00")

public class ServerDetail   {
  @JsonProperty("tag")
  private String tag = null;

  @JsonProperty("value")
  private String value = null;

  public ServerDetail tag(String tag) {
    this.tag = tag;
    return this;
  }

   /**
   * property name 
   * @return tag
  **/
  @ApiModelProperty(value = "property name ")
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public ServerDetail value(String value) {
    this.value = value;
    return this;
  }

   /**
   * property value 
   * @return value
  **/
  @ApiModelProperty(value = "property value ")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerDetail detail = (ServerDetail) o;
    return Objects.equals(this.tag, detail.tag) &&
        Objects.equals(this.value, detail.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tag, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Detail {\n");
    
    sb.append("    tag: ").append(toIndentedString(tag)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

