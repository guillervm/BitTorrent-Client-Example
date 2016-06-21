package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class InterestedMsg extends PeerProtocolMessage {
	
	public InterestedMsg() {
		super(Type.INTERESTED);
		super.setLength(ToolKit.intToBigEndianBytes(1, new byte[4], 0));		
	}
}
