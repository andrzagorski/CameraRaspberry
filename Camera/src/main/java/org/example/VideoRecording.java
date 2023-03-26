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
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

public class VideoRecording {

    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

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

                    Size frameSize = new Size(RecordWidth, RecordHeight); // this res work with external cam.

                    VideoCapture videoCapture = new VideoCapture(0);
                    videoCapture.set(CAP_PROP_FRAME_WIDTH,frameSize.width);
                    videoCapture.set(CAP_PROP_FRAME_HEIGHT,frameSize.height);


                    int fourcc = VideoWriter.fourcc('h','2','6','4'); // format wideo
                    VideoWriter videoWriter = new VideoWriter(file+".mp4", fourcc, fps, frameSize, true);


                    Mat frame = new Mat();
                    while (true) {
                        videoCapture.read(frame);
                        videoWriter.write(frame);

                        System.out.println(frame.height()+" "+frame.width());
                        System.out.println(videoCapture.get(CAP_PROP_FRAME_HEIGHT)+" "+videoCapture.get(CAP_PROP_FRAME_WIDTH));


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
