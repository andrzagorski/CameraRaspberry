package org.example;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.util.PriorityQueue;

public class CaptureFrame {

    static void Capture(FrameGrabber[] cam, Frame[] GrabbedFrame, JComboBox<String> listOfCameras, PriorityQueue<String> priorityQueue, CanvasFrame window, int prevWidth, int prevHeight, int MAX_WIDTH, int MAX_HEIGHT, Object lock) {
        Runnable runnableCapturingImage = new Runnable() {
            @Override
            public void run() {
                priorityQueue.add("Device Reservation"); // bo nie wejdzie do synchronized bo blokuje go inny watek

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
                    IplImage img = converter.convert(frame);

                    ImgSaver.saveImg(window,img);

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

                    priorityQueue.remove();
                    lock.notifyAll(); // nie dziala - watek nie startuje....


                    // jButtonChooseCamera.doClick();
                }
            }
        };
        try{new Thread(runnableCapturingImage).start();}
        catch (Exception exception){
        }
    }

    }

