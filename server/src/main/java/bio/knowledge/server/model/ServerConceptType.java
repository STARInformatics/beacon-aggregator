package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerConceptType
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-24T08:51:45.143-08:00")

public class ServerConceptType   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("idmap")
  private String idmap = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  @JsonProperty("beacon")
  private String beacon = null;

  public ServerConceptType id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) 
   * @return id
  **/
  @ApiModelProperty(value = "the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerConceptType idmap(String idmap) {
    this.idmap = idmap;
    return this;
  }

   /**
   * the IRI of the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes)
   * @return idmap
  **/
  @ApiModelProperty(value = "the IRI of the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes)")
  public String getIdmap() {
    return idmap;
  }

  public void setIdmap(String idmap) {
    this.idmap = idmap;
  }

  public ServerConceptType frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of instances of the specified type 
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of instances of the specified type ")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }

  public ServerConceptType beacon(String beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * beacon ID 
   * @return beacon
  **/
  @ApiModelProperty(value = "beacon ID ")
  public String getBeacon() {
    return beacon;
  }

  public void setBeacon(String beacon) {
    this.beacon = beacon;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConceptType serverConceptType = (ServerConceptType) o;
    return Objects.equals(this.id, serverConceptType.id) &&
        Objects.equals(this.idmap, serverConceptType.idmap) &&
        Objects.equals(this.frequency, serverConceptType.frequency) &&
        Objects.equals(this.beacon, serverConceptType.beacon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, idmap, frequency, beacon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptType {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    idmap: ").append(toIndentedString(idmap)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
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

