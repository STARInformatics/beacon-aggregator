package bio.knowledge.server.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Local predicate definition from a given beacon 
 */
@ApiModel(description = "Local predicate definition from a given beacon ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T15:02:51.082-07:00")

public class ServerBeaconPredicate   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("relation")
  private String relation = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  public ServerBeaconPredicate id(String id) {
    this.id = id;
    return this;
  }

   /**
   * the 'local' CURIE-encoded identifier of a maximal predicate relation, as published by  the given beacon 
   * @return id
  **/
  @ApiModelProperty(value = "the 'local' CURIE-encoded identifier of a maximal predicate relation, as published by  the given beacon ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerBeaconPredicate uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * the 'local' URI of a maximal predicate relation,  as published by the given beacon 
   * @return uri
  **/
  @ApiModelProperty(value = "the 'local' URI of a maximal predicate relation,  as published by the given beacon ")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public ServerBeaconPredicate relation(String relation) {
    this.relation = relation;
    return this;
  }

   /**
   * the human readable 'relation of the 'maximal' predicate (see the [Biolink Model](https://biolink.github.io/biolink-model) for a list of Biolink maximal predicates; this field may map onto beacon-specific non-Biolink relations 
   * @return relation
  **/
  @ApiModelProperty(value = "the human readable 'relation of the 'maximal' predicate (see the [Biolink Model](https://biolink.github.io/biolink-model) for a list of Biolink maximal predicates; this field may map onto beacon-specific non-Biolink relations ")
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public ServerBeaconPredicate description(String description) {
    this.description = description;
    return this;
  }

   /**
   * human readable definition of the given  predicate relation 
   * @return description
  **/
  @ApiModelProperty(value = "human readable definition of the given  predicate relation ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
    return Objects.equals(this.id, serverBeaconPredicate.id) &&
        Objects.equals(this.uri, serverBeaconPredicate.uri) &&
        Objects.equals(this.relation, serverBeaconPredicate.relation) &&
        Objects.equals(this.description, serverBeaconPredicate.description) &&
        Objects.equals(this.frequency, serverBeaconPredicate.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, relation, description, frequency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerBeaconPredicate {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

