package org.example;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;


import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;

/**
 \file VideoRecording.java
 \brief Plik klasą VideoRecording.
*/

/**
 \brief Klasa abstrachująca proces cyklicznego przechwytywania klatek

 i zapisu klatek w postaci wideo.
*/
public class VideoRecording {
    static OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
   public static AtomicBoolean stopRecording = new AtomicBoolean(false);

    //! Główna funkcja nagrywająca
    static void Record(FrameGrabber[] cam, Frame[] GrabbedFrame, AtomicBoolean priorityQueue, CanvasFrame window, int RecordWidth, int RecordHeight) {
        Runnable runnableRecordingVideo = new Runnable() {

            @Override
            public void run() {

                File file = null;
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();

                        try {
                            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file+".mp4",RecordWidth,RecordHeight);
                            recorder.setFrameRate(24);
                            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
                            recorder.setVideoBitrate(9000);
                            recorder.setFormat("mp4");
                            recorder.setVideoQuality(0); // maximum quality
                            recorder.start();

                            while(!stopRecording.get())
                            {
                                recorder.record(grabberConverter.convert(CaptureVideo.img2));
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                System.out.println("Trwa Nagrywanie");
                            }
                            recorder.stop();
                            recorder.release();
                        }
                        catch (org.bytedeco.javacv.FrameRecorder.Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        };
        try {
            new Thread(runnableRecordingVideo).start();

        } catch (Exception exception) {
        }
    }
}
