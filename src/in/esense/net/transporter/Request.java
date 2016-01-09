package in.esense.net.transporter;


/**
 * @author Chetan Patel
 * 
 */
public interface Request {

	/** fire post request with payload */
	public void fire(String url, String payload, Receiver receiver);
	
	/** fire get request */
	public void fire(String url, Receiver receiver);
	
	/** fire and forget */
	public void fire(String url);
	
	/** fire and forget */
	public void fire();
	
	/** stop sending request **/
	public void stop();
	
}
