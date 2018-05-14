package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.ServerConceptDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * ServerConceptWithDetailsBeaconEntry
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T14:35:19.924-07:00")

public class ServerConceptWithDetailsBeaconEntry   {
  @JsonProperty("beacon")
  private Integer beacon = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("synonyms")
  private List<String> synonyms = new ArrayList<String>();

  @JsonProperty("definition")
  private String definition = null;

  @JsonProperty("details")
  private List<ServerConceptDetail> details = new ArrayList<ServerConceptDetail>();

  public ServerConceptWithDetailsBeaconEntry beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * Index number of beacon providing these concept details 
   * @return beacon
  **/
  @ApiModelProperty(value = "Index number of beacon providing these concept details ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
    this.beacon = beacon;
  }

  public ServerConceptWithDetailsBeaconEntry id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE identifying the specific beacon source concept being described. 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE identifying the specific beacon source concept being described. ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerConceptWithDetailsBeaconEntry synonyms(List<String> synonyms) {
    this.synonyms = synonyms;
    return this;
  }

  public ServerConceptWithDetailsBeaconEntry addSynonymsItem(String synonymsItem) {
    this.synonyms.add(synonymsItem);
    return this;
  }

   /**
   * List of synonymous names or identifiers for the concept 
   * @return synonyms
  **/
  @ApiModelProperty(value = "List of synonymous names or identifiers for the concept ")
  public List<String> getSynonyms() {
    return synonyms;
  }

  public void setSynonyms(List<String> synonyms) {
    this.synonyms = synonyms;
  }

  public ServerConceptWithDetailsBeaconEntry definition(String definition) {
    this.definition = definition;
    return this;
  }

   /**
   * Concept definition provided by a given beacon 
   * @return definition
  **/
  @ApiModelProperty(value = "Concept definition provided by a given beacon ")
  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public ServerConceptWithDetailsBeaconEntry details(List<ServerConceptDetail> details) {
    this.details = details;
    return this;
  }

  public ServerConceptWithDetailsBeaconEntry addDetailsItem(ServerConceptDetail detailsItem) {
    this.details.add(detailsItem);
    return this;
  }

   /**
   * Get details
   * @return details
  **/
  @ApiModelProperty(value = "")
  public List<ServerConceptDetail> getDetails() {
    return details;
  }

  public void setDetails(List<ServerConceptDetail> details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConceptWithDetailsBeaconEntry serverConceptWithDetailsBeaconEntry = (ServerConceptWithDetailsBeaconEntry) o;
    return Objects.equals(this.beacon, serverConceptWithDetailsBeaconEntry.beacon) &&
        Objects.equals(this.id, serverConceptWithDetailsBeaconEntry.id) &&
        Objects.equals(this.synonyms, serverConceptWithDetailsBeaconEntry.synonyms) &&
        Objects.equals(this.definition, serverConceptWithDetailsBeaconEntry.definition) &&
        Objects.equals(this.details, serverConceptWithDetailsBeaconEntry.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beacon, id, synonyms, definition, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptWithDetailsBeaconEntry {\n");
    
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    synonyms: ").append(toIndentedString(synonyms)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

