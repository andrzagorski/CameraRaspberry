package org.example;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.io.File;

/**
	\file ImgSaver.java
	\brief Plik z klasą ImgSaver.
*/

/**
	\brief Klasa abstrachująca zapisu obrazu przechwyconego z kamery.
*/
public class ImgSaver {
	//! Metoda statyczna wyświetlająca okno do zapisu przechwyconego obrazu podanego w argumencie. 
    static void saveImg(CanvasFrame window, IplImage img) {

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String sciezka = file.toString();
            if(sciezka.contains(".jpg") || sciezka.contains(".png")){
                opencv_imgcodecs.cvSaveImage(file.toString(), img);
            }
            else
            opencv_imgcodecs.cvSaveImage(file.toString()+".jpg", img);
        }
    }
}
