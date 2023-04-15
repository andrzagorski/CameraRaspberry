package org.example;

import org.bytedeco.javacv.FrameGrabber;
import static java.lang.Thread.sleep;


public  class InitCam {

    public static void initialize(FrameGrabber cam, int width, int height) throws InterruptedException, FrameGrabber.Exception {
        cam.setImageWidth(width);
        cam.setImageHeight(height);
        cam.start();
        sleep(200);
    }
}
