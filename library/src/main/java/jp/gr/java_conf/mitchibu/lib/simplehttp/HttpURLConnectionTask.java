package jp.gr.java_conf.mitchibu.lib.simplehttp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jp.gr.java_conf.mitchibu.lib.simplehttp.entity.Entity;

public class HttpURLConnectionTask<E> extends SimpleHTTP.Task<E> {
	public HttpURLConnectionTask(SimpleHTTP.Request<E> req) {
		super(req);
	}

	@Override
	public SimpleHTTP.Response<E> call(SimpleHTTP.Request<E> req) throws Exception {
		final SimpleHTTP.Response<E> res = new SimpleHTTP.Response<E>();
		final String url = req.uri.toString();
		final String method = req.method.name();
		final Entity entity = req.getEntity();
		final Map<String, List<String>> headers = req.getAdditionalHeaders();

		InputStream in = null;
		OutputStream out = null;
		HttpURLConnection http = null;
		try {
			//boolean gzip = false;
			String encode = null;
//			http = (HttpURLConnection)new URL(url).openConnection();
			http = getHttpsConnection(url);
			http.setConnectTimeout(req.getConnectTimeout());
			http.setReadTimeout(req.getReadTimeout());
			http.setRequestMethod(method);
			if(headers != null) {
				for(Map.Entry<String, List<String>> entry : headers.entrySet()) {
					String name = entry.getKey();
					List<String> values = entry.getValue();

					if(name.equalsIgnoreCase("Content-Encoding")) {
						if(values.size() > 0) encode = values.get(0);
					}
					for(String v : values) http.addRequestProperty(name, v);
				}
			}
			http.setDoInput(true);
			http.setDoOutput(entity != null);
			http.connect();
			if(entity != null) {
				out = http.getOutputStream();
				if("gzip".equalsIgnoreCase(encode)) out = new GZIPOutputStream(out);
				else if("deflate".equalsIgnoreCase(encode)) out = new DeflaterOutputStream(out);
				entity.write(out);
				out.flush();
				out.close(); // closeしないとデータが全部行かない
				out = null;
			}

			res.statusCode = http.getResponseCode();
			res.message = http.getResponseMessage();
			res.headers.putAll(http.getHeaderFields());
			if(res.statusCode / 100 == 2) {
				encode = null;
				List<String> contentEncodings = res.headers.get("Content-Encoding");
				if(contentEncodings != null) {
					if(contentEncodings.size() > 0) encode = contentEncodings.get(0);
				}
				in = http.getInputStream();
				if("gzip".equalsIgnoreCase(encode)) in = new GZIPInputStream(in);
				else if("deflate".equalsIgnoreCase(encode)) in = new DeflaterInputStream(in);
			}
			if(in != null) res.data = req.onResponse(in);
		} finally {
			if(in != null) try { in.close(); } catch(Exception e) {}
			if(out != null) try { out.close(); } catch(Exception e) {}
			if(http != null) http.disconnect();
		}
		return res;
	}

	private static HttpURLConnection getHttpsConnection(String url) throws Exception {
		URL connUrl = new URL(url);
		if("https".equals(connUrl.getProtocol())) {
			TrustManager[] tm = { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
			}};
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, tm, null);
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			HttpsURLConnection conn = (HttpsURLConnection)connUrl.openConnection();
			((HttpsURLConnection)conn).setSSLSocketFactory(sslcontext.getSocketFactory());
			return conn;
		} else {
			return (HttpURLConnection)connUrl.openConnection();
		}
	}
}
