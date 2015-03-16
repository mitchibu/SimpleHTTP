package jp.gr.java_conf.mitchibu.lib.simplehttp.request;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.gr.java_conf.mitchibu.lib.simplehttp.SimpleHTTP;

import android.net.Uri;

public class StringRequest extends SimpleHTTP.Request<String> {
	public static String toString(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));
		StringBuilder sb = new StringBuilder();
		String s;
		while((s = reader.readLine()) != null) {
			sb.append(s).append('\n');
		}
		return sb.toString();
	}

	public StringRequest(Method method, Uri uri) {
		super(method, uri);
	}

	@Override
	protected String onResponse(InputStream in) throws Exception {
		if(in == null) return null;
		return toString(in);
	}
}
