package org.mcteam.factions.util;

import java.io.*;

/**
 * Hard disk related methods such as read and write.
 */
public class DiscUtil {
	/**
	 * Convenience function for writing a string to a file.
	 */
	public static void write(File file, String content) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
		out.write(content);
		out.close();
	}

	/**
	 * Convenience function for reading a file as a string.
	 */
	public static String read(File file) throws IOException {
//		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		InputStream inStream = new FileInputStream(file);
		BufferedReader in = new BufferedReader(inputStreamToReader(inStream));
		StringBuilder ret = new StringBuilder();

		String line;
		while ((line = in.readLine()) != null) {
			ret.append(line);
		}
		in.close();

		return ret.toString();
	}

	/**
	 * Helper method for determining and using correct encoding when reading data,
	 * since so many people/text editors seem to mess up the encoding; hopefully this will help
	 * Adapted from: http://blog.publicobject.com/2010/08/handling-byte-order-mark-in-java.html
	 */
	public static Reader inputStreamToReader(InputStream in) throws IOException {
		if (in.available() < 3)
			return new InputStreamReader(in);
		int byte1 = in.read();
		int byte2 = in.read();
		if (byte1 == 0xFF && byte2 == 0xFE) {
			return new InputStreamReader(in, "UTF-16LE");
		} else if (byte1 == 0xFF && byte2 == 0xFF) {
			return new InputStreamReader(in, "UTF-16BE");
		} else {
			int byte3 = in.read();
			if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF) {
				return new InputStreamReader(in, "UTF-8");
			} else {
				byte[] first3 = {(byte)byte1, (byte)byte2, (byte)byte3};
				InputStream firstChars = new ByteArrayInputStream(first3);
				return new InputStreamReader(new SequenceInputStream(firstChars, in));
			}
		}
	}
}
