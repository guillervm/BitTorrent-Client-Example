package es.deusto.ingenieria.ssdd.bitTorrent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import es.deusto.ingenieria.ssdd.bitTorrent.connection.TrackerConnection;
import es.deusto.ingenieria.ssdd.bitTorrent.connection.UploadConnection;
import es.deusto.ingenieria.ssdd.bitTorrent.core.Download;
import es.deusto.ingenieria.ssdd.bitTorrent.core.Peer;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.InfoDictionary;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.MetainfoFile;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler.MetainfoFileHandler;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler.MultipleFileHandler;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.handler.SingleFileHandler;

public class DownloadsManager {
	private final String downloadsFolder = "downloads";
	private final String downloadsInfoExtension = ".tmp";
	
	private static FileUtils fileUtils;
	
	private static UploadConnection uploadConnection;
	private TrackerConnection trackerConnection;
	private String peerId;
	
	private HashMap<String, Download> currentDownloads;
	private ArrayList<ArrayList<Peer>> peers;
	
	int firstPort = 6881;
	int lastPort = 6889;
	
	public DownloadsManager() {
		fileUtils = new FileUtils();
		peerId = ToolKit.generatePeerId();
		uploadConnection = new UploadConnection(peerId, firstPort, lastPort);
		trackerConnection = new TrackerConnection(peerId, uploadConnection.getPort(), 0);
	}

	/**
	 * Checks the started downloads. If there is any .torrent file in <i>torrent</i> folder which download hasn't started,
	 * the user will be asked if he wants to start it.
	 */
	@SuppressWarnings("unchecked")
	public void checkStartedDownloads() {
		try {
			currentDownloads = new HashMap<String, Download>();
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
					
					if (isStarted(handler.getMetainfo().getInfo().getName())) {
						// Load downloaded pieces info file
						if (loadStartedDownload(handler.getMetainfo())) {
							System.out.println("Resuming download: " + handler.getMetainfo().getInfo().getName());
						} else {
							System.out.println("Can't resume download: " + handler.getMetainfo().getInfo().getName());
						}
					} else if (!fileDownloaded(handler.getMetainfo().getInfo().getName())) {
						int ans = JOptionPane.showConfirmDialog(null,
							    "Would you like to start downloading '" + handler.getMetainfo().getInfo().getName() + "'?",
							    "Download",
							    JOptionPane.YES_NO_OPTION);
						if (ans == JOptionPane.YES_OPTION) {
							// Create empty file to reserve space
							if (fileUtils.createFile(handler.getMetainfo())) {
								System.out.println("Starting download: " + handler.getMetainfo().getInfo().getName());
								loadStartedDownload(handler.getMetainfo());
							} else {
								System.out.println("Can't start download: " + handler.getMetainfo().getInfo().getName());
							}
						}
					} else {
						// Seed
						uploadConnection.addFinishedDownload((MetainfoFile<InfoDictionary>) handler.getMetainfo());
						trackerConnection.sendTrackerSeeding((MetainfoFile<InfoDictionary>) handler.getMetainfo());
					}
				}
			}
		} catch (Exception ex) {
			System.err.println("# MetainforFileHandlerTest: " + ex.getMessage());
		}
	}

	/**
	 * Checks if the download of the file given by parameter is started.
	 * @param name File's name
	 * @return true if the download is started<br>false if the download isn't started
	 */
	private boolean isStarted(String name) {
		try {			
			File folder = new File(downloadsFolder);
			
			if (folder.isDirectory()) {
				for (File file : folder.listFiles()) {
					try {
						if (file.getName().equalsIgnoreCase(name + downloadsInfoExtension)) {
							return true;
						}
					} catch (Exception ex) {
						System.err.println("# MetainforFileHandlerTest: " + ex.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			System.err.println("# MetainforFileHandlerTest: " + ex.getMessage());
		}
		
		return false;
	}
	
	/**
	 * Checks if the file given by parameter is already downloaded.
	 * @param name File's name
	 * @return true if the download is started<br>false if the download isn't started
	 */
	private boolean fileDownloaded(String name) {
		try {			
			File folder = new File(downloadsFolder);
			
			if (folder.isDirectory()) {
				for (File file : folder.listFiles()) {
					try {
						if (file.getName().equalsIgnoreCase(name)) {
							return true;
						}
					} catch (Exception ex) {
						System.err.println("# MetainforFileHandlerTest: " + ex.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			System.err.println("# MetainforFileHandlerTest: " + ex.getMessage());
		}
		
		return false;
	}
	
	/**
	 * Loads the downloaded parts file for every started download.
	 * @param metainfo The metainfo of the file
	 * @return true if loads correctly<br>false if there is any error
	 */
	@SuppressWarnings("unchecked")
	private boolean loadStartedDownload(MetainfoFile<?> metainfo) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(downloadsFolder + "/" + metainfo.getInfo().getName() + downloadsInfoExtension);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Download dw = new Download((ArrayList<Integer>) ois.readObject(), metainfo);
			currentDownloads.put(metainfo.getInfo().getName(), dw);
			ois.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Saves a downloaded piece of a file to the output file.
	 * @param file File's name
	 * @param pieceIndex The index of the piece
	 * @param content The content to write
	 * @param download The download object
	 */
	public static void savePiece(int pieceIndex, byte[] content, Download download) {
		fileUtils.savePiece(pieceIndex, content, download);
	}
	
	/**
	 * Deletes the temporal configuration file created in the download folder.
	 * @param download The download which configuration file should be deleted.
	 */
	@SuppressWarnings("unchecked")
	public static void downloadFinished(Download download) {
		fileUtils.deleteTmpFile(download.getMetainfo().getInfo().getName());
		uploadConnection.addFinishedDownload((MetainfoFile<InfoDictionary>) download.getMetainfo());
	}
	
	/**
	 * Starts/resumes the downloads
	 */
	public void startDownloads() {
		peers = new ArrayList<ArrayList<Peer>>();
		final Iterator<Download> i = currentDownloads.values().iterator();
		while (i.hasNext()) {
			startDownload(i.next());
		}
	}
	
	/**
	 * Connects to the tracker and asks for peers
	 * @param download The <code>Download</code> object witch download is being started/resumed
	 */
	private void startDownload(Download download) {
		peers.add((ArrayList<Peer>) trackerConnection.askForPeers(download));
	}
	
	@Override
	protected void finalize() throws Throwable {
		// Send the stopped event to the tracker when closing the client
		for (String s:currentDownloads.keySet()) {
			trackerConnection.sendStopped(currentDownloads.get(s));
		}
		super.finalize();
	}
}
