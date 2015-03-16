package jp.gr.java_conf.mitchibu.lib.simplehttp.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

public class FormEntity implements Entity {
	private final List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

	public void add(String key, String value) {
		list.add(new Pair<String, String>(key, value));
	}

	@Override
	public void write(OutputStream out) throws IOException {
		boolean isFirst = true;
		for(Pair<String, String> pair : list) {
			if(isFirst) isFirst = false;
			else out.write('&');
			out.write(URLEncoder.encode(pair.first, "UTF-8").getBytes());
			out.write('=');
			out.write(URLEncoder.encode(pair.second, "UTF-8").getBytes());
		}
	}
}
