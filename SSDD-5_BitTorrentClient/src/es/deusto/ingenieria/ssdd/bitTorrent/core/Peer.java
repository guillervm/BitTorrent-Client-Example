package es.deusto.ingenieria.ssdd.bitTorrent.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.Timer;

import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.BitfieldMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.Handsake;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.InterestedMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.KeepAliveMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.PeerProtocolMessage;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.PieceMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.RequestMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.UnChokeMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.util.DownloadsManager;
import es.deusto.ingenieria.ssdd.bitTorrent.util.StringUtils;
import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class Peer {
	private final int defaultBufferLenght = 10240;
	private final String bitTorrentProtocolStr = "BitTorrent protocol";
	private final int reservedBytesLength = 8;
	private final int keepAliveSeconds = 120;
	private int receiveBufferLenght = 17408;
	
	private InetAddress address;
	private int port;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean alive;
	private boolean chocked;
	private boolean interested;
	private boolean amChocked;
	private boolean amInterested;
	
	private Timer sendKeepAlive;
	
	
	private Download download;
	
	public Peer(final InetAddress address, final int port, final String peerId, final Download download) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Peer.this.address = address;
				Peer.this.port = port;
				Peer.this.download = download;
				alive = true;
				chocked = true;
				interested = false;
				amChocked = true;
				amInterested = false;
				sendKeepAlive = new Timer(keepAliveSeconds*1000, new ActionListener() {		
					@Override
					public void actionPerformed(ActionEvent e) {
						sendKeepAliveMsg();
					}
				});
				receiveBufferLenght = download.getMetainfo().getInfo().getPieceLength() + 1024;

				try {
					byte[] temp;
					socket = new Socket(address, port);
					input = new DataInputStream(socket.getInputStream());
					output = new DataOutputStream(socket.getOutputStream());
					
					// Send handsake message
					sendHandsake(peerId, download.getMetainfo().getInfo().getInfoHash());
					// Read handsake message
					temp = listen(defaultBufferLenght);
					// temp[0] should be 19
					
					byte[] handsakeSHABytes = new byte[20];
					for (int i = 0; i < 20; i++) {
						handsakeSHABytes[i] = temp[i + 1 + bitTorrentProtocolStr.length() + reservedBytesLength];
					}
					if (!StringUtils.toHexString(handsakeSHABytes).equals(StringUtils.toHexString(download.getMetainfo().getInfo().getInfoHash()))) {
						try {
							Peer.this.finalize();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					} else {
						sendKeepAlive.start();
						
						// Read message
						temp = listen(defaultBufferLenght);
						
						// Send bitfield message
						//sendBitfieldMsg();
						
						sendInterestedMsg();
						
						UnChokeMsg u = new UnChokeMsg();
						do {
							temp = listen(defaultBufferLenght);
						} while (u.parseMessage(temp).getId() != 1);//temp[4] != 1);

						for (int i = download.getNextNotDownloadedPieceIndex(); i < download.getNumberOfPieces(); ) {
							int pieceLenght;
							if (i != download.getNumberOfPieces() - 1) {
								pieceLenght = download.getMetainfo().getInfo().getPieceLength();
							} else {
								pieceLenght = download.getLastPieceLenght();
							}
							sendRequesMsg(i, 0, pieceLenght);
							
							temp = listen(receiveBufferLenght);
							PeerProtocolMessage pm = u.parseMessage(temp);
							if (pm instanceof PieceMsg) {
								byte[] content;
								content = Arrays.copyOfRange(((PieceMsg)pm).getPayload(), 8, 8 + pieceLenght);
							
								byte[] generatedHash = ToolKit.generateSHA1Hash(content);
								String generatedHashString = new String(generatedHash);
								generatedHash = generatedHashString.getBytes("ASCII");
								
								boolean correct = true;
								String generated = StringUtils.toHexString(generatedHash);
								// Check the hash of the piece.
								if (!generated.equals(download.getMetainfo().getInfo().getHexStringSHA1().get(i))) {
									correct = false;
								}
								
								// If the hash of the piece is correct, process it.
								if (correct) {
									// Update the configuration file of the download and store this file and the downloaded data into the disk.
									download.pieceDownloaded(i, content.length);
									DownloadsManager.savePiece(i, content, download);
									// Look for the next not downloaded piece.
									i = download.getNextNotDownloadedPieceIndex();
								}
							}
							Thread.sleep(100);
						}
						System.out.println("Donwload finished!! - " + download.getMetainfo().getInfo().getName());
						DownloadsManager.downloadFinished(download);
					}
				} catch (Exception e) {
					//System.err.println("# TCP socket error: " + e.getMessage());
					//e.printStackTrace();
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isChocked() {
		return chocked;
	}

	public void setChocked(boolean chocked) {
		this.chocked = chocked;
	}

	public boolean isInterested() {
		return interested;
	}

	public void setInterested(boolean interested) {
		this.interested = interested;
	}

	public boolean isAmChocked() {
		return amChocked;
	}

	public void setAmChocked(boolean amChocked) {
		this.amChocked = amChocked;
	}

	public boolean isAmInterested() {
		return amInterested;
	}

	public void setAmInterested(boolean amInterested) {
		this.amInterested = amInterested;
	}

	@Override
	public String toString() {
		return address.toString() + ":" + port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Peer) {
			return this.address.equals(((Peer)obj).getAddress()) && this.port == ((Peer)obj).getPort();
		}
		return false;
	}
	
	private void sendHandsake(String peerId, byte[] hash) {
		try {
			Handsake handsake = new Handsake();
			handsake.setPeerId(peerId);
			handsake.setInfoHash(hash);
			output.write(handsake.getBytes());
			//System.out.println(" - Sent handsake to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (handsake): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void sendBitfieldMsg() {
		try {
			BitfieldMsg bitfieldMsg = new BitfieldMsg(ToolKit.bitSetToBytes(download.getBitSet()));
			output.write(bitfieldMsg.getBytes());
			//System.out.println(" - Sent bitfield to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (bitfield): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void sendInterestedMsg() {
		try {
			InterestedMsg interestedMsg = new InterestedMsg();
			output.write(interestedMsg.getBytes());
			//System.out.println(" - Sent interested to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (interested): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void sendRequesMsg(int index, int begin, int length) {
		try {
			RequestMsg requestMsg = new RequestMsg(index, begin, length);
			output.write(requestMsg.getBytes());
			//System.out.println(" - Sent request to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (request): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void sendKeepAliveMsg() {
		try {
			KeepAliveMsg keepAliveMsg = new KeepAliveMsg();
			output.write(keepAliveMsg.getBytes());
			//System.out.println(" - Sent keepAlive to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (keep alive): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private byte[] listen(int length) {
		final byte[] buffer = new byte[length];
		try {
			while (input.available() <= 0) {}
			input.read(buffer);
		} catch (IOException e) {
			System.err.println("# TCP Listen IO error: " + e.getMessage());
		}
		return buffer;
	}
	
	@Override
	protected void finalize() throws Throwable {
		sendKeepAlive.stop();
		socket.close();
		super.finalize();
	}
}
