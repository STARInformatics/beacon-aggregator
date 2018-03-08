package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-24T18:37:28.321-08:00")

public class NotFoundException extends ApiException {
	private static final long serialVersionUID = 2435449333504125846L;
	private int code;
	public NotFoundException (int code, String msg) {
		super(code, msg);
		this.code = code;
	}
	public int getCode() {
		return code;
	}
}
