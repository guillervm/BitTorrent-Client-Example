package es.deusto.ingenieria.ssdd.bitTorrent.graphics;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import es.deusto.ingenieria.ssdd.bitTorrent.client.Client;

public class MyJFrame extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	
	private Client client;

	public MyJFrame() {
		super("BitTorrent Client --- SSDD-5");
		
		JPanelWithBackground bgPanel = new JPanelWithBackground(new ImageIcon("res/loading.gif").getImage());
		bgPanel.setBounds(334, 184, 32, 32);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(700, 400);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.add(bgPanel);
		this.setVisible(true);
		client = new Client();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO update interface
	}
}
