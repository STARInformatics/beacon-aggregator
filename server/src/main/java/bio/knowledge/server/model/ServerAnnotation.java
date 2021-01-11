package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
/**
 * ServerAnnotation
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T15:02:51.082-07:00")

public class ServerAnnotation   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("evidence_code")
  private String evidenceCode = null;

  @JsonProperty("date")
  private String date = null;

  @JsonProperty("beacon")
  private Integer beacon = null;

  public ServerAnnotation id(String id) {
    this.id = id;
    return this;
  }

   /**
   * CURIE-encoded identifier to an associated external resources (e.g. PMID of a pubmed citation) 
   * @return id
  **/
  @ApiModelProperty(value = "CURIE-encoded identifier to an associated external resources (e.g. PMID of a pubmed citation) ")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ServerAnnotation label(String label) {
    this.label = label;
    return this;
  }

   /**
   * canonical human readable and searchable label of the annotation (i.e. comment, matched sentence, etc.) 
   * @return label
  **/
  @ApiModelProperty(value = "canonical human readable and searchable label of the annotation (i.e. comment, matched sentence, etc.) ")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ServerAnnotation evidenceCode(String evidenceCode) {
    this.evidenceCode = evidenceCode;
    return this;
  }

   /**
   * Gene Ontology Evidence Code (http://www.geneontology.org/page/guide-go-evidence-codes) 
   * @return evidenceCode
  **/
  @ApiModelProperty(value = "Gene Ontology Evidence Code (http://www.geneontology.org/page/guide-go-evidence-codes) ")
  public String getEvidenceCode() {
    return evidenceCode;
  }

  public void setEvidenceCode(String evidenceCode) {
    this.evidenceCode = evidenceCode;
  }

  public ServerAnnotation date(String date) {
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

  public ServerAnnotation beacon(Integer beacon) {
    this.beacon = beacon;
    return this;
  }

   /**
   * aggregator assigned beacon index identifier 
   * @return beacon
  **/
  @ApiModelProperty(value = "aggregator assigned beacon index identifier ")
  public Integer getBeacon() {
    return beacon;
  }

  public void setBeacon(Integer beacon) {
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
    ServerAnnotation serverAnnotation = (ServerAnnotation) o;
    return Objects.equals(this.id, serverAnnotation.id) &&
        Objects.equals(this.label, serverAnnotation.label) &&
        Objects.equals(this.evidenceCode, serverAnnotation.evidenceCode) &&
        Objects.equals(this.date, serverAnnotation.date) &&
        Objects.equals(this.beacon, serverAnnotation.beacon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, evidenceCode, date, beacon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerAnnotation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    evidenceCode: ").append(toIndentedString(evidenceCode)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

