package es.deusto.ingenieria.ssdd.bitTorrent.connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.bitTorrent.core.RequestPeer;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.InfoDictionary;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.MetainfoFile;
import es.deusto.ingenieria.ssdd.bitTorrent.util.FileUtils;
import es.deusto.ingenieria.ssdd.bitTorrent.util.StringUtils;

public class UploadConnection {
	private ServerSocket socket;
	private ArrayList<MetainfoFile<InfoDictionary>> finishedDownloads;
	private int port;
	private final int defaultBufferLenght = 10240;
	private DataInputStream input;
	private final String bitTorrentProtocolStr = "BitTorrent protocol";
	private final int reservedBytesLength = 8;
	private String peerId;
	private static FileUtils fileUtils;
	
	public UploadConnection(final String peerId, final int firstPort, final int lastPort) {
		fileUtils = new FileUtils();
		this.peerId = peerId;
		finishedDownloads = new ArrayList<MetainfoFile<InfoDictionary>>();
		
		for (int i = firstPort; i <= lastPort; i++) {
			try {
				port = i;
				socket = new ServerSocket(i);
				break;
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Socket connectionSocket;
						connectionSocket = socket.accept();
						input = new DataInputStream(connectionSocket.getInputStream());
						byte[] temp = listen();
						byte[] handsakeSHABytes = new byte[20];
						for (int i = 0; i < 20; i++) {
							handsakeSHABytes[i] = temp[i + 1 + bitTorrentProtocolStr.length() + reservedBytesLength];
						}
						boolean isValid = false;
						MetainfoFile<InfoDictionary> d = null;
						for (MetainfoFile<InfoDictionary> m:finishedDownloads) {
							if (StringUtils.toHexString(handsakeSHABytes).equals(StringUtils.toHexString(m.getInfo().getInfoHash()))) {
								isValid = true;
								d = m;
							}
						}
						if (isValid && d != null) {
							new RequestPeer(UploadConnection.this.peerId, d, connectionSocket.getRemoteSocketAddress());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	public void addFinishedDownload(MetainfoFile<InfoDictionary> download) {
		if (!finishedDownloads.contains(download)) {
			finishedDownloads.add(download);
			System.out.println(" - Seeding " + download.getInfo().getName());
		}
	}
	
	public int getPort() {
		return port;
	}
	
	private byte[] listen() {
		final byte[] buffer = new byte[defaultBufferLenght];
		try {
			while (input.available() <= 0) {}
			input.read(buffer);
		} catch (IOException e) {
			System.err.println("# TCP Listen IO error: " + e.getMessage());
		}
		return buffer;
	}
	
	public static byte[] readPart(String file, int piece, int offset, int pieceSize, int size) {
		return fileUtils.readPart(file, piece, offset, pieceSize, size);
	}
	
	@Override
	protected void finalize() throws Throwable {
		socket.close();
		super.finalize();
	}
}
