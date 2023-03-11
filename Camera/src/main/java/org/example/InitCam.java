package org.example;

import org.bytedeco.javacv.FrameGrabber;

public  class InitCam {

    public static void initialize(FrameGrabber cam, int width, int height) throws InterruptedException, FrameGrabber.Exception {
        cam.setImageWidth(width);
        cam.setImageHeight(height);
        cam.start();
        Thread.sleep(200);
    }
}
