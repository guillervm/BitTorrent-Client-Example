package es.deusto.ingenieria.ssdd.bitTorrent.metainfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MetainfoFile<Info extends InfoDictionary> {
	//The announce URL of the tracker.
	private String announce;
	//(optional) this is an extention to the official specification, offering backwards-compatibility.
	private List<List<String>> announceList;
	//(optional) the creation time of the torrent, in standard UNIX epoch format (seconds since 1-Jan-1970 00:00:00 UTC)
	private long creationDate = -1;
	//(optional) free-form textual comments of the author
	private String comment;
	//(optional) name and version of the program used to create the .torrent 
	private String createdBy;
	//(optional) the string encoding format used to generate the pieces part of the info dictionary in the .torrent metafile.
	private String encoding;
	//a dictionary that describes the file(s) of the torrent.
	private Info info;
	
	public String getAnnounce() {
		return announce;
	}
	
	public void setAnnounce(String announce) {
		this.announce = announce;
	}
	
	public List<List<String>> getAnnounceList() {
		return announceList;
	}
	
	public List<String> getHTTPAnnounceList() {
		List<String> announceHTTP = new ArrayList<>();
		
		if (this.announce != null && this.announce.startsWith("http://")) {
			announceHTTP.add(announce);
		}
		
		if (this.announceList != null) {
			for (List<String> list : this.announceList) {
				for (String tracker : list) {
					if (tracker.startsWith("http://")) {
						announceHTTP.add(tracker);
					}				
				}
			}
		}
		
		return announceHTTP;
	}
	
	public void setAnnounceList(List<List<String>> announceList) {
		this.announceList = announceList;
	}
	
	public long getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public Info getInfo() {
		return this.info;
	}
	
	public void setInfo(Info info) {
		this.info = info;
	}	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if (this.announce != null && !this.announce.trim().isEmpty()) {
			buffer.append("announce: ");
			buffer.append(this.announce);
		}
		
		if (this.announceList != null && !this.announceList.isEmpty()) {
			buffer.append("\nannounce list: ");
			
			for (List<String> alist : this.announceList) {
				buffer.append("\n  -");
				for (String server : alist) {
					buffer.append(" ");
					buffer.append(server);					
				}
			}
		}
		
		if (this.creationDate != -1) {
			buffer.append("\ncreation date: ");
			buffer.append(new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss").format(new Date(this.creationDate*1000)));
		}
		
		if (this.comment != null && !this.comment.trim().isEmpty()) {
			buffer.append("\ncomment: ");
			buffer.append(this.comment);
		}
		
		if (this.createdBy != null && !this.createdBy.trim().isEmpty()) {
			buffer.append("\ncreated by: ");
			buffer.append(this.createdBy);
		}
		
		if (this.encoding != null && !this.encoding.trim().isEmpty()) {
			buffer.append("\nencoding: ");
			buffer.append(this.encoding);
		}		
		
		if (info != null) {
			buffer.append(info.toString());
		}
		
		return buffer.toString();
	}
}
