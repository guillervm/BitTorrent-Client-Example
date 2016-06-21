package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public abstract class PeerProtocolMessage {

	public static enum Type {
		KEEP_ALIVE(null),
		CHOKE(0),
		UNCHOKE(1),
		INTERESTED(2),
		NOT_INTERESTED(3),
		HAVE(4),
		BITFIELD(5),
		REQUEST(6),
		PIECE(7),
		CANCEL(8),
		PORT(9);
		
		private final Integer id;
		
		private Type(Integer id) { this.id = id; }
		public Integer getId() { return this.id; }
	}
	
	private Type type;
	private byte[] length;
	private byte[] payload;

	public PeerProtocolMessage(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

	public byte[] getLength() {
		return length;
	}
	
	public void setLength(byte[] length) {
		this.length = length;
	}
	
	public Integer getId() {
		return !type.equals(Type.KEEP_ALIVE) ? type.getId() : -1;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public byte[] getBytes() {
		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();

			if (length != null) {
				result.write(this.length);
			}
			
			if (this.getId() != null) {
				result.write(this.getId());
			}
			
			if (payload != null) {
				result.write(this.payload);
			}
			
			return result.toByteArray();
		} catch (Exception ex) {
			return null;
		}
	}
	
	public PeerProtocolMessage parseMessage(byte[] msgBytes) {
		PeerProtocolMessage message = null;
		
		if (msgBytes != null && msgBytes.length != 0) {
			
			int length = ToolKit.bigEndianBytesToInt(msgBytes, 0);
			
			//
			if (length == 0) {
				return new KeepAliveMsg();
			}
			
			int id = msgBytes[4];
					
			switch (id) {			
				case 0:	//choke
					message = new ChokeMsg();
					break;
				case 1:	//unchoke
					message = new UnChokeMsg();
					break;
				case 2:	//interested
					message = new InterestedMsg();
					break;
				case 3: //not_interested
					message = new NotInterestedMsg();
					break;
				case 4: //have
					message = new HaveMsg(ToolKit.bigEndianBytesToInt(msgBytes, 5));	//Piece index
					break;
				case 5:	//bitfield
					message = new BitfieldMsg(Arrays.copyOfRange(msgBytes, 5, msgBytes.length));	//Bitfield
					break;
				case 6:	//request
					message = new RequestMsg(ToolKit.bigEndianBytesToInt(msgBytes, 5), 		//Piece index 
							                 ToolKit.bigEndianBytesToInt(msgBytes, 9), 		//Block offset
							                 ToolKit.bigEndianBytesToInt(msgBytes, 13));	//Block length	
					break;
				case 7:	//piece
					message = new PieceMsg(ToolKit.bigEndianBytesToInt(msgBytes, 5),			//Piece index
              			   				   ToolKit.bigEndianBytesToInt(msgBytes, 9),			//Block offset
              			   				   Arrays.copyOfRange(msgBytes, 13, msgBytes.length));	//Data					
					break;
				case 8:	//cancel
					message = new CancelMsg(ToolKit.bigEndianBytesToInt(msgBytes, 5), 	//Piece index
			                 				ToolKit.bigEndianBytesToInt(msgBytes, 9), 	//Block offset
			                 				ToolKit.bigEndianBytesToInt(msgBytes, 13));	//Block length						
					break;
				case 9:	//port
					message = new PortMsg(ToolKit.bigEndianBytesToInt(msgBytes, 5));	//Port number
					break;					
			}
		}
		
		return message;		
	}
}