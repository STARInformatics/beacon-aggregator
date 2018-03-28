package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerBeaconPredicate
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T22:57:55.565-07:00")

public class ServerBeaconPredicate   {
  @JsonProperty("beacon")
  private Integer beacon = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("iri")
  private String iri = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public ServerBeaconPredicate beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * aggregator index of the given beacon 
   * @return beacon
  **/
  @ApiModelProperty(value = "aggregator index of the given beacon ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
    this.beacon = beacon;
  }

  public ServerBeaconPredicate id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the 'local' CURIE-encoded identifier of the given predicate, as published by the given beacon 
   * @return id
  **/
  @ApiModelProperty(value = "the 'local' CURIE-encoded identifier of the given predicate, as published by the given beacon ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerBeaconPredicate iri(String iri) {
    this.iri = iri;
    return this;
  }

   /**
   * the 'local' CURIE-encoded identifier of the given predicate, as published by the given beacon 
   * @return iri
  **/
  @ApiModelProperty(value = "the 'local' CURIE-encoded identifier of the given predicate, as published by the given beacon ")
  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public ServerBeaconPredicate label(String label) {
    this.label = label;
    return this;
  }

   /**
   * the 'local' human readable of the given predicate, as published by the given beacon 
   * @return label
  **/
  @ApiModelProperty(value = "the 'local' human readable of the given predicate, as published by the given beacon ")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ServerBeaconPredicate frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of instances of the specified predicate relation is used in statements within the given beacon 
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of instances of the specified predicate relation is used in statements within the given beacon ")
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
    ServerBeaconPredicate serverBeaconPredicate = (ServerBeaconPredicate) o;
    return Objects.equals(this.beacon, serverBeaconPredicate.beacon) &&
        Objects.equals(this.id, serverBeaconPredicate.id) &&
        Objects.equals(this.iri, serverBeaconPredicate.iri) &&
        Objects.equals(this.label, serverBeaconPredicate.label) &&
        Objects.equals(this.frequency, serverBeaconPredicate.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beacon, id, iri, label, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerBeaconPredicate {\n");
    
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    iri: ").append(toIndentedString(iri)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
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

