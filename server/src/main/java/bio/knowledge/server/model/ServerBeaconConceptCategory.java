package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Single local concept categories from a given beacon 
 */
@ApiModel(description = "Single local concept categories from a given beacon ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T15:45:49.260-07:00")

public class ServerBeaconConceptCategory   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("iri")
  private String iri = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public ServerBeaconConceptCategory id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the 'local' CURIE-encoded identifier of the given concept category, as published by the given beacon 
   * @return id
  **/
  @ApiModelProperty(value = "the 'local' CURIE-encoded identifier of the given concept category, as published by the given beacon ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerBeaconConceptCategory iri(String iri) {
    this.iri = iri;
    return this;
  }

   /**
   * the 'local' IRI of the given concept category,  as published by the given beacon 
   * @return iri
  **/
  @ApiModelProperty(value = "the 'local' IRI of the given concept category,  as published by the given beacon ")
  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public ServerBeaconConceptCategory label(String label) {
    this.label = label;
    return this;
  }

   /**
   * the 'local' human readable of the given concept category, as published by the given beacon 
   * @return label
  **/
  @ApiModelProperty(value = "the 'local' human readable of the given concept category, as published by the given beacon ")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ServerBeaconConceptCategory frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the number of instances of the specified concept category is used in statements within the given beacon 
   * @return frequency
  **/
  @ApiModelProperty(value = "the number of instances of the specified concept category is used in statements within the given beacon ")
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
    ServerBeaconConceptCategory serverBeaconConceptCategory = (ServerBeaconConceptCategory) o;
    return Objects.equals(this.id, serverBeaconConceptCategory.id) &&
        Objects.equals(this.iri, serverBeaconConceptCategory.iri) &&
        Objects.equals(this.label, serverBeaconConceptCategory.label) &&
        Objects.equals(this.frequency, serverBeaconConceptCategory.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, iri, label, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerBeaconConceptCategory {\n");
    
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

