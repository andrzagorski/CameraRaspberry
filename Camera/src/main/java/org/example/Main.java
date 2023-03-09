package org.example;

import com.github.sarxos.webcam.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws FrameGrabber.Exception {


        JComboBox<String> listOfCameras = new JComboBox<>();

        for (int i=0;i<Webcam.getWebcams().size();i++) {
            listOfCameras.addItem(Webcam.getWebcams().get(i).toString());
        }
        listOfCameras.setBounds(80, 50, 225, 20);

        JButton jButtonChooseCamera = new JButton("Wybierz");
        jButtonChooseCamera.setBounds(100, 100, 90, 20);

        JButton ButtonGrab = new JButton("Save Image!");
        ButtonGrab.setBounds(20, 140, 130, 20);



        JLabel jLabel = new JLabel();
        jLabel.setBounds(90, 100, 400, 100);


        JFrame window = new JFrame("Webcam");

        window.add(jButtonChooseCamera);
        window.add(listOfCameras);
        window.add(ButtonGrab);

        window.add(jLabel);


        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setSize(1280,1024);
        window.setVisible(true);
        window.setLayout(new FlowLayout());
        JPanel mainPanel = new JPanel(new FlowLayout());
        window.add(mainPanel);

        final WebcamPanel[] webcamPanel = {null};
        final Webcam[] webcam = {null};


        jButtonChooseCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Webcam[] prevCamera = new Webcam[1];
                 WebcamPanel[] prevwebcamPanel = new WebcamPanel[1];

                Runnable runnableCapturingVideo = new Runnable() {

                    @Override
                    public void run() {
                        String chosenCamera = listOfCameras.getSelectedItem().toString();

                        if(chosenCamera != null) {  // list of cameras is not empty
                            webcam[0] = Webcam.getWebcams().get(listOfCameras.getSelectedIndex());
                        }

                        synchronized (webcam[0]){

                            prevwebcamPanel[0] =webcamPanel[0];     // reason for this is to be able to free those devices when they are already in use.
                            prevCamera[0]=webcam[0];

                            if(webcam[0] != null&&webcamPanel[0] == null){ // if camera is instantialized but not shown  - > no prev. // first add

                                webcam[0].setViewSize(new Dimension(640,480)); // image prev resolution
                                webcamPanel[0] = new WebcamPanel(webcam[0]);
                                webcamPanel[0].setPreferredSize(new Dimension(640, 480));
                                mainPanel.add(webcamPanel[0]);
                                mainPanel.setPreferredSize(new Dimension(800, 600)); // preferowana wielkość dla panelu nadrzędnego

                                window.pack();
                                window.setVisible(true);






                            }


                            else if (webcam[0] != null&&webcamPanel[0] != null) { // camera is instantialized and something is shown in prev. window -> has to be cleared out and realocated.

                                prevCamera[0].close();
                                mainPanel.remove(prevwebcamPanel[0]);

                                // same case as adding in the first time
                                webcam[0].setViewSize(new Dimension(640,480)); // image prev resolution
                                webcamPanel[0] = new WebcamPanel(webcam[0]);
                                webcamPanel[0].setPreferredSize(new Dimension(640, 480));
                                mainPanel.add(webcamPanel[0]);
                                mainPanel.setPreferredSize(new Dimension(800, 600)); // preferowana wielkość dla panelu nadrzędnego
                                window.pack();
                                window.setVisible(true);

                            }
                            else {JOptionPane.showMessageDialog(window, "Nie znaleziono kamery "); }

                            String selectedFruit = "You selected " + listOfCameras.getSelectedItem().toString();
                            jLabel.setText(selectedFruit );
                        }
                    }
                };
                new Thread(runnableCapturingVideo).start();
            }
        });

       ButtonGrab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Runnable runnableCapturingImage = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (webcam[0]){
                             //WebcamLock lock= new WebcamLock(webcam[0]);
                                webcam[0].close();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }

                            //grabbing an image.
                            System.out.println(listOfCameras.getSelectedIndex());
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

                            JFileChooser fileChooser = new JFileChooser();
                            if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
                                File file = fileChooser.getSelectedFile();
                                opencv_imgcodecs.cvSaveImage(file.toString(),img);
                            }

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

                            webcam[0].open();


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