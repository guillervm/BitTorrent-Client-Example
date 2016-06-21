package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class NotInterestedMsg extends PeerProtocolMessage {
	
	public NotInterestedMsg() {
		super(Type.NOT_INTERESTED);
		super.setLength(ToolKit.intToBigEndianBytes(1, new byte[4], 0));		
	}
}
