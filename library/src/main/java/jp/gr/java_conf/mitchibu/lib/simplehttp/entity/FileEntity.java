package jp.gr.java_conf.mitchibu.lib.simplehttp.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileEntity implements Entity {
	private final File file;

	public FileEntity(File file) {
		this.file = file;
	}

	@Override
	public void write(OutputStream out) throws IOException {
		byte[] buf = new byte[4096];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			int len;
			while((len = in.read(buf)) != 0) out.write(buf, 0, len);
		} finally {
			if(in != null) try { in.close(); } catch(Exception e) {}
		}
	}
}
