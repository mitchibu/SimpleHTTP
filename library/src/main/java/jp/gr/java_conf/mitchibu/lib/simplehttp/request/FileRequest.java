package jp.gr.java_conf.mitchibu.lib.simplehttp.request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import jp.gr.java_conf.mitchibu.lib.simplehttp.SimpleHTTP;

import android.net.Uri;

public class FileRequest extends SimpleHTTP.Request<File> {
	private final File file;

	public FileRequest(Method method, Uri uri, File file) {
		super(method, uri);
		this.file = file;
	}

	@Override
	protected File onResponse(InputStream in) throws Exception {
		if(in == null) return null;

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);

			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			if(out != null) try {out.close();} catch(Exception e) {}
		}
		return file;
	}
}
