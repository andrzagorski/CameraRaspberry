package org.example;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.example.Main.*;

/**
	\file CaptureVideo.java
	\brief Plik z klasą CaptureVideo.
*/

/**
	\brief Klasa abstrachująca przechwytywanie wideo z kamery.
*/
public class CaptureVideo {
    public static boolean httpstream = false;
    private static HttpStreamServer httpStreamService= new HttpStreamServer(OCT_A,OCT_B,OCT_C,OCT_D);
    private static Thread httpStreamThread;

    static VideoCapture videoCapture;
    static Timer tmrVideoProcess;

    public static void  startThreadStream() {
        httpStreamThread = new Thread(httpStreamService);
        httpStreamThread.start();
    }
    public static void StopStreamServer() {
        httpstream=false;

        if (httpStreamThread != null && httpStreamThread.isAlive()) {
            httpStreamThread.interrupt();
        }
    }

    public static BufferedImage toBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }


    static void Capture(FrameGrabber[] cam, AtomicBoolean priorityQueue, CanvasFrame window,JPanel left, int prevWidth, int prevHeight, Object lock) {
        left.removeAll();
        CanvasFrame canvasFrame = new CanvasFrame("video");
        canvasFrame.setVisible(false);
        left.add(canvasFrame.getCanvas());
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();


        Runnable runnableCapturingVideo = new Runnable() {
            @Override
            public void run() {

                if (cam[0] != null) { // if camera is already allocated -> case when camera is switched to another
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
                cam[0] = new OpenCVFrameGrabber(0);

                synchronized (lock) {
                    try {
                        InitCam.initialize(cam[0], prevWidth, prevHeight);
                    } catch (InterruptedException | FrameGrabber.Exception ex) {
                        ex.printStackTrace();
                    }

                    while (true) {

                        while(priorityQueue.get()) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Frame frame = null;
                        try {
                            frame = cam[0].grab();
                        } catch (FrameGrabber.Exception ex) {
                            throw new RuntimeException(ex);
                        }


                        canvasFrame.showImage(frame);
                        canvasFrame.setCanvasSize(640,480);
                        window.revalidate();

                        IplImage img2 = converter.convert(frame);

                        if(httpstream) {
                            httpStreamService.imag = toBufferedImage(img2);
                        }

                        try {
                            Thread.sleep(50);
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
