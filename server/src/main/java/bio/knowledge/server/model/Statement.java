package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.Predicate;
import bio.knowledge.server.model.Subject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Statement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-10T12:47:04.653-07:00")

public class Statement   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("subject")
  private Subject subject = null;

  @JsonProperty("predicate")
  private Predicate predicate = null;

  @JsonProperty("object")
  private Object object = null;

  @JsonProperty("beacon")
  private String beacon = null;

  public Statement id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier for statement (can be used to retrieve associated evidence)
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier for statement (can be used to retrieve associated evidence)")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Statement subject(Subject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
  }

  public Statement predicate(Predicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public Predicate getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }

  public Statement object(Object object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public Object getObject() {
    return object;
  }

  public void setObject(Object object) {
    this.object = object;
  }

  public Statement beacon(String beacon) {
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
    Statement statement = (Statement) o;
    return Objects.equals(this.id, statement.id) &&
        Objects.equals(this.subject, statement.subject) &&
        Objects.equals(this.predicate, statement.predicate) &&
        Objects.equals(this.object, statement.object) &&
        Objects.equals(this.beacon, statement.beacon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, subject, predicate, object, beacon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Statement {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    predicate: ").append(toIndentedString(predicate)).append("\n");
    sb.append("    object: ").append(toIndentedString(object)).append("\n");
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

