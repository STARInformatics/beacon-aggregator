package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-19T18:00:36.924-08:00")

public class NotFoundException extends ApiException {
	private static final long serialVersionUID = 5877047418171588396L;
	private int code;
	public NotFoundException (int code, String msg) {
		super(code, msg);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
