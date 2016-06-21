package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class KeepAliveMsg extends PeerProtocolMessage {
	
	public KeepAliveMsg() {
		super(Type.KEEP_ALIVE);
		super.setLength(ToolKit.intToBigEndianBytes(0, new byte[4], 0));		
	}
}