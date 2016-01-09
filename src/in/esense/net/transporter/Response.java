package in.esense.net.transporter;

/**
 * @author Chetan Patel 
 * 
 *  @info Http Response POJO
 *
 */
public class Response {
	private int code;
	private String content;
	
	public static final int ERROR_CODE = -1;
	
	public Response(){}

	public Response(int code, String content) {
		super();
		this.code = code;
		this.content = content;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}