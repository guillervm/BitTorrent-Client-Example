package es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages;

import java.io.ByteArrayOutputStream;

public class Handsake {	
	private static final String DEFAULT_PROTOCOL = "BitTorrent protocol";
	private static final String RESERVED = "00000000";
	
	private int nameLength;
	private String protocolName;
	private String reserved;
	private byte[] infoHash;
	private String peerId;
	
	public Handsake() {		
		this.protocolName = Handsake.DEFAULT_PROTOCOL;
		this.nameLength = this.protocolName.length();
		this.reserved = Handsake.RESERVED;
	}
	
	public int getNameLength() {
		return nameLength;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
		this.nameLength = protocolName.length();
	}

	public String getReserved() {
		return reserved;
	}

	public byte[] getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(byte[] infoHash) {
		this.infoHash = infoHash;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	
	public byte[] getBytes() {
		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();

			result.write(this.nameLength);
			result.write(this.protocolName.getBytes());
			result.write(this.reserved.getBytes());
			result.write(this.infoHash);
			result.write(this.peerId.getBytes());
			
			return result.toByteArray();
		} catch (Exception ex) {
			return null;
		}
	}		
		
		
	public String toString() {
		return new String(this.getBytes());
	}
}