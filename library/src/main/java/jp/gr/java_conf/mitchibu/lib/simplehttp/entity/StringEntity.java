package jp.gr.java_conf.mitchibu.lib.simplehttp.entity;

import java.io.IOException;
import java.io.OutputStream;

public class StringEntity implements Entity {
	private final String data;

	public StringEntity(String data) {
		this.data = data;
	}

	@Override
	public void write(OutputStream out) throws IOException {
		out.write(data.getBytes());
	}
}
