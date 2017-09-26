package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-26T14:52:59.489-07:00")

public class NotFoundException extends ApiException {
	private int code;
	public NotFoundException (int code, String msg) {
		super(code, msg);
		this.code = code;
	}
}
