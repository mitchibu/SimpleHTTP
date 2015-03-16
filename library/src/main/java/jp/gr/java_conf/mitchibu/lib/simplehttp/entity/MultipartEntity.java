package jp.gr.java_conf.mitchibu.lib.simplehttp.entity;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.util.EncodingUtils;

public class MultipartEntity implements Entity {
	private static final String BOUNDARY = "----------------314159265358979323846";
	private static final byte[] BOUNDARY_BYTES = EncodingUtils.getAsciiBytes(BOUNDARY);
	private static final String CRLF = "\r\n";
	private static final byte[] CRLF_BYTES = EncodingUtils.getAsciiBytes(CRLF);
	private static final String EXTRA = "--";
	private static final byte[] EXTRA_BYTES = EncodingUtils.getAsciiBytes(EXTRA);
	private static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=";
	private static final byte[] CONTENT_DISPOSITION_BYTES = EncodingUtils.getAsciiBytes(CONTENT_DISPOSITION);
	private static final String CONTENT_TYPE = "Content-Type: ";
	private static final byte[] CONTENT_TYPE_BYTES = EncodingUtils.getAsciiBytes(CONTENT_TYPE);
	private static final String CHARSET = "; charset=";
	private static final byte[] CHARSET_BYTES = EncodingUtils.getAsciiBytes(CHARSET);
	private static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding: ";
	private static final byte[] CONTENT_TRANSFER_ENCODING_BYTES = EncodingUtils.getAsciiBytes(CONTENT_TRANSFER_ENCODING);

	private final Entity[] entities;

	public MultipartEntity(Entity[] entities) {
		this.entities = entities;
	}

	@Override
	public void write(OutputStream outstream) throws IOException {
		for(Entity entity : entities) {
			outstream.write(EXTRA_BYTES);
			outstream.write(BOUNDARY_BYTES);
			outstream.write(CRLF_BYTES);
			entity.write(outstream);
		}
		outstream.write(EXTRA_BYTES);
		outstream.write(BOUNDARY_BYTES);
		outstream.write(EXTRA_BYTES);
		outstream.write(CRLF_BYTES);
	}
}
