package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Object
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-11T17:55:45.568-07:00")

public class Object   {
  @JsonProperty("clique")
  private String clique = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("semgroup")
  private String semgroup = null;

  public Object clique(String clique) {
    this.clique = clique;
    return this;
  }

   /**
   * CURIE-encoded cannonical identifier of \"equivalent concepts clique\" 
   * @return clique
  **/
  @ApiModelProperty(value = "CURIE-encoded cannonical identifier of \"equivalent concepts clique\" ")
  public String getClique() {
    return clique;
  }

  public void setClique(String clique) {
    this.clique = clique;
  }

  public Object id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier of object concept 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier of object concept ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Object name(String name) {
    this.name = name;
    return this;
  }

   /**
   * human readable label of object concept
   * @return name
  **/
  @ApiModelProperty(value = "human readable label of object concept")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object semgroup(String semgroup) {
    this.semgroup = semgroup;
    return this;
  }

   /**
   * a semantic group for the subject concept (specified as a code CHEM, GENE, etc. - see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) 
   * @return semgroup
  **/
  @ApiModelProperty(value = "a semantic group for the subject concept (specified as a code CHEM, GENE, etc. - see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ")
  public String getSemgroup() {
    return semgroup;
  }

  public void setSemgroup(String semgroup) {
    this.semgroup = semgroup;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Object object = (Object) o;
    return Objects.equals(this.clique, object.clique) &&
        Objects.equals(this.id, object.id) &&
        Objects.equals(this.name, object.name) &&
        Objects.equals(this.semgroup, object.semgroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clique, id, name, semgroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Object {\n");
    
    sb.append("    clique: ").append(toIndentedString(clique)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    semgroup: ").append(toIndentedString(semgroup)).append("\n");
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

