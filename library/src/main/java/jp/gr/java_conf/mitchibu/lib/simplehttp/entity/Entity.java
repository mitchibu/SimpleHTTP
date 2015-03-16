package jp.gr.java_conf.mitchibu.lib.simplehttp.entity;

import java.io.IOException;
import java.io.OutputStream;

public interface Entity {
	void write(OutputStream out) throws IOException;
}
