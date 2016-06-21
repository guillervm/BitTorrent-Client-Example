package es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler;

import java.util.HashMap;

import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.SingleFileInfoDictionary;

public class SingleFileHandler extends MetainfoFileHandler<SingleFileInfoDictionary> {
	
	public SingleFileHandler() {
		super();
	}
	
	protected void parseInfo(HashMap<String, Object> info) {
		SingleFileInfoDictionary infoDictionary = new SingleFileInfoDictionary();
		super.getMetainfo().setInfo(infoDictionary);
		
		if (info.containsKey("length")) {
			infoDictionary.setLength((Integer)info.get("length"));
		} else {
			super.setMetainfo(null);
			
			return;
		}		
		
		if (info.containsKey("piece length")) {
			infoDictionary.setPieceLength((Integer)info.get("piece length"));
		}
		
		if (info.containsKey("pieces")) {
			super.parsePieces((String)info.get("pieces"));
		}		
		
		if (info.containsKey("private")) {
			infoDictionary.setPrivatePeers((Integer)info.get("private"));
		}
		
		if (info.containsKey("name")) {
			infoDictionary.setName((String)info.get("name"));
		}		
	}
}