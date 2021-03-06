package in.esense.net.transporter;

import in.zeitech.net.transporter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Chetan Patel : file download module
 * 
 */
public class FileDownloader extends AsyncTask<Void, Long, Boolean> {

	private Context context;
	private String url, payload, fileName, filePath;
	private String message;
	private boolean stopDownload;
	
	private File downlodFile;
	
	private DownloadReceiver receiver;

	private FileDownloader(Context context, String fileName, String filePath,
			String url, String payload, DownloadReceiver receiver) {
		this.context = context;
		this.url = url;
		this.payload = payload;
		this.fileName = fileName;
		this.filePath = filePath;
		this.receiver = receiver;
	}

	public static FileDownloader download(Context context, String fileName,
			String filePath, String url, String payload, DownloadReceiver receiver) {
		FileDownloader downloader = new FileDownloader(context, fileName,
				filePath, url, payload, receiver);
		downloader.execute();
		return downloader;
	}

	public void stopDownload(FileDownloader downloader) {
		stopDownload = true;
		try {
			Log.e(this.getClass().getName(), "Stopping Download");
			downloader.cancel(true);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	    receiver.onPrepareDownload();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		stopDownload = false;
		Log.e("File downloading", "downloading.. " + url + "\n payload:"
				+ payload + "\nFile Name:" + fileName);

		InputStream response = getStream();
		if(response == null)
			return false;
		
		FileOutputStream fos = null;
		
		
		try {
			downlodFile = new File(filePath, fileName);

			if (downlodFile.exists()) {
              //TODO:
			}

			fos = new FileOutputStream(downlodFile);

			byte[] buffer = new byte[1024];
			int count;

			Log.e(getClass().getName(), "? " + response);
			while ((count = response.read(buffer)) > 0 && !stopDownload) {
				fos.write(buffer, 0, count);
			}
			fos.flush();

			if (stopDownload) {
				message = context.getString(R.string.download_stopped);
				downlodFile.delete();
				return false;
			}
			return true;
		} catch (Exception e) {
			if (downlodFile != null)
				downlodFile.delete();
			e.printStackTrace();
			message = context.getString(R.string.download_failed);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private InputStream getStream() {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			
			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(payload));
			HttpResponse response = httpClient.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream inputstream = response.getEntity().getContent();

				if (inputstream != null) {
					return inputstream;
				} else {
					Log.e("error", "download error: inputstream is null");
				}
			} else {
				message = context.getString(R.string.error_connection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = context.getString(R.string.error_connection);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		// download success
		if(result){
			receiver.onSuccessDownload(downlodFile.getAbsolutePath());
		}
		// download failed
		else{
			receiver.onFailedDownload(message);
		}
	}

}
