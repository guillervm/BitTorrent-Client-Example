package es.deusto.ingenieria.ssdd.bitTorrent.metainfo;

/**
 * Describes the dictionary of a file in a Multiple Mode File
 */
public class FileDictionary {
	//Length of the file in bytes
	private int length;
	
	//(optional) a 32-character hexadecimal string corresponding to the MD5 sum of the file.
	private String md5sum;
	
	//Full filename (include path).
	private String path;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if (this.path != null && !this.path.trim().isEmpty()) {
			buffer.append(this.path);
		}
		
		if (this.length > 0) {
			buffer.append(" - (");
			buffer.append(this.length);
			buffer.append(" bytes)");			
		}
		
		if (this.md5sum != null && !this.md5sum.trim().isEmpty()) {
			buffer.append(" [MD5: ");
			buffer.append(this.md5sum);
			buffer.append("]");
		}
		
		return buffer.toString();
	}
}