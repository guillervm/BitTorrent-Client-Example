package es.deusto.ingenieria.ssdd.bitTorrent.metainfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the "info" dictionary in a Multiple Mode File. 
 */
public class MultipleFileInfoDictionary extends InfoDictionary {	
	//List of the dictionary that describes each file.
	private List<FileDictionary> files;
	
	public MultipleFileInfoDictionary() {
		super();
		this.files = new ArrayList<>();
	}
	
	public int getLength() {
		int length = 0;
		
		for (FileDictionary file : files) {
			length += file.getLength();
		}
		
		return length;
	}

	public List<FileDictionary> getFiles() {
		return files;
	}

	public void setFiles(List<FileDictionary> files) {
		this.files = files;
	}
	
	public void addFile(FileDictionary file) {
		if (file != null && !this.files.contains(file)) {
			//Update the fullpath name of the file
			StringBuffer fullPath = new StringBuffer();
			fullPath.append(super.getName());
			fullPath.append("/");
			fullPath.append(file.getPath());			
			file.setPath(fullPath.toString());
			
			this.files.add(file);
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(super.toString());
		
		if (this.files.size() > 0) {
			buffer.append("\nFiles:");

			for(FileDictionary file : this.files) {
				buffer.append("\n  - ");
				buffer.append(file.toString());
			}
		}
		
		return buffer.toString();		
	}
}