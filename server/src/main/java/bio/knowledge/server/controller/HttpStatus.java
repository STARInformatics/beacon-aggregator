/**
 * 
 */
package bio.knowledge.server.controller;

/**
 * @author richard
 *
 */
public interface HttpStatus {
	public final Integer SUCCESS = 200;
	public final Integer CREATED = 201;  // The query was "created" but the data is already available
	public final Integer QUERY_IN_PROGRESS = 102;
	public final Integer CONTENT_NOT_FOUND = 400;
	public final Integer SERVER_ERROR = 500;
}
