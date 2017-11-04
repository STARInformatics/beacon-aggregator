package bio.knowledge.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerConceptWithDetails
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-04T15:13:32.973-07:00")

public class ServerConceptWithDetails   {
  @JsonProperty("clique")
  private String clique = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("aliases")
  private List<String> aliases = new ArrayList<String>();

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("semanticGroup")
  private String semanticGroup = null;

  @JsonProperty("synonyms")
  private List<String> synonyms = new ArrayList<String>();

  @JsonProperty("definition")
  private String definition = null;

  @JsonProperty("details")
  private List<ServerConceptDetail> details = new ArrayList<ServerConceptDetail>();

  @JsonProperty("beacon")
  private String beacon = null;

  public ServerConceptWithDetails clique(String clique) {
    this.clique = clique;
    return this;
  }

   /**
   * CURIE identifying the equivalent concept clique to which the concept belongs. 
   * @return clique
  **/
  @ApiModelProperty(value = "CURIE identifying the equivalent concept clique to which the concept belongs. ")
  public String getClique() {
    return clique;
  }

  public void setClique(String clique) {
    this.clique = clique;
  }

  public ServerConceptWithDetails id(String id) {
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

  public ServerConceptWithDetails aliases(List<String> aliases) {
    this.aliases = aliases;
    return this;
  }

  public ServerConceptWithDetails addAliasesItem(String aliasesItem) {
    this.aliases.add(aliasesItem);
    return this;
  }

   /**
   * set of alias CURIES in the equivalent concept clique of the concept 
   * @return aliases
  **/
  @ApiModelProperty(value = "set of alias CURIES in the equivalent concept clique of the concept ")
  public List<String> getAliases() {
    return aliases;
  }

  public void setAliases(List<String> aliases) {
    this.aliases = aliases;
  }

  public ServerConceptWithDetails name(String name) {
    this.name = name;
    return this;
  }

   /**
   * canonical human readable name of the concept 
   * @return name
  **/
  @ApiModelProperty(value = "canonical human readable name of the concept ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServerConceptWithDetails semanticGroup(String semanticGroup) {
    this.semanticGroup = semanticGroup;
    return this;
  }

   /**
   * concept semantic type 
   * @return semanticGroup
  **/
  @ApiModelProperty(value = "concept semantic type ")
  public String getSemanticGroup() {
    return semanticGroup;
  }

  public void setSemanticGroup(String semanticGroup) {
    this.semanticGroup = semanticGroup;
  }

  public ServerConceptWithDetails synonyms(List<String> synonyms) {
    this.synonyms = synonyms;
    return this;
  }

  public ServerConceptWithDetails addSynonymsItem(String synonymsItem) {
    this.synonyms.add(synonymsItem);
    return this;
  }

   /**
   * list of synonyms for concept 
   * @return synonyms
  **/
  @ApiModelProperty(value = "list of synonyms for concept ")
  public List<String> getSynonyms() {
    return synonyms;
  }

  public void setSynonyms(List<String> synonyms) {
    this.synonyms = synonyms;
  }

  public ServerConceptWithDetails definition(String definition) {
    this.definition = definition;
    return this;
  }

   /**
   * concept definition 
   * @return definition
  **/
  @ApiModelProperty(value = "concept definition ")
  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public ServerConceptWithDetails details(List<ServerConceptDetail> details) {
    this.details = details;
    return this;
  }

  public ServerConceptWithDetails addDetailsItem(ServerConceptDetail detailsItem) {
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

  public ServerConceptWithDetails beacon(String beacon) {
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
    ServerConceptWithDetails serverConceptWithDetails = (ServerConceptWithDetails) o;
    return Objects.equals(this.clique, serverConceptWithDetails.clique) &&
        Objects.equals(this.id, serverConceptWithDetails.id) &&
        Objects.equals(this.aliases, serverConceptWithDetails.aliases) &&
        Objects.equals(this.name, serverConceptWithDetails.name) &&
        Objects.equals(this.semanticGroup, serverConceptWithDetails.semanticGroup) &&
        Objects.equals(this.synonyms, serverConceptWithDetails.synonyms) &&
        Objects.equals(this.definition, serverConceptWithDetails.definition) &&
        Objects.equals(this.details, serverConceptWithDetails.details) &&
        Objects.equals(this.beacon, serverConceptWithDetails.beacon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clique, id, aliases, name, semanticGroup, synonyms, definition, details, beacon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConceptWithDetails {\n");
    
    sb.append("    clique: ").append(toIndentedString(clique)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    aliases: ").append(toIndentedString(aliases)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    semanticGroup: ").append(toIndentedString(semanticGroup)).append("\n");
    sb.append("    synonyms: ").append(toIndentedString(synonyms)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

