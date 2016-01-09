package in.esense.net.transporter;

/**
 * @author Chetan Patel
 * 
 * @info Asynchronous Receiver to handle http response
 * 
 */
public interface Receiver {

	public void onSuccess(String response);

	public void onFailure(String failure);

	public void onError(String error);

}
