package org.example;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.io.File;

public class ImgSaver {

    static void saveImg(CanvasFrame window, IplImage img) {

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            opencv_imgcodecs.cvSaveImage(file.toString(), img);
        }

    }

}
