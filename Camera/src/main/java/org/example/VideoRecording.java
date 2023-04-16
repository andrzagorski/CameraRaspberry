package org.example;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.*;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.videoio.VideoCapture;

import static org.example.Main.PREV_HEIGHT;
import static org.example.Main.PREV_WIDTH;
import static org.opencv.highgui.HighGui.*;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

/**
	\file VideoRecording.java
	\brief Plik klasą VideoRecording.
*/

/**
	\brief Klasa abstrachująca proces cyklicznego przechwytywania klatek

	i zapisu klatek w postaci wideo.
*/
public class VideoRecording {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

	//! Główna funkcja nagrywająca
    static void Record(FrameGrabber[] cam, Frame[] GrabbedFrame, AtomicBoolean priorityQueue,Boolean showHQ, CanvasFrame window, int RecordWidth, int RecordHeight, int fps, int recordingTime, Object lock) {
        Runnable runnableRecordingVideo = new Runnable() {

            @Override
            public void run() {

                priorityQueue.set(true);

                File file = null;
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();


                    synchronized (lock) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                        Size frameSize = new Size(RecordWidth, RecordHeight); // this res work with external cam.

                        VideoCapture videoCapture = new VideoCapture(0);

                        if(showHQ){
                            videoCapture.set(CAP_PROP_FRAME_WIDTH, frameSize.width);
                            videoCapture.set(CAP_PROP_FRAME_HEIGHT, frameSize.height);
                        }
                        else {
                            videoCapture.set(CAP_PROP_FRAME_WIDTH, PREV_WIDTH);
                            videoCapture.set(CAP_PROP_FRAME_HEIGHT, PREV_HEIGHT);
                        }


                        int fourcc = VideoWriter.fourcc('h', '2', '6', '4'); // format wideo
                        String sciezka = file.toString();
                        VideoWriter videoWriter;
                        if (!sciezka.contains(".mp4")) {
                            sciezka += ".mp4";
                        }
                        videoWriter = new VideoWriter(sciezka, fourcc, fps, frameSize, true);


                        Mat frame = new Mat();


                        HighGui.namedWindow("windowName", HighGui.WINDOW_NORMAL);

                        while (true) {
                            videoCapture.read(frame);
                            videoWriter.write(frame);

                            System.out.println(frame.height() + " " + frame.width());


                            HighGui.imshow("windowName", frame);

                            //HighGui.destroyAllWindows();  // tu zamyka

                            if (waitKey(1) == 27) {
                                HighGui.destroyAllWindows();  // nie zamyka
                                break;
                            } // Przerwanie nagrywania po naciśnięciu klawisza Esc

                        }
                        HighGui.destroyAllWindows(); // nie zamyka

                        videoCapture.release();
                        videoWriter.release();
                        priorityQueue.set(false);
                        lock.notifyAll();
                    }
                } else priorityQueue.set(false);
            }
        };
        try {
            new Thread(runnableRecordingVideo).start();

        } catch (Exception exception) {
        }
    }
}
