package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import java.io.ByteArrayOutputStream;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class BitfieldMsg extends PeerProtocolMessage {
	
	public BitfieldMsg(byte[] bitfield) {
		super(Type.BITFIELD);
		super.setLength(ToolKit.intToBigEndianBytes(1+bitfield.length, new byte[4], 0));
		this.updatePayload(bitfield);
	}
	
	private void updatePayload(byte[] bitfield) {
		try {
			ByteArrayOutputStream payload = new ByteArrayOutputStream();

			payload.write(bitfield);
			
			super.setPayload(payload.toByteArray());
		} catch (Exception ex) {
			System.out.println("# Error updating BitfieldMsg payload: " + ex.getMessage());
		}
	}	
}