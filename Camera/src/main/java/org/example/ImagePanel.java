package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class ImagePanel extends JPanel {
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    private BufferedImage image = null;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
}
