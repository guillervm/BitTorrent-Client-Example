package es.deusto.ingenieria.ssdd.bitTorrent.connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.Timer;

import sun.net.www.protocol.http.HttpURLConnection;
import es.deusto.ingenieria.ssdd.bitTorrent.bencoding.Bencoder;
import es.deusto.ingenieria.ssdd.bitTorrent.core.Download;
import es.deusto.ingenieria.ssdd.bitTorrent.core.Event;
import es.deusto.ingenieria.ssdd.bitTorrent.core.Peer;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.InfoDictionary;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.MetainfoFile;
import es.deusto.ingenieria.ssdd.bitTorrent.util.InputStreamUtils;
import es.deusto.ingenieria.ssdd.bitTorrent.util.MyToolkit;

public class TrackerConnection {
	private String peerId;
	private int port;
	private int compact;
	private Timer update;
	private Timer updateSeeding;
	
	/**
	 * Constructor of the TrackerConnection class.
	 * @param peerId The id of the client peer.
	 * @param port The port used.
	 * @param compact Accept/refuse compact response.
	 */
	public TrackerConnection(String peerId, int port, int compact) {
		this.peerId = peerId;
		this.port = port;
		this.compact = compact;
	}
	
	/**
	 * Asks for peers to the tracker.
	 * @param download The <code>Download</code> object witch peers will be kept.
	 * @return A <code>Collection</code> containing the peers provided by the tracker.
	 */
	public Collection<Peer> askForPeers(final Download download) {
		ArrayList<Peer> peers = new ArrayList<Peer>();
		try {
			String urlString = "";
			urlString += download.getMetainfo().getAnnounce();
			urlString.replaceAll("announce", "file");
			urlString += "?info_hash=";
			urlString += download.getMetainfo().getInfo().getUrlInfoHash();
			urlString += "&peer_id=";
			urlString += peerId;
			urlString += "&port=";
			urlString += port;
			urlString += "&downloaded=";
			urlString += download.getDownloaded();
			urlString += "&uploaded=";
			urlString += download.getUploaded();
			urlString += "&left=";
			urlString += download.getLeft();
			urlString += "&event=";
			urlString += download.getEvent();
			urlString += "&compact=";
			urlString += compact;		
			
			byte[] bytes = null;
			if (urlString.startsWith("http")) { //Tracker host is http
				URL url = new URL(urlString);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream is = connection.getInputStream();
				bytes = InputStreamUtils.inputStreamToByteArray(is);
			} else if (urlString.startsWith("udp")) { //Tracker host is udp
				try {
					DatagramSocket udpSocket = new DatagramSocket();
					udpSocket.setSoTimeout(3000);
					
					String host = download.getMetainfo().getAnnounce();
					host = host.substring(6, host.indexOf(":", 6));
					InetAddress serverHost = InetAddress.getByName(host);
					
					byte[] byteMsg = urlString.getBytes();
					DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, 80);
					udpSocket.send(request);
					
					byte[] buffer = new byte[1024];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					udpSocket.receive(reply);
					bytes = reply.getData();
					
					udpSocket.close();
				} catch (SocketTimeoutException e) {
					System.out.println("# Tracker connection error: the tracker does not respond.");
				}
			}
			
			int updateSeconds = 120;
			if (bytes != null) {
				Bencoder b = new Bencoder();
				HashMap<String, Object> unbencoded = b.unbencodeDictionary(bytes);
				try {
					String peersString = (String)unbencoded.get("peers");
					for (int i = 0; i < peersString.length(); i += 6) {
						InetAddress address = InetAddress.getByAddress(peersString.substring(i, i + 4).getBytes());
						int port = MyToolkit.bytesToPort(peersString.charAt(i + 4), peersString.charAt(i + 5));
						Peer peer = new Peer(address, port, peerId, download);
						peers.add(peer);
					}
					updateSeconds = (int) unbencoded.get("interval");
					if (updateSeconds < 0) {
						updateSeconds = 120;
					} else if (updateSeconds > 180) {
						updateSeconds = 180;
					}
				} catch (ClassCastException ce) {
					@SuppressWarnings("unchecked")
					ArrayList<HashMap<String, Object>> peersReceived = (ArrayList<HashMap<String, Object>>) unbencoded.get("peers");
					for (HashMap<String, Object> p:peersReceived) {
						Peer peer = new Peer(InetAddress.getByName((String)p.get("ip")), (Integer)p.get("port"), (String)p.get("peer id"), download);
						peers.add(peer);
					}
				}
				
				System.out.println(" - " + download.getMetainfo().getInfo().getName());
				System.out.println("    " + peers.size() + " peers found.");
				/*for (Peer p:peers) {
					System.out.println(p.toString());
				}*/
				
				update = new Timer(updateSeconds*1000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						updateTracker(download);
					}
				});
				update.start();
			} else {
				System.out.println("# Tracker connection error: can't connect to the tracker.");
			}
		} catch (IOException e) {
			System.out.println("# Tracker connection error.");
			e.printStackTrace();
		}
		
		return peers;
	}
	
	/**
	 * Sends the info to the tracker.
	 * @param download The download which info is being sent.
	 */
	private void updateTracker(Download download) {
		String urlString = "";
		urlString += download.getMetainfo().getAnnounce();
		urlString.replaceAll("announce", "file");
		urlString += "?info_hash=";
		urlString += download.getMetainfo().getInfo().getUrlInfoHash();
		urlString += "&peer_id=";
		urlString += peerId;
		urlString += "&port=";
		urlString += port;
		urlString += "&downloaded=";
		urlString += download.getDownloaded();
		urlString += "&uploaded=";
		urlString += download.getUploaded();
		urlString += "&left=";
		urlString += download.getLeft();
		urlString += "&event=";
		urlString += download.getEvent();
		urlString += "&compact=";
		urlString += compact;
		
		byte[] bytes = null;
		try {
			if (urlString.startsWith("http")) { //Tracker host is http
				URL url;
				url = new URL(urlString);
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream is = connection.getInputStream();
				bytes = InputStreamUtils.inputStreamToByteArray(is);
			} else if (urlString.startsWith("udp")) { //Tracker host is udp
				try {
					DatagramSocket udpSocket = new DatagramSocket();
					udpSocket.setSoTimeout(3000);
					
					String host = download.getMetainfo().getAnnounce();
					host = host.substring(6, host.indexOf(":", 6));
					InetAddress serverHost = InetAddress.getByName(host);
					
					byte[] byteMsg = urlString.getBytes();
					DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, 80);
					udpSocket.send(request);
					
					byte[] buffer = new byte[1024];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					udpSocket.receive(reply);
					bytes = reply.getData();
					
					udpSocket.close();
				} catch (SocketTimeoutException e) {
					System.out.println("# Tracker connection error: the tracker does not respond.");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int updateSeconds = 120;
		if (bytes != null) {
			Bencoder b = new Bencoder();
			HashMap<String, Object> unbencoded = b.unbencodeDictionary(bytes);
			updateSeconds = (int) unbencoded.get("interval");
			if (updateSeconds < 0) {
				updateSeconds = 120;
			} else if (updateSeconds > 180) {
				updateSeconds = 180;
			}
			update.setDelay(updateSeconds);
		}
	}
	
	/**
	 * Sends the stopped event to the tracker (should be send when closing the client).
	 * @param download Download info
	 */
	public void sendStopped(Download download) {
		String urlString = "";
		urlString += download.getMetainfo().getAnnounce();
		urlString.replaceAll("announce", "file");
		urlString += "?info_hash=";
		urlString += download.getMetainfo().getInfo().getUrlInfoHash();
		urlString += "&peer_id=";
		urlString += peerId;
		urlString += "&port=";
		urlString += port;
		urlString += "&downloaded=";
		urlString += download.getDownloaded();
		urlString += "&uploaded=";
		urlString += download.getUploaded();
		urlString += "&left=";
		urlString += download.getLeft();
		urlString += "&event=";
		urlString += Event.stopped;
		urlString += "&compact=";
		urlString += compact;
		
		try {
			if (urlString.startsWith("http")) { //Tracker host is http
				URL url;
				url = new URL(urlString);
				url.openConnection();
			} else if (urlString.startsWith("udp")) { //Tracker host is udp
				try {
					DatagramSocket udpSocket = new DatagramSocket();
					udpSocket.setSoTimeout(3000);
					
					String host = download.getMetainfo().getAnnounce();
					host = host.substring(6, host.indexOf(":", 6));
					InetAddress serverHost = InetAddress.getByName(host);
					
					byte[] byteMsg = urlString.getBytes();
					DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, 80);
					udpSocket.send(request);
					udpSocket.close();
				} catch (SocketTimeoutException e) {
					System.out.println("# Tracker connection error: the tracker does not respond.");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendTrackerSeeding(final MetainfoFile<InfoDictionary> meta) {
		try {
			String urlString = "";
			urlString += meta.getAnnounce();
			urlString.replaceAll("announce", "file");
			urlString += "?info_hash=";
			urlString += meta.getInfo().getUrlInfoHash();
			urlString += "&peer_id=";
			urlString += peerId;
			urlString += "&port=";
			urlString += port;
			urlString += "&compact=";
			urlString += compact;		
			
			byte[] bytes = null;
			if (urlString.startsWith("http")) { //Tracker host is http
				URL url = new URL(urlString);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream is = connection.getInputStream();
				bytes = InputStreamUtils.inputStreamToByteArray(is);
			} else if (urlString.startsWith("udp")) { //Tracker host is udp
				try {
					DatagramSocket udpSocket = new DatagramSocket();
					udpSocket.setSoTimeout(3000);
					
					String host = meta.getAnnounce();
					host = host.substring(6, host.indexOf(":", 6));
					InetAddress serverHost = InetAddress.getByName(host);
					
					byte[] byteMsg = urlString.getBytes();
					DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, 80);
					udpSocket.send(request);
					
					byte[] buffer = new byte[1024];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					udpSocket.receive(reply);
					bytes = reply.getData();
					
					udpSocket.close();
				} catch (SocketTimeoutException e) {
					System.out.println("# Tracker connection error: the tracker does not respond.");
				}
			}
			
			int updateSeconds = 120;
			if (bytes != null) {
				Bencoder b = new Bencoder();
				HashMap<String, Object> unbencoded = b.unbencodeDictionary(bytes);
				updateSeconds = (int) unbencoded.get("interval");
				
				if (updateSeconds < 0) {
					updateSeconds = 120;
				} else if (updateSeconds > 180) {
					updateSeconds = 180;
				}
				
				updateSeeding = new Timer(updateSeconds*1000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						updateTrackerSeeding(meta);
					}
				});
				updateSeeding.start();
			} else {
				System.out.println("# Tracker connection error: can't connect to the tracker.");
			}
		} catch (IOException e) {
			System.out.println("# Tracker connection error.");
			e.printStackTrace();
		}	
	}	
		
	private void updateTrackerSeeding(MetainfoFile<InfoDictionary> meta) {
		try {
			String urlString = "";
			urlString += meta.getAnnounce();
			urlString.replaceAll("announce", "file");
			urlString += "?info_hash=";
			urlString += meta.getInfo().getUrlInfoHash();
			urlString += "&peer_id=";
			urlString += peerId;
			urlString += "&port=";
			urlString += port;
			urlString += "&compact=";
			urlString += compact;		
			
			byte[] bytes = null;
			if (urlString.startsWith("http")) { //Tracker host is http
				URL url = new URL(urlString);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream is = connection.getInputStream();
				bytes = InputStreamUtils.inputStreamToByteArray(is);
			} else if (urlString.startsWith("udp")) { //Tracker host is udp
				try {
					DatagramSocket udpSocket = new DatagramSocket();
					udpSocket.setSoTimeout(3000);
					
					String host = meta.getAnnounce();
					host = host.substring(6, host.indexOf(":", 6));
					InetAddress serverHost = InetAddress.getByName(host);
					
					byte[] byteMsg = urlString.getBytes();
					DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, 80);
					udpSocket.send(request);
					
					byte[] buffer = new byte[1024];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					udpSocket.receive(reply);
					bytes = reply.getData();
					
					udpSocket.close();
				} catch (SocketTimeoutException e) {
					System.out.println("# Tracker connection error: the tracker does not respond.");
				}
			}
			
			int updateSeconds = 120;
			if (bytes != null) {
				Bencoder b = new Bencoder();
				HashMap<String, Object> unbencoded = b.unbencodeDictionary(bytes);
				updateSeconds = (int) unbencoded.get("interval");
				
				if (updateSeconds < 0) {
					updateSeconds = 120;
				} else if (updateSeconds > 180) {
					updateSeconds = 180;
				}
				
				updateSeeding.setDelay(updateSeconds);
			} else {
				System.out.println("# Tracker connection error: can't connect to the tracker.");
			}
		} catch (IOException e) {
			System.out.println("# Tracker connection error.");
			e.printStackTrace();
		}
	}
}
