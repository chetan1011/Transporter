package in.esense.net.transporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Chetan Patel
 * 
 * @info Server request transporter class for server API call implements GET and
 *       POST method
 * 
 */

public class Transporter implements Request {

	private AysncRequest serverCall;

	@Override
	public synchronized void fire(String url, String payload, Receiver receiver) {
		serverCall = new AysncRequest(url, payload, receiver);
		serverCall.execute();
	}

	@Override
	public synchronized void fire(String url, Receiver receiver) {
		serverCall = new AysncRequest(url, receiver);
		serverCall.execute();
	}

	@Override
	public synchronized void fire(String url) {
		// TODO : code for fire and forget
	}

	@Override
	public synchronized void fire() {
		// TODO : code for fire and forget
	}

	@Override
	public void stop() {
		try {
			serverCall.cancel(true);
			Log.i(this.getClass().getName(), "Request Canceled...");
		} catch (Exception e) {
		}
	}

	private class AysncRequest extends AsyncTask<Void, Void, Response> {

		private String url, payload;
		private Receiver receiver;
		private HttpClient client;

		public AysncRequest(String url, String payload, Receiver receiver) {
			this.url = url;
			this.payload = payload;
			this.receiver = receiver;
			// ///////////////////Orignal code/////////////////
			// HttpParams params = new BasicHttpParams();
			// HttpConnectionParams.setConnectionTimeout(params, 50000);
			// HttpConnectionParams.setSoTimeout(params, 50000);
			// client = new DefaultHttpClient(params);
			// ////////////////////////////////////////////////

			/******** chetan ************************/
			// SchemeRegistry schReg = new SchemeRegistry();
			// schReg.register(new Scheme("http",
			// PlainSocketFactory.getSocketFactory(), 80));
			// schReg.register(new Scheme("https",
			// SSLSocketFactory.getSocketFactory(), 443));
			// ClientConnectionManager conMgr = new
			// ThreadSafeClientConnManager(params, schReg);
			/*******************************/

			client = getNewHttpClient();

		}

		public AysncRequest(String url, Receiver receiver) {
			this.url = url;
			this.receiver = receiver;
			client = getNewHttpClient();

		}

		@Override
		protected Response doInBackground(Void... params) {
			if (payload != null && !TextUtils.isEmpty(payload))
				return executePostRequest();
			else
				return executeGetRequest();

		}

		@Override
		protected void onPostExecute(Response result) {
			if (result != null && receiver != null) {
				int code = result.getCode();
				String content = result.getContent();

				if (code == 200) {
					receiver.onSuccess(content);
				} else if (code == Response.ERROR_CODE) {
					receiver.onError(content);
				} else {
					Log.i(this.getClass().getName(), "" + result.getCode());
					receiver.onFailure(content);
				}
			}
			super.onPostExecute(result);
		}

		private Response executePostRequest() {
			HttpPost post = new HttpPost(url);
			try {
				Log.i(getClass().getName(), "POST: Requesting with : "
						+ payload + " \nUrl:" + url);
				// ///////////////added by chetan 04-15-2015/////////////
				post.setHeader("Accept", "*/*");
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				// ////////////////////////////////////////////////////////
				post.setEntity(new StringEntity(payload, "UTF-8"));
				try {
					HttpResponse res = client.execute(post);
					int statuscode = res.getStatusLine().getStatusCode();
					if (statuscode == 200) {
						String response = readResponse(res);
						Log.i("Response", "" + response);
						return new Response(statuscode, response);
					} else {
						return new Response(statuscode, res.getStatusLine()
								.getReasonPhrase());
					}
				} catch (ClientProtocolException e) {
					Log.i("Caught inside client", e.getLocalizedMessage());
					e.printStackTrace();
					return new Response(Response.ERROR_CODE, ""
							+ e.getLocalizedMessage());
				} catch (IOException e) {
					Log.i("Caught inside IOException",
							"" + e.getLocalizedMessage());
					e.printStackTrace();
					return new Response(Response.ERROR_CODE, ""
							+ e.getLocalizedMessage());
				}
			} catch (UnsupportedEncodingException e) {
				Log.i("Caught inside IOException", "" + e.getLocalizedMessage());
				e.printStackTrace();
				return new Response(Response.ERROR_CODE, ""
						+ e.getLocalizedMessage());
			} catch (Exception e) {
				Log.i(this.getClass().getName(), "" + e.getLocalizedMessage());
				e.printStackTrace();
				return new Response(Response.ERROR_CODE, ""
						+ e.getLocalizedMessage());
			}

		}

		private Response executeGetRequest() {
			HttpGet get = new HttpGet(url);
			try {
				Log.i(getClass().getName(), "GET : Requesting with : "
						+ payload + " \nUrl:" + url);
				// ///////////////added by chetan 04-15-2015/////////////
				get.setHeader("Accept", "*/*");
				get.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				// ////////////////////////////////////////////////////////
				HttpResponse res = client.execute(get);
				int statuscode = res.getStatusLine().getStatusCode();
				if (statuscode == 200) {
					return new Response(statuscode, EntityUtils.toString(res
							.getEntity()));
				} else {
					return new Response(statuscode, res.getStatusLine()
							.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return new Response(Response.ERROR_CODE,
						e.getLocalizedMessage());
			} catch (IOException e) {
				e.printStackTrace();
				return new Response(Response.ERROR_CODE,
						e.getLocalizedMessage());
			}
		}

		private String readResponse(HttpResponse response) {
			InputStream input = null;
			try {
				input = response.getEntity().getContent();
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				byte[] buffer = new byte[1024];
				int count;

				while ((count = input.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				return out.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"The JVM does not support the compiler's default encoding.",
						e);
			} catch (IOException e) {
				return null;
			} finally {
				try {
					if (input != null) {
						input.close();
					}
				} catch (IOException ignored) {
				}
			}
		}

	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, 300000);
			HttpConnectionParams.setSoTimeout(params, 300000);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
