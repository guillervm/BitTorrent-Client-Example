package es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler.test;

import java.io.File;

import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler.MetainfoFileHandler;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler.MultipleFileHandler;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler.SingleFileHandler;

public class MetainfoFileHandlerTest {
	public static void main(String[] args) {		
		try {			
			File folder = new File("torrent");
			MetainfoFileHandler<?> handler;
			
			if (folder.isDirectory()) {
				for (File torrent : folder.listFiles()) {
					try {
						handler = new SingleFileHandler();
						handler.parseTorrenFile(torrent.getPath());
					} catch (Exception ex) {
						handler = new MultipleFileHandler();
						handler.parseTorrenFile(torrent.getPath());
					}					
					
					System.out.println("#######################################\n" + torrent.getPath());
					System.out.println(handler.getMetainfo());
				}
			}
		} catch (Exception ex) {
			System.err.println("# MetainforFileHandlerTest: " + ex.getMessage());
		}
		
	}
}
