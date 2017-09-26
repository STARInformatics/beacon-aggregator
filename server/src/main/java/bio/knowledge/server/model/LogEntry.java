/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * LogEntry
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-15T11:46:37.748-07:00")

public class LogEntry   {
  @JsonProperty("timestamp")
  private String timestamp = null;

  @JsonProperty("beacon")
  private String beacon = null;

  @JsonProperty("query")
  private String query = null;

  @JsonProperty("message")
  private String message = null;

  public LogEntry timestamp(String timestamp) {
    this.timestamp = timestamp;
    return this;
  }

   /**
   * timestamp 
   * @return timestamp
  **/
  @ApiModelProperty(value = "timestamp ")


  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public LogEntry beacon(String beacon) {
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

  public LogEntry query(String query) {
    this.query = query;
    return this;
  }

   /**
   * URL of the API call executed by the aggregator 
   * @return query
  **/
  @ApiModelProperty(value = "URL of the API call executed by the aggregator ")


  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public LogEntry message(String message) {
    this.message = message;
    return this;
  }

   /**
   * error message 
   * @return message
  **/
  @ApiModelProperty(value = "error message ")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogEntry logEntry = (LogEntry) o;
    return Objects.equals(this.timestamp, logEntry.timestamp) &&
        Objects.equals(this.beacon, logEntry.beacon) &&
        Objects.equals(this.query, logEntry.query) &&
        Objects.equals(this.message, logEntry.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, beacon, query, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogEntry {\n");
    
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
    sb.append("    query: ").append(toIndentedString(query)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

