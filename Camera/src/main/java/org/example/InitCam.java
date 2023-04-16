package org.example;

import org.bytedeco.javacv.FrameGrabber;
import static java.lang.Thread.sleep;

/**
	\file InitCam.java
	\brief Plik z klasą InitCam.
*/

/**
	\brief Klasa abstrachująca proces inicjalizacji kamery.
*/
public  class InitCam {
	//! Metoda inicjalizująca pracę kamery.
    public static void initialize(FrameGrabber cam, int width, int height) throws InterruptedException, FrameGrabber.Exception {
        cam.setImageWidth(width);
        cam.setImageHeight(height);
        cam.start();
        sleep(200);
    }
}
