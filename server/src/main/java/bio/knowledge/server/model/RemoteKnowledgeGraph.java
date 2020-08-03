package bio.knowledge.server.model;

import java.util.Objects;
import bio.knowledge.server.model.Credentials;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A thought graph associated with this result that is not repeated here, but stored elsewhere in a way that can be remotely accessed by the reader of this Message
 */
@ApiModel(description = "A thought graph associated with this result that is not repeated here, but stored elsewhere in a way that can be remotely accessed by the reader of this Message")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-20T20:17:56.260Z[GMT]")
public class RemoteKnowledgeGraph   {
  @JsonProperty("url")
  private String url = null;

  @JsonProperty("credentials")
  @Valid
  private List<Credentials> credentials = null;

  @JsonProperty("protocol")
  private String protocol = "neo4j";

  public RemoteKnowledgeGraph url(String url) {
    this.url = url;
    return this;
  }

  /**
   * URL that provides programmatic access to the remote knowledge graph
   * @return url
  **/
  @ApiModelProperty(example = "http://robokop.renci.org/api/kg", required = true, value = "URL that provides programmatic access to the remote knowledge graph")
  @NotNull

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public RemoteKnowledgeGraph credentials(List<Credentials> credentials) {
    this.credentials = credentials;
    return this;
  }

  public RemoteKnowledgeGraph addCredentialsItem(Credentials credentialsItem) {
    if (this.credentials == null) {
      this.credentials = new ArrayList<>();
    }
    this.credentials.add(credentialsItem);
    return this;
  }

  /**
   * Credentials needed for programmatic access to the remote knowledge graph
   * @return credentials
  **/
  @ApiModelProperty(value = "Credentials needed for programmatic access to the remote knowledge graph")
  @Valid
  public List<Credentials> getCredentials() {
    return credentials;
  }

  public void setCredentials(List<Credentials> credentials) {
    this.credentials = credentials;
  }

  public RemoteKnowledgeGraph protocol(String protocol) {
    this.protocol = protocol;
    return this;
  }

  /**
   * Get protocol
   * @return protocol
  **/
  @ApiModelProperty(value = "")

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RemoteKnowledgeGraph remoteKnowledgeGraph = (RemoteKnowledgeGraph) o;
    return Objects.equals(this.url, remoteKnowledgeGraph.url) &&
        Objects.equals(this.credentials, remoteKnowledgeGraph.credentials) &&
        Objects.equals(this.protocol, remoteKnowledgeGraph.protocol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, credentials, protocol);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RemoteKnowledgeGraph {\n");
    
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    credentials: ").append(toIndentedString(credentials)).append("\n");
    sb.append("    protocol: ").append(toIndentedString(protocol)).append("\n");
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
