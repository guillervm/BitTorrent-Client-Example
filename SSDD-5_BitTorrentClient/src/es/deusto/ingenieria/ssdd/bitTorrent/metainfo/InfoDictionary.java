package es.deusto.ingenieria.ssdd.bitTorrent.metainfo;

import java.util.ArrayList;
import java.util.List;
/**
 * A dictionary that describes the file(s) of the torrent. There are two possible forms: 
 * one for the case of a 'single-file' torrent with no directory structure, 
 * and one for the case of a 'multi-file' torrent.  
 */
public abstract class InfoDictionary {
	//Number of bytes in each piece.
	private int pieceLength;
	//The filename (in Single File Mode) or The file path of the directory (in Multiple File Mode)
	private String name;	
	/*
	 * (optional) If it is set to "1", the client MUST publish its presence to get other peers ONLY 	 
	 * via the trackers explicitly described in the metainfo file. If this field is set to "0" or 
	 * is not present, the client may obtain peer from other means, e.g. peer exchange.
	 */
	private int privatePeers;	
	
	//20-byte SHA1 hash value of each piece (original format)
	private List<byte[]> byteSHA1;	
	//20-byte SHA1 hash value of each piece (string format)
	private List<String> hexStringSHA1;
	//20-byte SHA1 hash value of each piece (URLencoded format)
	private List<String> urlEncodedSHA1;
	
	/*
	 * urlencoded 20-byte SHA1 hash of the value of the info key from the Metainfo file.
	 * Note that the value will be a bencoded dictionary.
	 */	
	private String urlInfoHash;
	private String hexInfoHash;
	private byte[] infoHash;
	
	public InfoDictionary() {
		byteSHA1 = new ArrayList<>();
		hexStringSHA1 = new ArrayList<>();
		urlEncodedSHA1 = new ArrayList<>();
	}
	
	public abstract int getLength();
	
	public int getPieceLength() {
		return pieceLength;
	}
	
	public void setPieceLength(int pieceLength) {
		this.pieceLength = pieceLength;
	}
	
	public int getPrivatePeers() {
		return privatePeers;
	}
	
	public void setPrivatePeers(int privatePeers) {
		this.privatePeers = privatePeers;
	}
	
	public List<byte[]> getByteSHA1() {
		return byteSHA1;
	}

	public void setByteSHA1(List<byte[]> byteSHA1) {
		this.byteSHA1 = byteSHA1;
	}

	public List<String> getHexStringSHA1() {
		return hexStringSHA1;
	}

	public void setHexStringSHA1(List<String> hexStringSHA1) {
		this.hexStringSHA1 = hexStringSHA1;
	}

	public List<String> getUrlEncodedSHA1() {
		return urlEncodedSHA1;
	}

	public void setUrlEncodedSHA1(List<String> urlEncodedSHA1) {
		this.urlEncodedSHA1 = urlEncodedSHA1;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public byte[] getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(byte[] infoHash) {
		this.infoHash = infoHash;
	}	
	
	public String getUrlInfoHash() {
		return this.urlInfoHash;
	}

	public void setUrlInfoHash(String infoHash) {
		this.urlInfoHash = infoHash;
	}

	public String getHexInfoHash() {
		return this.hexInfoHash;
	}

	public void setHexInfoHash(String infoHash) {
		this.hexInfoHash = infoHash;
	}	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if (this.name != null && !this.name.trim().isEmpty()) {
			buffer.append("\nname: ");
			buffer.append(this.name);
		}
		
		if (this.pieceLength > 0) {
			buffer.append("\npiece length: ");
			buffer.append(this.pieceLength);
		}
		
		if (!this.byteSHA1.isEmpty() ||
			!this.hexStringSHA1.isEmpty() || 
			!this.urlEncodedSHA1.isEmpty()) {
			buffer.append("\nnumber of pieces: ");
			buffer.append(this.hexStringSHA1.size());
		}
		
		if (this.hexInfoHash != null && !this.hexInfoHash.trim().isEmpty()) {
			buffer.append("\ninfo_hash: ");
			buffer.append(this.hexInfoHash);
		}
		
		return buffer.toString();
	}
}