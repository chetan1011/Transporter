package in.esense.net.transporter;


/**
 * @author Chetan Patel
 *
 */
public interface DownloadReceiver {
	
	public void onPrepareDownload();
	
	public void onSuccessDownload(String downloadedFilePath);

	public void onFailedDownload(String message);

}
