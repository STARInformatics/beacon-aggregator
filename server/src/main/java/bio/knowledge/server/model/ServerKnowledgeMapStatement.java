package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.ServerKnowledgeMapObject;
import bio.knowledge.server.model.ServerKnowledgeMapPredicate;
import bio.knowledge.server.model.ServerKnowledgeMapSubject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * ServerKnowledgeMapStatement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-28T14:42:53.737-07:00")

public class ServerKnowledgeMapStatement   {
  @JsonProperty("subject")
  private ServerKnowledgeMapSubject subject = null;

  @JsonProperty("predicate")
  private ServerKnowledgeMapPredicate predicate = null;

  @JsonProperty("object")
  private ServerKnowledgeMapObject object = null;

  @JsonProperty("frequency")
  private Integer frequency = null;

  @JsonProperty("description")
  private String description = null;

  public ServerKnowledgeMapStatement subject(ServerKnowledgeMapSubject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public ServerKnowledgeMapSubject getSubject() {
    return subject;
  }

  public void setSubject(ServerKnowledgeMapSubject subject) {
    this.subject = subject;
  }

  public ServerKnowledgeMapStatement predicate(ServerKnowledgeMapPredicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public ServerKnowledgeMapPredicate getPredicate() {
    return predicate;
  }

  public void setPredicate(ServerKnowledgeMapPredicate predicate) {
    this.predicate = predicate;
  }

  public ServerKnowledgeMapStatement object(ServerKnowledgeMapObject object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public ServerKnowledgeMapObject getObject() {
    return object;
  }

  public void setObject(ServerKnowledgeMapObject object) {
    this.object = object;
  }

  public ServerKnowledgeMapStatement frequency(Integer frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * the frequency of statements of the specified relationship within the given beacon 
   * @return frequency
  **/
  @ApiModelProperty(value = "the frequency of statements of the specified relationship within the given beacon ")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }

  public ServerKnowledgeMapStatement description(String description) {
    this.description = description;
    return this;
  }

   /**
   * a description of the nature of the relationship 
   * @return description
  **/
  @ApiModelProperty(value = "a description of the nature of the relationship ")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerKnowledgeMapStatement serverKnowledgeMapStatement = (ServerKnowledgeMapStatement) o;
    return Objects.equals(this.subject, serverKnowledgeMapStatement.subject) &&
        Objects.equals(this.predicate, serverKnowledgeMapStatement.predicate) &&
        Objects.equals(this.object, serverKnowledgeMapStatement.object) &&
        Objects.equals(this.frequency, serverKnowledgeMapStatement.frequency) &&
        Objects.equals(this.description, serverKnowledgeMapStatement.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, predicate, object, frequency, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerKnowledgeMapStatement {\n");
    
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    predicate: ").append(toIndentedString(predicate)).append("\n");
    sb.append("    object: ").append(toIndentedString(object)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

