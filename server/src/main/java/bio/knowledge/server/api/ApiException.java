package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-24T18:37:28.321-08:00")

public class ApiException extends Exception{
	private static final long serialVersionUID = 7051207703127814089L;
	private int code;
	public ApiException (int code, String msg) {
		super(msg);
		this.setCode(code);
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
