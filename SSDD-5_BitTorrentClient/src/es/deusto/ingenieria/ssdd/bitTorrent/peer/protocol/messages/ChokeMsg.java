package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class ChokeMsg extends PeerProtocolMessage {
	
	public ChokeMsg() {
		super(Type.CHOKE);
		super.setLength(ToolKit.intToBigEndianBytes(1, new byte[4], 0));		
	}
}