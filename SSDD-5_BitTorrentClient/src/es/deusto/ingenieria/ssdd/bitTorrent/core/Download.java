package es.deusto.ingenieria.ssdd.bitTorrent.core;

import java.util.ArrayList;
import java.util.BitSet;

import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.MetainfoFile;

public class Download {
	private final float minPercentageUpload = 0.1f;
	
	private ArrayList<Integer> downloadedParts;
	private BitSet bitSet;
	private MetainfoFile<?> metainfo;
	
	private Event event;
	private int uploaded;
	private int downloaded;
	private int left;
	private int lastPieceLenght;
	private int numberOfPieces;
	
	public Download(ArrayList<Integer> downloadedParts, MetainfoFile<?> metainfo) {
		this.downloadedParts = downloadedParts;
		this.metainfo = metainfo;
		bitSet = new BitSet(this.downloadedParts.size() - 1);
		uploaded = downloadedParts.get(downloadedParts.size() - 1);
		downloaded = 0;
		for (int i = 0; i < downloadedParts.size() - 1; i++) {
			if (downloadedParts.get(i) != 0) {
				downloaded++;
				bitSet.set(i);
			}
		}
		left = metainfo.getInfo().getPieceLength() - downloaded;
		if (left == 0) {
			event = Event.completed;
		} else {
			event = Event.started;
		}
		numberOfPieces = (int) Math.ceil((float)metainfo.getInfo().getLength()/(float)metainfo.getInfo().getPieceLength());
		lastPieceLenght = metainfo.getInfo().getLength() - (numberOfPieces - 1) * metainfo.getInfo().getPieceLength();
	}

	/**
	 * Calculates the downloaded percentage of the file.
	 * @return The downloaded percentage of the file.
	 */
	public float getDownloadedPercentage() {
		return 100 * downloaded / (downloaded + left);
	}
	
	/**
	 * Indicates if the upload percentage is satisfied or not.
	 * @return <code>true</code> if it is satisfied.</br><code>false</code> if it is not satisfied.
	 */
	public boolean isUploadPercentageSatisfied() {
		return uploaded >= (downloaded + left) * minPercentageUpload;
	}

	public ArrayList<Integer> getDownloadedParts() {
		return downloadedParts;
	}

	public void setDownloadedParts(ArrayList<Integer> downloadedParts) {
		this.downloadedParts = downloadedParts;
		this.bitSet = new BitSet(this.downloadedParts.size() - 1);
		for (int i = 0; i < downloadedParts.size() - 1; i++) {
			if (downloadedParts.get(i) != 0) {
				downloaded++;
				bitSet.set(i);
			}
		}
	}

	public BitSet getBitSet() {
		return bitSet;
	}

	public MetainfoFile<?> getMetainfo() {
		return metainfo;
	}

	public void setMetainfo(MetainfoFile<?> metainfo) {
		this.metainfo = metainfo;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public int getUploaded() {
		return uploaded;
	}

	public void setUploaded(int uploaded) {
		this.uploaded = uploaded;
	}

	public int getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}
	
	public int getPieceSize() {
		return metainfo.getInfo().getPieceLength();
	}

	public int getLastPieceLenght() {
		return lastPieceLenght;
	}

	public int getNumberOfPieces() {
		return numberOfPieces;
	}

	public void setNumberOfPieces(int numberOfPieces) {
		this.numberOfPieces = numberOfPieces;
	}
	
	/**
	 * Returns the index of the first piece not downloaded yet.
	 * @return The index of the first not downloaded piece.
	 */
	public int getNextNotDownloadedPieceIndex() {
		return bitSet.nextClearBit(0);
	}
	
	/**
	 * This function should be called when a piece is correctly downloaded. It updates the configuration file of the download.
	 * @param index The index of the donwloaded piece.
	 */
	public void pieceDownloaded(int index, int size) {
		downloadedParts.set(index, 1);
		bitSet.set(index);
		downloaded += size;
		left -= size;
		if (left <= 0) {
			event = Event.completed;
		}
	}
 }
