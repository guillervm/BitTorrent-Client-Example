package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class UnChokeMsg extends PeerProtocolMessage {
	
	public UnChokeMsg() {
		super(Type.UNCHOKE);
		super.setLength(ToolKit.intToBigEndianBytes(1, new byte[4], 0));		
	}
}
