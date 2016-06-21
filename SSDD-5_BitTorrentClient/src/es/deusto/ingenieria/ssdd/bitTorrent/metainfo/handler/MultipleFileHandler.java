package es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler;

import java.util.HashMap;
import java.util.List;

import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.FileDictionary;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.MultipleFileInfoDictionary;

public class MultipleFileHandler extends MetainfoFileHandler<MultipleFileInfoDictionary> {
	
	public MultipleFileHandler() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	protected void parseInfo(HashMap<String, Object> info) {
		MultipleFileInfoDictionary infoDictionary = new MultipleFileInfoDictionary();
		super.getMetainfo().setInfo(infoDictionary);
		
		if (info.containsKey("length")) {			
			super.setMetainfo(null);
			
			return;
		}		
		
		if (info.containsKey("piece length")) {
			infoDictionary.setPieceLength((Integer)info.get("piece length"));
		}		
		
		if (info.containsKey("private")) {
			infoDictionary.setPrivatePeers((Integer)info.get("private"));
		}
		
		if (info.containsKey("name")) {
			infoDictionary.setName((String)info.get("name"));
		}
		
		if (info.containsKey("pieces")) {
			super.parsePieces((String)info.get("pieces"));
		}
		
		if (info.containsKey("files")) {
			this.parseFiles((List<HashMap<String, Object>>)info.get("files"));
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void parseFiles(List<HashMap<String, Object>> filesMap) {
		FileDictionary fileDictionary;
		List<String> path;
		StringBuffer filename;
		
		for(HashMap<String, Object> fileMap : filesMap) {			
			fileDictionary = new FileDictionary();

			if (fileMap.containsKey("md5sum")) {
				fileDictionary.setMd5sum((String)fileMap.get("md5sum"));
			}
			
			if (fileMap.containsKey("length")) {
				fileDictionary.setLength((Integer)fileMap.get("length"));
			}
			
			if (fileMap.containsKey("path")) {
				path = (List<String>)fileMap.get("path");
				filename = new StringBuffer();
				
				for (int i=0; i<path.size(); i++) {
					if(i<path.size()-1) {
						filename.append("/");
					}
					
					filename.append(path.get(i));
				}
				
				fileDictionary.setPath(filename.toString());
			}
			
			super.getMetainfo().getInfo().addFile(fileDictionary);
		}
	}	
}