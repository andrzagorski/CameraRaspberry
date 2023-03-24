package org.example;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import org.opencv.videoio.VideoCapture;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;


public class VideoRecording {

    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
  // static {
       //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      // System.load("C:\\opencv\\build\\java\\x64\\opencv_java451.dll");
      // System.load("C:\\opencv\\build\\x64\\vc14\\bin\\opencv_ffmpeg451_64.dll");
 //  }

    static void Record(FrameGrabber[] cam, Frame[] GrabbedFrame, AtomicBoolean priorityQueue, CanvasFrame window, int RecordWidth, int RecordHeight,int fps,int recordingTime,Object lock) {
        Runnable runnableRecordingVideo = new Runnable() {

            @Override
            public void run() {

                priorityQueue.set(true);

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

                    Size frameSize = new Size(RecordWidth, RecordHeight); // rozmiar klatki wideo
                    int fourcc = VideoWriter.fourcc('X','V','I','D'); // format wideo
                    VideoWriter videoWriter = new VideoWriter(file+".avi", fourcc, fps, frameSize, true);

                    VideoCapture videoCapture = new VideoCapture(0);

                    Mat frame = new Mat();
                    while (true) {
                        videoCapture.read(frame);
                        videoWriter.write(frame);
                        imshow("Nagrywanie wideo", frame);
                        if (waitKey(1) == 27) break; // Przerwanie nagrywania po naciśnięciu klawisza Esc
                    }

                    videoCapture.release();
                    videoWriter.release();

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
