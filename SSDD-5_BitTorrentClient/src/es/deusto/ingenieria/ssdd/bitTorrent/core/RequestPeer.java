package es.deusto.ingenieria.ssdd.bitTorrent.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.BitSet;

import javax.swing.Timer;

import es.deusto.ingenieria.ssdd.bitTorrent.connection.UploadConnection;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.InfoDictionary;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.MetainfoFile;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.BitfieldMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.Handsake;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.InterestedMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.KeepAliveMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.PieceMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.RequestMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.peer.protocol.messages.UnChokeMsg;
import es.deusto.ingenieria.ssdd.bitTorrent.util.ToolKit;

public class RequestPeer {
	private MetainfoFile<InfoDictionary> meta;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean chocked;
	private boolean alive = true;
	private final int defaultBufferLenght = 10240;
	private final int keepAliveSeconds = 120;
	private Timer sendKeepAlive;
	
	public RequestPeer(String peerId, MetainfoFile<InfoDictionary> meta, SocketAddress address) {
		this.meta = meta;
		chocked = true;
		sendKeepAlive = new Timer(keepAliveSeconds*1000, new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!alive) {
					try {
						finalize();
					} catch (Throwable e1) {
						e1.printStackTrace();
					}
				}
				sendKeepAliveMsg();
				alive = false;
			}
		});

		try {
			byte[] temp;
			socket = new Socket();
			socket.bind(address);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			
			// Send handsake message
			sendHandsake(peerId, this.meta.getInfo().getInfoHash());
			
			sendKeepAlive.start();
				
			boolean bitfieldSent = false;
			
			// If uninterested drop connection else unchoke the requestpeer and start receiving requests
			
			InterestedMsg interestedMsg = new InterestedMsg();
			int msgId;
			while (true) {
				temp = listen();
				if (ToolKit.bigEndianBytesToInt(temp, 0) == 0) {
					// Keep alive message
					alive = true;
				} else {
					msgId = interestedMsg.parseMessage(temp).getId();
					if (msgId != 3) {
						if (chocked && msgId == 2) {
							chocked = false;
							sendUnchokeMsg();
						} else if (chocked && !bitfieldSent) {
							sendBitfieldMsg();
							bitfieldSent = true;
						}
						if (!chocked) {
							if (msgId == 6) {
								RequestMsg requestMsg = (RequestMsg) interestedMsg.parseMessage(temp);
								int index = requestMsg.getPayload()[0];
								int offset = requestMsg.getPayload()[1];
								int lenght = requestMsg.getPayload()[2];
								
								byte[] content = UploadConnection.readPart(meta.getInfo().getName(), index, offset, meta.getInfo().getPieceLength(), lenght);
								PieceMsg pieceMsg = new PieceMsg(index, offset, content);
								sendPieceMsg(pieceMsg);
							}
						}
					}
					else {
						try {
							// Not interested, drop connection
							finalize();
						} catch (Throwable e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			//System.err.println("# TCP socket error: " + e.getMessage());
			//e.printStackTrace();
		}
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
	
	private void sendUnchokeMsg() {
		try {
			UnChokeMsg unchokeMsg = new UnChokeMsg();
			output.write(unchokeMsg.getBytes());
			//System.out.println(" - Sent unchoke to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (unchoke): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void sendBitfieldMsg() {
		try {
			BitSet bitSet = new BitSet((int) Math.ceil((float)meta.getInfo().getLength()/(float)meta.getInfo().getPieceLength()));
			bitSet.set(0, bitSet.length());
			BitfieldMsg bitfieldMsg = new BitfieldMsg(ToolKit.bitSetToBytes(bitSet));
			output.write(bitfieldMsg.getBytes());
			//System.out.println(" - Sent bitfield to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (bitfield): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void sendPieceMsg(PieceMsg pieceMsg) {
		try {
			output.write(pieceMsg.getBytes());
			//System.out.println(" - Sent piece to '" + address.getHostAddress() + ":" + request.getPort() + "' -> " + new String(request.getData()));
		} catch (IOException e) {
			System.err.println("# TCP Socket error (piece): " + e.getMessage());
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
	
	@Override
	protected void finalize() throws Throwable {
		socket.close();
		super.finalize();
	}
}
