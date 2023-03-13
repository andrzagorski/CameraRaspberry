package org.example;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.swing.*;
import java.util.PriorityQueue;

public class CaptureVideo {
    static void Capture(FrameGrabber[] cam, JComboBox<String> listOfCameras, PriorityQueue<String> priorityQueue, CanvasFrame window, int prevWidth, int prevHeight, Object lock) {
        Runnable runnableCapturingVideo = new Runnable() {
            @Override
            public void run() {

                if (cam[0] != null) { // if camera is arleady allocated -> case when camera is switched to another
                    try {
                        cam[0].close();
                    } catch (FrameGrabber.Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        cam[0].release();
                    } catch (FrameGrabber.Exception ex) {
                        throw new RuntimeException(ex);
                    }

                }

                cam[0] = new OpenCVFrameGrabber(listOfCameras.getSelectedIndex());

                synchronized (lock) {

                    while(!(priorityQueue.isEmpty())) {
                        System.out.println("W");
                        try {
                            lock.wait();
                            System.out.println("A");

                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    System.out.println("T");

                    try {

                        InitCam.initialize(cam[0], prevWidth, prevHeight);
                    } catch (InterruptedException | FrameGrabber.Exception ex) {
                        ex.printStackTrace();
                    }
                    while (priorityQueue.isEmpty()) {
                        Frame frame = null;
                        try {
                            frame = cam[0].grab();
                        } catch (FrameGrabber.Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        window.showImage(frame);
                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }

        };
        new Thread(runnableCapturingVideo).start();
    }

    }
