package org.example;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CaptureFrame {
private static IplImage img = null;

    static void SaveImage(CanvasFrame window) {
        if (img!=null) {
            ImgSaver.saveImg(window,img);
        }
    }
    static void Capture(FrameGrabber[] cam, Frame[] GrabbedFrame, AtomicBoolean priorityQueue, CanvasFrame window,JPanel right, int prevWidth, int prevHeight, int MAX_WIDTH, int MAX_HEIGHT, Object lock) {
        right.removeAll();
        CanvasFrame canvasFrame = new CanvasFrame("xyz");
        canvasFrame.setVisible(false);
        right.add(canvasFrame.getCanvas());
        Runnable runnableCapturingImage = new Runnable() {
            @Override
            public void run() {

                priorityQueue.set(true);

                synchronized (lock){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    //grabbing an image.
                    try {
                        InitCam.initialize(cam[0],MAX_WIDTH , MAX_HEIGHT);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    } catch (FrameGrabber.Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    Frame frame = null;
                    try {
                        frame = cam[0].grab();
                        GrabbedFrame[0] =frame;
                    } catch (FrameGrabber.Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
                    img = converter.convert(frame);

                    canvasFrame.showImage(frame);
                    canvasFrame.setCanvasSize(640,480);
                    window.revalidate();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    try {
                        InitCam.initialize(cam[0], prevWidth, prevHeight);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    } catch (FrameGrabber.Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    priorityQueue.set(false);
                    lock.notifyAll();
                }
            }
        };
        try{new Thread(runnableCapturingImage).start();}
        catch (Exception exception){
        }
    }

    }

