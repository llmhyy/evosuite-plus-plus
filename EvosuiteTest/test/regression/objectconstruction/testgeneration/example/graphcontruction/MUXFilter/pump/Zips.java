package regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Zips {
	public static byte[] gunzipBuffer(byte[] data) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(data));
			byte[] buf = new byte[2048];
			while (true) {
				int size = in.read(buf);
				if (size <= 0)
					break;
				out.write(buf, 0, size);
			}
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("IOException while gzipping buffer. This should never happen", e);
		}
	}
}
