package es.deusto.ingenieria.ssdd.bitTorrent.metainfo;

/**
 * Describes the "info" dictionary in a Single Mode File. 
 */
public class SingleFileInfoDictionary extends InfoDictionary {	
	//Length of the file in bytes. 
	private int length;
	//(optional) a 32-character hexadecimal string corresponding to the MD5 sum of the file
	private String md5sum;
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String getMd5sum() {
		return md5sum;
	}
	
	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if (this.length > 0) {
			buffer.append("\nfile length: ");
			buffer.append(this.length);
		}
		
		if (this.md5sum != null && !this.md5sum.trim().isEmpty()) {
			buffer.append("\nmd5sum: ");
			buffer.append(this.md5sum);			
		}
		
		buffer.append(super.toString());
		
		return buffer.toString();
	}
}