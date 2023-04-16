package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
	\file ImagePanel.java
	\brief Plik z klasą ImagePanel.
*/

/**
	\brief Klasa panelu, która rysuje klatkę.
*/
public class ImagePanel extends JPanel {
	//! Setter klatki.
    public void setImage(BufferedImage image) {
        this.image = image;
    }

	//! Klatka do wyświetlenia.
    private BufferedImage image = null;

	//! Przeciążenie funkcji paintComponent(), która rysuję klatkę na panelu.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
}
