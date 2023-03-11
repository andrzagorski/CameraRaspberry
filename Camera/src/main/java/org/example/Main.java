package org.example;

import com.github.sarxos.webcam.*;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.PriorityQueue;

public class Main {
    static PriorityQueue<String> priorityQueue = new PriorityQueue<String>(); // reason for this is to get access by the grabimage method to physical device.

    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {

        JComboBox<String> listOfCameras = new JComboBox<>();

        for (int i = 0; i < Webcam.getWebcams().size(); i++) {
            listOfCameras.addItem(Webcam.getWebcams().get(i).toString());
        }
        listOfCameras.setBounds(80, 50, 225, 20);

        JButton jButtonChooseCamera = new JButton("Wybierz");
        jButtonChooseCamera.setBounds(100, 100, 90, 20);

        JButton ButtonGrab = new JButton("Save Image!");
        ButtonGrab.setEnabled(false); // cannot grab image before choosing camera.
        ButtonGrab.setBounds(20, 140, 130, 20);

        JLabel jLabel = new JLabel();
        jLabel.setBounds(90, 100, 400, 100);

        CanvasFrame window = new CanvasFrame("Webcam");

        window.add(jButtonChooseCamera);
        window.add(listOfCameras);
        window.add(ButtonGrab);

        window.add(jLabel);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setSize(1280, 1024);
        window.setVisible(true);
        window.setLayout(new FlowLayout());
        JPanel mainPanel = new JPanel(new FlowLayout());
        window.add(mainPanel);
        mainPanel.setPreferredSize(new Dimension(800, 600)); // preferowana wielkość dla panelu nadrzędnego

        final FrameGrabber[] cam = new FrameGrabber[1]; // current camera

        jButtonChooseCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonGrab.setEnabled(true);
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

                        synchronized (cam[0]) {

                            while(!priorityQueue.isEmpty()) {
                                try {
                                    cam[0].wait();
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }

                            try {

                                    InitCam.initialize(cam[0], 640, 480);
                                } catch (InterruptedException | FrameGrabber.Exception ex) {
                                    ex.printStackTrace();
                                }
                                while (true) {
                                    if(!priorityQueue.isEmpty()) break;
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
        });

        ButtonGrab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(priorityQueue);
                Runnable runnableCapturingImage = new Runnable() {

                    @Override
                    public void run() {
                        priorityQueue.add("Device Reservation");
                        synchronized (cam[0]){

                              try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                        //grabbing an image.
                        FrameGrabber grabber = new OpenCVFrameGrabber(listOfCameras.getSelectedIndex());
                        grabber.setImageWidth(9152);
                        grabber.setImageHeight(6944);

                        try {
                            grabber.start();

                        } catch (FrameGrabber.Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        Frame frame = null;
                        try {
                            frame = grabber.grab();
                        } catch (FrameGrabber.Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
                        IplImage img = converter.convert(frame);

                        ImgSaver.saveImg(window,img);

                        try {
                            grabber.close();
                            grabber.release();
                        } catch (FrameGrabber.Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                              priorityQueue.remove();
                             cam[0].notify(); // tell thread for video that device is aval.

                            jButtonChooseCamera.doClick();
                    }
                      }
                };
                 try{new Thread(runnableCapturingImage).start();}
                catch (Exception exception){
                   }
            }
        });
    }
}