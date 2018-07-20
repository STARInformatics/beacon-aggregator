package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerStatementCitation
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-11T17:59:49.447Z")

public class ServerStatementCitation   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("evidence_type")
  private String evidenceType = null;

  @JsonProperty("date")
  private String date = null;

  public ServerStatementCitation id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier to a citation to evidence supporting the given statement (e.g. PMID of a pubmed abstract) 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier to a citation to evidence supporting the given statement (e.g. PMID of a pubmed abstract) ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerStatementCitation uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * (optional) expansion of the citation CURIE 
   * @return uri
  **/
  @ApiModelProperty(value = "(optional) expansion of the citation CURIE ")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public ServerStatementCitation name(String name) {
    this.name = name;
    return this;
  }

   /**
   * canonical human readable and searchable name of the citation (i.e. publication title, comment, etc.) 
   * @return name
  **/
  @ApiModelProperty(value = "canonical human readable and searchable name of the citation (i.e. publication title, comment, etc.) ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServerStatementCitation evidenceType(String evidenceType) {
    this.evidenceType = evidenceType;
    return this;
  }

   /**
   * class of evidence supporting the statement made in an edge - typically a class from the ECO ontology (e.g. ECO:0000220 'sequencing assay evidence', see [Evidence Ontology](http://purl.obolibrary.org/obo/eco.owl) 
   * @return evidenceType
  **/
  @ApiModelProperty(value = "class of evidence supporting the statement made in an edge - typically a class from the ECO ontology (e.g. ECO:0000220 'sequencing assay evidence', see [Evidence Ontology](http://purl.obolibrary.org/obo/eco.owl) ")
  public String getEvidenceType() {
    return evidenceType;
  }

  public void setEvidenceType(String evidenceType) {
    this.evidenceType = evidenceType;
  }

  public ServerStatementCitation date(String date) {
    this.date = date;
    return this;
  }

   /**
   * publication date of annotation (generally of format 'yyyy-mm-dd') 
   * @return date
  **/
  @ApiModelProperty(value = "publication date of annotation (generally of format 'yyyy-mm-dd') ")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerStatementCitation serverStatementCitation = (ServerStatementCitation) o;
    return Objects.equals(this.id, serverStatementCitation.id) &&
        Objects.equals(this.uri, serverStatementCitation.uri) &&
        Objects.equals(this.name, serverStatementCitation.name) &&
        Objects.equals(this.evidenceType, serverStatementCitation.evidenceType) &&
        Objects.equals(this.date, serverStatementCitation.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uri, name, evidenceType, date);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerStatementCitation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    evidenceType: ").append(toIndentedString(evidenceType)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

