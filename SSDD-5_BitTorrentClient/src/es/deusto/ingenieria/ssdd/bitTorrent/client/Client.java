package es.deusto.ingenieria.ssdd.bitTorrent.client;

import es.deusto.ingenieria.ssdd.bitTorrent.util.DownloadsManager;
import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;


public class Client {
	private DownloadsManager downloadsManager;
	private LocalObservable observable;
	
	public Client() {
		downloadsManager = new DownloadsManager();
		System.out.println("Checking downloads...");
		downloadsManager.checkStartedDownloads();
		System.out.println("Getting peers...");
		downloadsManager.startDownloads();
	}
}
