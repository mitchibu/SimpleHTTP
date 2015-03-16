package jp.gr.java_conf.mitchibu.lib.simplehttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Pair;

import jp.gr.java_conf.mitchibu.lib.simplehttp.SimpleHTTP;
import jp.gr.java_conf.mitchibu.lib.simplehttp.entity.Entity;

public class HttpClientTask<E> extends SimpleHTTP.Task<E> {
	public HttpClientTask(SimpleHTTP.Request<E> req) {
		super(req);
	}

	@Override
	public SimpleHTTP.Response<E> call(SimpleHTTP.Request<E> req) throws Exception {
		final SimpleHTTP.Response<E> res = new SimpleHTTP.Response<E>();
		final String url = req.uri.toString();
		final Entity entity = req.getEntity();

		HttpClient http = new DefaultHttpClient();
		try {
			HttpUriRequest request;
			switch(req.method) {
			case GET:
				request = new HttpGet(url);
				break;
			case POST:
				request = new HttpPost(url);
				if(entity != null) ((HttpPost)request).setEntity(new MyEntity(entity));
				break;
			default:
				throw new UnsupportedOperationException();
			}

			boolean gzip = false;
			Map<String, List<String>> headers = req.getAdditionalHeaders();
			if(headers != null) {
				for(Map.Entry<String, List<String>> entry : headers.entrySet()) {
					String name = entry.getKey();
					List<String> values = entry.getValue();

					if(name.equalsIgnoreCase("Content-Encoding")) {
						for(String v : values) {
							if(v.equalsIgnoreCase("gzip")) {
								gzip = true;
								break;
							}
						}
					}
					for(String v : values) {
						request.addHeader(name, v);
					}
				}
			}

			HttpResponse response = http.execute(request);

			InputStream in = null;
			StatusLine status = response.getStatusLine();
			res.statusCode = status.getStatusCode();
			res.message = status.getReasonPhrase();
			if(res.statusCode / 100 == 2) {
				gzip = false;
				Header[] contentEncodings = response.getHeaders("Content-Encoding");
				for(Header header : contentEncodings) {
					gzip = header.getValue().equalsIgnoreCase("gzip");
				}
				in = response.getEntity().getContent();
				if(gzip) in = new GZIPInputStream(in);
			}
			if(in != null) res.data = req.onResponse(in);
		} finally {
			http.getConnectionManager().shutdown();
		}
		return res;
	}

	private class MyEntity implements HttpEntity {
		private final Entity entity;

		public MyEntity(Entity entity) {
			this.entity = entity;
		}

		@Override
		public void consumeContent() throws IOException {
		}

		@Override
		public InputStream getContent() throws IOException, IllegalStateException {
			return null;
		}

		@Override
		public Header getContentEncoding() {
			return null;
		}

		@Override
		public long getContentLength() {
			return -1;
		}

		@Override
		public Header getContentType() {
			return null;
		}

		@Override
		public boolean isChunked() {
			return false;
		}

		@Override
		public boolean isRepeatable() {
			return false;
		}

		@Override
		public boolean isStreaming() {
			return true;
		}

		@Override
		public void writeTo(OutputStream outstream) throws IOException {
			entity.write(outstream);
		}
	}
}
