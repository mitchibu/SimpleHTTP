package jp.gr.java_conf.mitchibu.lib.simplehttp.request;

import java.io.InputStream;

import org.json.JSONObject;

import jp.gr.java_conf.mitchibu.lib.simplehttp.SimpleHTTP;

import android.net.Uri;
import android.text.TextUtils;

public class JsonObjectRequest extends SimpleHTTP.Request<JSONObject> {
	public JsonObjectRequest(Method method, Uri uri) {
		super(method, uri);
	}

	@Override
	protected JSONObject onResponse(InputStream in) throws Exception {
		if(in == null) return null;
		String s = StringRequest.toString(in);
		if(TextUtils.isEmpty(s)) return null;
		return new JSONObject(s);
	}
}
