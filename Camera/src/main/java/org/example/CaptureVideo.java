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
	\brief Klasa abstrachująca przechwytywanie wideo z kamery

	zawierająca narzędzia niezbędne do transmisji obrazu kamery na serwer HTTP.
*/
public class CaptureVideo {
	//! Zmienna pokazująca, czy serwer http wystartował
    public static boolean httpstream = false;

	//! Obiekt klasy HttpStreamServer do transmisji na serwer HTTP.
    private static HttpStreamServer httpStreamService= null;

	//! Główny wątek transmisji HTTP.
    private static Thread httpStreamThread;

    public static IplImage img2= null;


	//! Funkcja rozpoczynająca transmisję HTTP.
    public static void  startThreadStream() {
        httpStreamService= new HttpStreamServer(OCT_A,OCT_B,OCT_C,OCT_D);
        httpStreamThread = new Thread(httpStreamService);
        httpStreamThread.start();
    }

	//! Funkcja kończąca transmisję HTTP.
    public static void StopStreamServer() {
        httpstream=false;

        if (httpStreamThread != null && httpStreamThread.isAlive()) {
            httpStreamThread.interrupt();
        }
    }

	//! Funkcja konwertująca klatkę IplImage do typu BufferedImage.
    public static BufferedImage toBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }

	//! Główna funkcja wątka przechwycającego klatkę oraz przekazująca klatkę do dalszych części programu
    static void Capture(FrameGrabber[] cam, AtomicBoolean priorityQueue, CanvasFrame window,JPanel left, int prevWidth, int prevHeight, Object lock) {
        left.removeAll();
        CanvasFrame canvasFrame = new CanvasFrame("video");
        canvasFrame.setVisible(false);
        left.add(canvasFrame.getCanvas());
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();


        Runnable runnableCapturingVideo = new Runnable() {
            @Override
            public void run() {

                synchronized (lock) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
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

                         img2 = converter.convert(frame);

                        if(httpstream) {
                            httpStreamService.imag = toBufferedImage(img2);
                        }

                        try {
                            Thread.sleep(250);
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
