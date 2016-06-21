package es.deusto.ingenieria.ssdd.bitTorrent.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamUtils {
	/**
	 * Transforms an InputStream to a byte array.
	 * 
	 * @param is
	 *            The <code>InputStream</code> to transform.
	 * @return The resulting <code>byte[]</code>.
	 */
	public static byte[] inputStreamToByteArray(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int reads;

			reads = is.read();

			while (reads != -1) {
				baos.write(reads);
				reads = is.read();
			}
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Transforms an InputStream to a String.
	 * 
	 * @param is
	 *            The <code>InputStream</code> to transform.
	 * @return The resulting <code>String</code>.
	 */
	public static String inputStreamToString(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
}
