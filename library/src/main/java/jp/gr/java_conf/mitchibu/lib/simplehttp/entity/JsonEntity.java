package jp.gr.java_conf.mitchibu.lib.simplehttp.entity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonWriter;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class JsonEntity implements Entity {
	private final StringWriter stringWriter = new StringWriter();
	private final JsonWriter writer;

	public JsonEntity() {
		writer = new JsonWriter(new BufferedWriter(stringWriter));
	}

	public void begin() throws Exception {
		beginObject(null);
	}

	public void commit() throws Exception {
		endObject();
		writer.close();
	}

	public void beginArray(String name) throws Exception {
		if(name != null) writer.name(name);
		writer.beginArray();
	}

	public void endArray() throws Exception {
		writer.endArray();
	}

	public void beginObject(String name) throws Exception {
		if(name != null) writer.name(name);
		writer.beginObject();
	}

	public void endObject() throws Exception {
		writer.endObject();
	}

	public void setValue(String name, String value) throws Exception {
		writer.name(name).value(value);
	}

	@Override
	public void write(OutputStream out) throws IOException {
		out.write(stringWriter.toString().getBytes());
	}
}
