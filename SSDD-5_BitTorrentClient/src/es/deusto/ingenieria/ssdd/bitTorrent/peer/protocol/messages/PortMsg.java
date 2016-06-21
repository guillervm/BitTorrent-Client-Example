package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import java.io.ByteArrayOutputStream;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class PortMsg extends PeerProtocolMessage {
	
	public PortMsg(int port) {
		super(Type.PORT);
		super.setLength(ToolKit.intToBigEndianBytes(3, new byte[4], 0));
		this.updatePayload(port);
	}
	
	private void updatePayload(int port) {
		try {
			ByteArrayOutputStream payload = new ByteArrayOutputStream();

			payload.write(ToolKit.intToBigEndianBytes(port, new byte[4], 0));
			
			super.setPayload(payload.toByteArray());
		} catch (Exception ex) {
			System.out.println("# Error updating PortMsg payload: " + ex.getMessage());
		}
	}	
}