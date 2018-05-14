package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.ServerConceptCategoriesByBeacon;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * ServerConceptCategories
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T14:35:19.924-07:00")

public class ServerConceptCategories   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("iri")
  private String iri = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("beacons")
  private List<ServerConceptCategoriesByBeacon> beacons = new ArrayList<ServerConceptCategoriesByBeacon>();

  public ServerConceptCategories id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the CURIE of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)
   * @return id
  **/
  @ApiModelProperty(value = "the CURIE of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerConceptCategories iri(String iri) {
    this.iri = iri;
    return this;
  }

   /**
   * the IRI of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)  for the full list of IRI)
   * @return iri
  **/
  @ApiModelProperty(value = "the IRI of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model)  for the full list of IRI)")
  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public ServerConceptCategories label(String label) {
    this.label = label;
    return this;
  }

   /**
   * the human readable label of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model) for  the full list of concept type names) 
   * @return label
  **/
  @ApiModelProperty(value = "the human readable label of the concept category (see [Biolink Model Classes](https://biolink.github.io/biolink-model) for  the full list of concept type names) ")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ServerConceptCategories description(String description) {
    this.description = description;
    return this;
  }

   /**
   * human readable definition assigned by the beacon for the specified concept category 
   * @return description
  **/
  @ApiModelProperty(value = "human readable definition assigned by the beacon for the specified concept category ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ServerConceptCategories beacons(List<ServerConceptCategoriesByBeacon> beacons) {
    this.beacons = beacons;
    return this;
  }

  public ServerConceptCategories addBeaconsItem(ServerConceptCategoriesByBeacon beaconsItem) {
    this.beacons.add(beaconsItem);
    return this;
  }

   /**
   * list of metadata for beacons that support the use of this concept category 
   * @return beacons
  **/
  @ApiModelProperty(value = "list of metadata for beacons that support the use of this concept category ")
  public List<ServerConceptCategoriesByBeacon> getBeacons() {
    return beacons;
  }

  public void setBeacons(List<ServerConceptCategoriesByBeacon> beacons) {
    this.beacons = beacons;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConceptCategories serverConceptCategories = (ServerConceptCategories) o;
    return Objects.equals(this.id, serverConceptCategories.id) &&
        Objects.equals(this.iri, serverConceptCategories.iri) &&
        Objects.equals(this.label, serverConceptCategories.label) &&
        Objects.equals(this.description, serverConceptCategories.description) &&
        Objects.equals(this.beacons, serverConceptCategories.beacons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, iri, label, description, beacons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptCategories {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    iri: ").append(toIndentedString(iri)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    beacons: ").append(toIndentedString(beacons)).append("\n");
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

