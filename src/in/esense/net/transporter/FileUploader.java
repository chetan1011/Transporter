package in.esense.net.transporter;
//package in.zeitech.net.transporter;
//
//import in.zeitech.net.transporter.Receiver;
//import in.zeitech.net.transporter.Request;
//import in.zeitech.net.transporter.Response;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//import android.os.AsyncTask;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.zeitech.undervisor.utils.Constants;
//
///**
// * @author Pratik Popat
// * @info class for uploading files to server
// */
//public class FileUploader extends AsyncTask<Void, Void, Response> implements
//		Request {
//	private String url, payload, filePath;
//	private File file;
//	private Receiver receiver;
//
//	public FileUploader(String url, String payload, String filePath,
//			Receiver receiver) {
//		this.url = url;
//		this.payload = payload;
//		this.receiver = receiver;
//		this.filePath = filePath;
//	}
//
//	public FileUploader(String url, String payload, File file, Receiver receiver) {
//		this.url = url;
//		this.payload = payload;
//		this.receiver = receiver;
//		this.file = file;
//	}
//
//	@Override
//	public void fire(String url, String payload, Receiver receiver) {
//	}
//
//	@Override
//	public void fire(String url, Receiver receiver) {
//	}
//
//	@Override
//	public void fire() {
//		this.execute();
//	}
//
//	@Override
//	public void stop() {
//		try {
//			this.cancel(true);
//		} catch (Exception e) {
//		}
//	}
//
//	// ////////////// async task ///////////
//
//	@Override
//	protected Response doInBackground(Void... params) {
//		return uploadFile();
//	}
//
//	@Override
//	protected void onPostExecute(Response result) {
//		if (result != null && receiver != null) {
//			int code = result.getCode();
//			String content = result.getContent();
//
//			if (code == 200) {
//				receiver.onSuccess(content);
//			} else if (code == Response.ERROR_CODE) {
//				receiver.onError(content);
//			} else {
//				receiver.onFailure(content);
//			}
//		}
//		super.onPostExecute(result);
//	}
//
//	// /////////////////////////////////////////////////
//
//	private Response uploadFile() {
//		try {
//			System.out.println("Uploading File With  \nPayload: " + payload
//					+ "+\n url: " + url + " \nfilePath: " + filePath);
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost postRequest = new HttpPost(url);
//			MultipartEntity multipart = new MultipartEntity(
//					HttpMultipartMode.BROWSER_COMPATIBLE);
//			StringBody fileinfo = new StringBody(payload);
//			multipart.addPart(Constants.FILE_INFO_PART, fileinfo);
//			if (file == null) {
//				if (!TextUtils.isEmpty(filePath)) {
//					File f = new File(filePath);
//					FileBody filebody = new FileBody(f);
//					System.out.println("File :" + f.getName());
//					multipart.addPart(Constants.FILE_PART, filebody);
//				}
//			} else {
//				FileBody filebody = new FileBody(file);
//				System.out.println("File :" + file.getName());
//				multipart.addPart(Constants.FILE_PART, filebody);
//			}
//
//			postRequest.setEntity(multipart);
//			HttpResponse res = httpClient.execute(postRequest);
//			int statuscode = res.getStatusLine().getStatusCode();
//			if (statuscode == 200) {
//				String response = readResponse(res);
//				Log.e("Response", "" + response);
//				return new Response(statuscode, response);
//			} else {
//				return new Response(statuscode, res.getStatusLine()
//						.getReasonPhrase());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Response(Response.ERROR_CODE, e.getLocalizedMessage());
//		}
//
//	}
//
//	private String readResponse(HttpResponse response) {
//		InputStream input = null;
//		try {
//			input = response.getEntity().getContent();
//			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
//			byte[] buffer = new byte[1024];
//			int count;
//
//			while ((count = input.read(buffer)) > 0) {
//				out.write(buffer, 0, count);
//			}
//			return out.toString("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(
//					"The JVM does not support the compiler's default encoding.",
//					e);
//		} catch (IOException e) {
//			return null;
//		} finally {
//			try {
//				if (input != null) {
//					input.close();
//				}
//			} catch (IOException ignored) {
//			}
//		}
//	}
//
//	@Override
//	public void fire(String url) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
