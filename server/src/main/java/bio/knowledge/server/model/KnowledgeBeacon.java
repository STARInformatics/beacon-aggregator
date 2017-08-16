package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * KnowledgeBeacon
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-15T11:46:37.748-07:00")

public class KnowledgeBeacon   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("contact")
  private String contact = null;

  @JsonProperty("wraps")
  private String wraps = null;

  @JsonProperty("repo")
  private String repo = null;

  public KnowledgeBeacon id(String id) {
    this.id = id;
    return this;
  }

   /**
   * beacon ID 
   * @return id
  **/
  @ApiModelProperty(value = "beacon ID ")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public KnowledgeBeacon name(String name) {
    this.name = name;
    return this;
  }

   /**
   * beacon name 
   * @return name
  **/
  @ApiModelProperty(value = "beacon name ")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public KnowledgeBeacon url(String url) {
    this.url = url;
    return this;
  }

   /**
   * URL used to execute API calls on the beacon 
   * @return url
  **/
  @ApiModelProperty(value = "URL used to execute API calls on the beacon ")


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public KnowledgeBeacon description(String description) {
    this.description = description;
    return this;
  }

   /**
   * beacon description 
   * @return description
  **/
  @ApiModelProperty(value = "beacon description ")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public KnowledgeBeacon contact(String contact) {
    this.contact = contact;
    return this;
  }

   /**
   * name of the person responsible for this beacon 
   * @return contact
  **/
  @ApiModelProperty(value = "name of the person responsible for this beacon ")


  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public KnowledgeBeacon wraps(String wraps) {
    this.wraps = wraps;
    return this;
  }

   /**
   * URL of this beacon's data source 
   * @return wraps
  **/
  @ApiModelProperty(value = "URL of this beacon's data source ")


  public String getWraps() {
    return wraps;
  }

  public void setWraps(String wraps) {
    this.wraps = wraps;
  }

  public KnowledgeBeacon repo(String repo) {
    this.repo = repo;
    return this;
  }

   /**
   * URL of this beacon's repository 
   * @return repo
  **/
  @ApiModelProperty(value = "URL of this beacon's repository ")


  public String getRepo() {
    return repo;
  }

  public void setRepo(String repo) {
    this.repo = repo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KnowledgeBeacon knowledgeBeacon = (KnowledgeBeacon) o;
    return Objects.equals(this.id, knowledgeBeacon.id) &&
        Objects.equals(this.name, knowledgeBeacon.name) &&
        Objects.equals(this.url, knowledgeBeacon.url) &&
        Objects.equals(this.description, knowledgeBeacon.description) &&
        Objects.equals(this.contact, knowledgeBeacon.contact) &&
        Objects.equals(this.wraps, knowledgeBeacon.wraps) &&
        Objects.equals(this.repo, knowledgeBeacon.repo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, url, description, contact, wraps, repo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KnowledgeBeacon {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    contact: ").append(toIndentedString(contact)).append("\n");
    sb.append("    wraps: ").append(toIndentedString(wraps)).append("\n");
    sb.append("    repo: ").append(toIndentedString(repo)).append("\n");
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

