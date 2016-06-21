package es.deusto.ingenieria.ssdd.bitTorrent.util;

public class MyToolkit {
	
	/**
	 * Transforms the <code>char</code>s into an <code>Integer</code>.
	 * @param byte0 The 5th <code>byte</code> of the peer.
	 * @param byte1 The 6th <code>byte</code> of the peer.
	 * @return An <code>Integer</code> witch represents the port number.
	 */
	public static int bytesToPort(char byte0, char byte1) {
		return (0xFF & byte0) << 8 | (0xFF & byte1);
	}
	
	/**
	 * Returns the message length like an <code>Integer</code> from a <code>bigEndian</code> encoding <code>byte[]</code>.
	 * @param messageBytes The <code>byte[]</code> containing the length like <code>bigEndianBytes</code>.
	 * @return An <code>Integer</code> representing the message length.
	 */
	public static int getMessageLength(byte[] messageBytes) {
		if (messageBytes.length < 4) {
			return -1;
		} else {
			return ToolKit.bigEndianBytesToInt(messageBytes, 0);
		}
	}
}
