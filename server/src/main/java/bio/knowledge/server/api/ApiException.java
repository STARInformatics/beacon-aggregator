package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-15T11:46:37.748-07:00")

public class ApiException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7051207703127814089L;
	private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
    
    public int getCode() {
    	return code;
    }
}
