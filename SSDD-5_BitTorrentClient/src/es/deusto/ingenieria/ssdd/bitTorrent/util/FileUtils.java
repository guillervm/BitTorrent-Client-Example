package es.deusto.ingenieria.ssdd.bitTorrent.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import es.deusto.ingenieria.ssdd.bitTorrent.core.Download;
import es.deusto.ingenieria.ssdd.bitTorrent.metainfo.MetainfoFile;

public class FileUtils {
	private final String downloadsFolder = "downloads";
	private final String downloadsInfoExtension = ".tmp";
	
	/**
	 * Creates the file according to the matainfo of the handler.
	 * @param metainfo The metainfo handler
	 * @return <code>true</code> if creates correctly<br><code>false</code> if there is any error
	 */
	public boolean createFile(MetainfoFile<?> metainfo) {
		try {
			//Create an empty file of the size of the file download
            RandomAccessFile file = new RandomAccessFile(downloadsFolder + "/" + metainfo.getInfo().getName(), "rw");
            file.setLength(metainfo.getInfo().getLength());
            file.close();
            
            //Create downloaded parts info file
            ArrayList<Integer> downloadedParts = new ArrayList<Integer>();
            for (int i = 0; i < Math.ceil((float)metainfo.getInfo().getLength()/(float)metainfo.getInfo().getPieceLength()) + 1; i++) { //Number of parts + 1 int to save the number of uploaded parts
            	downloadedParts.add(0);
            }
            FileOutputStream fos = new FileOutputStream(downloadsFolder + "/" + metainfo.getInfo().getName() + downloadsInfoExtension);
        	ObjectOutputStream oos = new ObjectOutputStream(fos);
        	oos.writeObject(downloadedParts);
        	oos.close();
            return true;
       } catch (Exception e) {
            System.err.println(e);
       }
		return false;
	}
	
	/**
	 * Saves the downloaded part to the disk and updates the downloaded parts file.
	 * @param file Name of the file
	 * @param part Part index
	 * @param content The downloaded bytes
	 * @param download The download object
	 */
	public void savePiece(int part, byte[] content, Download download) {
		writePart(download.getMetainfo().getInfo().getName(), part, download.getMetainfo().getInfo().getPieceLength(), content);
		updateDownloadedParts(download.getMetainfo().getInfo().getName(), download);
	}
	
	/**
	 * Writes the downloaded part in the disk.
	 * @param file Name of the file
	 * @param part Part index
	 * @param content The downloaded bytes
	 */
	private void writePart(String file, int part, int pieceSize, byte[] content) {
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(new File(downloadsFolder + "/" + file), "rw");
			f.seek(part*pieceSize); // Seek ahead
			f.write(content);
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the part from the file.
	 * @param file Name of the file
	 * @param piece Piece index
	 * @param offset The begin of the requested bytes inside the piece
	 * @param pieceSize The size of the pieces
	 * @param size The requested length
	 */
	public byte[] readPart(String file, int piece, int offset, int pieceSize, int size) {
		byte[] content = new byte[size];
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(new File(downloadsFolder + "/" + file), "r");
			f.seek(piece*pieceSize + offset); // Seek ahead
			f.read(content);
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * Updates the downloaded parts file.
	 * @param file Name of the file
	 * @param download Download object
	 */
	private void updateDownloadedParts(String file, Download download) {
    	try {
    		FileOutputStream fos = new FileOutputStream(downloadsFolder + "/" + file + downloadsInfoExtension);
        	ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(download.getDownloadedParts());
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes the configuration file. This function should be called when the download has been finished.
	 * @param file Name of the file
	 */
	public void deleteTmpFile(String file) {
		File f = new File(downloadsFolder + "/" + file + downloadsInfoExtension);
		f.delete();
	}
}
