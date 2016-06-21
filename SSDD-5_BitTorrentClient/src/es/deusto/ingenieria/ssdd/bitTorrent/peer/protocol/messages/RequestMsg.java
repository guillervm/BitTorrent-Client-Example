package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import java.io.ByteArrayOutputStream;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class RequestMsg extends PeerProtocolMessage {
	
	public RequestMsg(int index, int begin, int length) {
		super(Type.REQUEST);
		super.setLength(ToolKit.intToBigEndianBytes(13, new byte[4], 0));
		this.updatePayload(index, begin, length);
	}
	
	private void updatePayload(int index, int begin, int length) {
		try {
			ByteArrayOutputStream payload = new ByteArrayOutputStream();

			payload.write(ToolKit.intToBigEndianBytes(index, new byte[4], 0));
			payload.write(ToolKit.intToBigEndianBytes(begin, new byte[4], 0));
			payload.write(ToolKit.intToBigEndianBytes(length, new byte[4], 0));
			
			super.setPayload(payload.toByteArray());
		} catch (Exception ex) {
			System.out.println("# Error updating RequestMsg payload: " + ex.getMessage());
		}
	}	
}