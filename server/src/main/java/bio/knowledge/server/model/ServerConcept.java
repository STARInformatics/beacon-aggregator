package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * ServerConcept
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-18T19:51:31.275-08:00")

public class ServerConcept   {
  @JsonProperty("clique")
  private String clique = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("aliases")
  private List<String> aliases = new ArrayList<String>();

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("synonyms")
  private List<String> synonyms = new ArrayList<String>();

  @JsonProperty("definition")
  private String definition = null;

  @JsonProperty("beacon")
  private String beacon = null;

  public ServerConcept clique(String clique) {
    this.clique = clique;
    return this;
  }

   /**
   * CURIE identifying the inferred equivalent concept clique to which the concept belongs. This is assigned by an identifier precedence heuristic by the beacon-aggregator 
   * @return clique
  **/
  @ApiModelProperty(value = "CURIE identifying the inferred equivalent concept clique to which the concept belongs. This is assigned by an identifier precedence heuristic by the beacon-aggregator ")
  public String getClique() {
    return clique;
  }

  public void setClique(String clique) {
    this.clique = clique;
  }

  public ServerConcept id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE for the concept in the specified knowledge beacon 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE for the concept in the specified knowledge beacon ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerConcept aliases(List<String> aliases) {
    this.aliases = aliases;
    return this;
  }

  public ServerConcept addAliasesItem(String aliasesItem) {
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

  public ServerConcept name(String name) {
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

  public ServerConcept type(String type) {
    this.type = type;
    return this;
  }

   /**
   * concept semantic type 
   * @return type
  **/
  @ApiModelProperty(value = "concept semantic type ")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ServerConcept synonyms(List<String> synonyms) {
    this.synonyms = synonyms;
    return this;
  }

  public ServerConcept addSynonymsItem(String synonymsItem) {
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

  public ServerConcept definition(String definition) {
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

  public ServerConcept beacon(String beacon) {
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
    ServerConcept serverConcept = (ServerConcept) o;
    return Objects.equals(this.clique, serverConcept.clique) &&
        Objects.equals(this.id, serverConcept.id) &&
        Objects.equals(this.aliases, serverConcept.aliases) &&
        Objects.equals(this.name, serverConcept.name) &&
        Objects.equals(this.type, serverConcept.type) &&
        Objects.equals(this.synonyms, serverConcept.synonyms) &&
        Objects.equals(this.definition, serverConcept.definition) &&
        Objects.equals(this.beacon, serverConcept.beacon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clique, id, aliases, name, type, synonyms, definition, beacon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerConcept {\n");
    
    sb.append("    clique: ").append(toIndentedString(clique)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    aliases: ").append(toIndentedString(aliases)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    synonyms: ").append(toIndentedString(synonyms)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
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

