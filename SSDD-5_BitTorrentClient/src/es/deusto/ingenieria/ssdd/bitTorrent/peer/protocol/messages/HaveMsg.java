package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import java.io.ByteArrayOutputStream;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class HaveMsg extends PeerProtocolMessage {
	
	public HaveMsg(int index) {
		super(Type.HAVE);
		super.setLength(ToolKit.intToBigEndianBytes(5, new byte[4], 0));
		this.updatePayload(index);
	}
	
	private void updatePayload(int index) {
		try {
			ByteArrayOutputStream payload = new ByteArrayOutputStream();

			payload.write(ToolKit.intToBigEndianBytes(index, new byte[4], 0));
			
			super.setPayload(payload.toByteArray());
		} catch (Exception ex) {
			System.out.println("# Error updating HaveMsg payload: " + ex.getMessage());
		}
	}	
}