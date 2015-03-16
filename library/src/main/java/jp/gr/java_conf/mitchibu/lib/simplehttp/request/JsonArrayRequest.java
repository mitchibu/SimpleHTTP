package jp.gr.java_conf.mitchibu.lib.simplehttp.request;

import java.io.InputStream;

import org.json.JSONArray;

import jp.gr.java_conf.mitchibu.lib.simplehttp.SimpleHTTP;

import android.net.Uri;
import android.text.TextUtils;

public class JsonArrayRequest extends SimpleHTTP.Request<JSONArray> {
	public JsonArrayRequest(Method method, Uri uri) {
		super(method, uri);
	}

	@Override
	protected JSONArray onResponse(InputStream in) throws Exception {
		if(in == null) return null;
		String s = StringRequest.toString(in);
		if(TextUtils.isEmpty(s)) return null;
		return new JSONArray(s);
	}
}
