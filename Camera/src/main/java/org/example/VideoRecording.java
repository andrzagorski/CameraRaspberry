package org.example;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bytedeco.opencv.global.opencv_highgui.waitKey;

public class VideoRecording {
    static void Capture(FrameGrabber[] cam, Frame[] GrabbedFrame, JComboBox<String> listOfCameras, AtomicBoolean priorityQueue, CanvasFrame window, int prevWidth, int prevHeight, int MAX_WIDTH, int MAX_HEIGHT, Object lock) {
        Runnable runnableRecordingVideo = new Runnable() {
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

                    int frameWidth = 640;
                    int frameHeight = 480;
                    int recordingTime = 10;
                    int fourcc = VideoWriter.fourcc('X','2','6','6');
                    VideoCapture capture = new VideoCapture(0); //na sztywno kamera 0
                    capture.set(3,frameWidth);
                    capture.set(4,frameHeight);
                    VideoWriter out = new VideoWriter("output.mp4", fourcc, 25.0, new Size(frameWidth,frameHeight));

                    long startTime = System.nanoTime();

                    while(true){
                        Mat frameMat  = new Mat();
                        capture.read(frameMat);
                        out.write(frameMat);

                        // Sprawdzanie, czy czas nagrywania nie przekroczył ustalonej długości
                        long currentTime = System.nanoTime();
                        double elapsedTime = (currentTime - startTime) / 1e9;
                        if (elapsedTime >= recordingTime) {
                            break;
                        }
                        JPanel panel = new JPanel();
                        // Wyświetlanie ramki w oknie
                        //imshow("frame", frame);

                        // Czekanie na klawisz 'q', aby przerwać nagrywanie
                        if (waitKey(1) == 'q') {
                            break;
                        }
                    }
                    // Zwalnianie zasobów
                    capture.release();
                    out.release();

                    //OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
                    //IplImage img = converter.convert(frame);

                    //ImgSaver.saveImg(window,img);

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
        try{new Thread(runnableRecordingVideo).start();}
        catch (Exception exception){
        }
    }
}
