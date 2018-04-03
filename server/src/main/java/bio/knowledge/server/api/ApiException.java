package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-02T12:58:15.341-07:00")

public class ApiException extends Exception{
	private int code;
	public ApiException (int code, String msg) {
		super(msg);
		this.code = code;
	}
}
