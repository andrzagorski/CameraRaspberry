package org.example;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bytedeco.opencv.global.opencv_highgui.waitKey;

public class VideoRecording {
    static void Record(FrameGrabber[] cam, Frame[] GrabbedFrame, AtomicBoolean priorityQueue, CanvasFrame window, int RecordWidth, int RecordHeight,int fps,int recordingTime,Object lock) {
        Runnable runnableRecordingVideo = new Runnable() {
            @Override
            public void run() {

                priorityQueue.set(true);

                // where u want to save a video.
                File file = null;

                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                }

                synchronized (lock){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    //grabbing an image.
                    try {
                        InitCam.initialize(cam[0],RecordWidth , RecordHeight);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    } catch (FrameGrabber.Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    Frame frame = null;
                    try {
                        frame = cam[0].grab();
                    } catch (FrameGrabber.Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    int fourcc = VideoWriter.fourcc('X','2','6','6');
                    VideoCapture capture = new VideoCapture(0); //na sztywno kamera 0
                    capture.set(3,RecordWidth);
                    capture.set(4,RecordHeight);

                        if(file==null) {throw new NullPointerException(); }

                            VideoWriter out = new VideoWriter(file.toString()+".mp4", fourcc, fps, new Size(RecordWidth,RecordHeight));

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
                        InitCam.initialize(cam[0], RecordWidth, RecordHeight);
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
