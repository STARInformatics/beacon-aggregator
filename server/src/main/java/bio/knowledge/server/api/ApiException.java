package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-19T15:48:00.887-07:00")

public class ApiException extends Exception{
	private int code;
	public ApiException (int code, String msg) {
		super(msg);
		this.code = code;
	}
}
