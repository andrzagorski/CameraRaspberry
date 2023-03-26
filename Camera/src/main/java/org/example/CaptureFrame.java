package org.example;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class CaptureFrame {

private static IplImage img = null;

    static void SaveImage(CanvasFrame window) {
        if (img!=null) {
            ImgSaver.saveImg(window,img);
        }
    }

    static void Capture(FrameGrabber[] cam, Frame[] GrabbedFrame, AtomicBoolean priorityQueue, CanvasFrame window,ImagePanel right, int prevWidth, int prevHeight, int MAX_WIDTH, int MAX_HEIGHT, Object lock) {
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
                    BufferedImage bufferedImage = Java2DFrameUtils.toBufferedImage(img);


                    right.setPreferredSize(new Dimension(window.getWidth()/2,window.getHeight()/2));
                    //right.setSize(new Dimension(bufferedImage.getWidth(),bufferedImage.getHeight()));
                    System.out.println(bufferedImage.getWidth()+" i "+bufferedImage.getHeight());
                    right.setImage(bufferedImage);
                    window.repaint();
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

