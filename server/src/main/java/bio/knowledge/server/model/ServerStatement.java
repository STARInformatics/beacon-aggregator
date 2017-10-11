package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * Statement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-10T12:47:04.653-07:00")

public class ServerStatement   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("subject")
  private ServerSubject subject = null;

  @JsonProperty("predicate")
  private ServerPredicate predicate = null;

  @JsonProperty("object")
  private ServerObject object = null;

  @JsonProperty("beacon")
  private String beacon = null;

  public ServerStatement id(String id) {
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

  public ServerStatement subject(ServerSubject subject) {
    this.subject = subject;
    return this;
  }

   /**
   * Get subject
   * @return subject
  **/
  @ApiModelProperty(value = "")
  public ServerSubject getSubject() {
    return subject;
  }

  public void setSubject(ServerSubject subject) {
    this.subject = subject;
  }

  public ServerStatement predicate(ServerPredicate predicate) {
    this.predicate = predicate;
    return this;
  }

   /**
   * Get predicate
   * @return predicate
  **/
  @ApiModelProperty(value = "")
  public ServerPredicate getPredicate() {
    return predicate;
  }

  public void setPredicate(ServerPredicate predicate) {
    this.predicate = predicate;
  }

  public ServerStatement object(ServerObject object) {
    this.object = object;
    return this;
  }

   /**
   * Get object
   * @return object
  **/
  @ApiModelProperty(value = "")
  public ServerObject getObject() {
    return object;
  }

  public void setObject(ServerObject object) {
    this.object = object;
  }

  public ServerStatement beacon(String beacon) {
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
    ServerStatement statement = (ServerStatement) o;
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

