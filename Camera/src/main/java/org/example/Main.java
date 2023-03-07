package org.example;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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




        jButtonChooseCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String chosenCamera = listOfCameras.getSelectedItem().toString();
                Webcam webcam = null;
                // KAMERA
                if(chosenCamera != null) {       // jesli uzytkownik wybral kamere
                    for (int i = 0; i < Webcam.getWebcams().size(); i++) {
                        if (chosenCamera.contains(Webcam.getWebcams().get(i).getName())) {
                            webcam = Webcam.getWebcams().get(i);
                        }
                    }
                }


                if(webcam != null){
                    WebcamPanel webcamPanel = new WebcamPanel(webcam);
                    webcamPanel.setPreferredSize(new Dimension(640, 480));

                    mainPanel.add(webcamPanel);
                    mainPanel.setPreferredSize(new Dimension(800, 600)); // preferowana wielkość dla panelu nadrzędnego

                    window.pack();
                    window.setVisible(true);

                }
                else {
                    JOptionPane.showMessageDialog(window, "Nie znaleziono kamery ");
                }

                String selectedFruit = "You selected " + listOfCameras.getSelectedItem().toString();
                //jLabel.setText(selectedFruit);
            }
        });

       ButtonGrab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                // grabbing an image.
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



            }
        });


    }
}