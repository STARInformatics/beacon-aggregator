package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.ServerBeaconConceptType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * ServerConceptType
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-24T18:37:28.321-08:00")

public class ServerConceptType   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("iri")
  private String iri = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("beacons")
  private List<ServerBeaconConceptType> beacons = new ArrayList<ServerBeaconConceptType>();

  public ServerConceptType name(String name) {
    this.name = name;
    return this;
  }

   /**
   * the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of concept type names) 
   * @return name
  **/
  @ApiModelProperty(value = "the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of concept type names) ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServerConceptType iri(String iri) {
    this.iri = iri;
    return this;
  }

   /**
   * the IRI of the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of IRI )
   * @return iri
  **/
  @ApiModelProperty(value = "the IRI of the concept type (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of IRI )")
  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public ServerConceptType description(String description) {
    this.description = description;
    return this;
  }

   /**
   * human readable definition assigned by the beacon for the specified concept type 
   * @return description
  **/
  @ApiModelProperty(value = "human readable definition assigned by the beacon for the specified concept type ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ServerConceptType beacons(List<ServerBeaconConceptType> beacons) {
    this.beacons = beacons;
    return this;
  }

  public ServerConceptType addBeaconsItem(ServerBeaconConceptType beaconsItem) {
    this.beacons.add(beaconsItem);
    return this;
  }

   /**
   * list of metadata for beacons that support the use of this concept type 
   * @return beacons
  **/
  @ApiModelProperty(value = "list of metadata for beacons that support the use of this concept type ")
  public List<ServerBeaconConceptType> getBeacons() {
    return beacons;
  }

  public void setBeacons(List<ServerBeaconConceptType> beacons) {
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
    ServerConceptType serverConceptType = (ServerConceptType) o;
    return Objects.equals(this.name, serverConceptType.name) &&
        Objects.equals(this.iri, serverConceptType.iri) &&
        Objects.equals(this.description, serverConceptType.description) &&
        Objects.equals(this.beacons, serverConceptType.beacons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, iri, description, beacons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptType {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    iri: ").append(toIndentedString(iri)).append("\n");
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

