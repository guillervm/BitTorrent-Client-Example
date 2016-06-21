package es.deusto.ingenieria.ssdd.bitTorrent.graphics;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * This class draws an image on the background of a <code>JPanel</code>.
 */
public class JPanelWithBackground extends JPanel
{
	private static final long serialVersionUID = 1L;
	private Image imagen;

	public JPanelWithBackground()
	{
	}

	/**
	 * Constructor que pone la imagen cuya ruta recibe como parámetro como fondo
	 * 
	 * @param nombreImagen
	 *          (String)
	 */
	public JPanelWithBackground(String nombreImagen)
	{
		if (nombreImagen != null)
		{
			imagen = new ImageIcon(getClass().getResource(nombreImagen)).getImage();
		}
	}

	/**
	 * Constructor que pone la imagen recibida como parámetro como fondo
	 * 
	 * @param imagenInicial
	 *          (Image)
	 */
	public JPanelWithBackground(Image imagenInicial)
	{
		if (imagenInicial != null)
		{
			imagen = imagenInicial;
		}
	}

	/**
	 * Método que pone la imagen cuya ruta recibe como parámetro como fondo
	 * 
	 * @param nombreImagen
	 *          (String)
	 */
	public void setImagen(String nombreImagen)
	{
		if (nombreImagen != null)
		{
			imagen = new ImageIcon(getClass().getResource(nombreImagen)).getImage();
		} else
		{
			imagen = null;
		}
		repaint();
	}

	/**
	 * Método que pone la imagen recibida como parámetro como fondo
	 * 
	 * @param nuevaImagen
	 *          (Image)
	 */
	public void setImagen(Image nuevaImagen)
	{
		imagen = nuevaImagen;
		repaint();
	}

	/**
	 * Método que en caso de haber una imagen la añade y pone el fondo
	 * transparente. Si no hay imagen pone el fondo opaco
	 * 
	 * @param g
	 *          (Graphics)
	 */
	@Override
	public void paint(Graphics g)
	{
		if (imagen != null)
		{
			g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
			setOpaque(false);
		} else
		{
			setOpaque(true);
		}
		super.paint(g);
	}
}
