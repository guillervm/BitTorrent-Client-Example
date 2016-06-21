package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import java.io.ByteArrayOutputStream;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class PieceMsg extends PeerProtocolMessage {
	
	public PieceMsg(int index, int begin, byte[] block) {
		super(Type.PIECE);
		super.setLength(ToolKit.intToBigEndianBytes(9+block.length, new byte[4], 0));		
		this.updatePayload(index, begin, block);
	}
	
	private void updatePayload(int index, int begin, byte[] block) {
		try {
			ByteArrayOutputStream payload = new ByteArrayOutputStream();

			payload.write(ToolKit.intToBigEndianBytes(index, new byte[4], 0));
			payload.write(ToolKit.intToBigEndianBytes(begin, new byte[4], 0));
			payload.write(block);
			
			super.setPayload(payload.toByteArray());
		} catch (Exception ex) {
			System.out.println("# Error updating PieceMsg payload: " + ex.getMessage());
		}
	}
}