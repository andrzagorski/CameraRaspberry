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



        Webcam webcam = Webcam.getDefault();
        Dimension OnlyToView = new Dimension(640,480);


        webcam.setViewSize(OnlyToView);
         WebcamPanel panel = new WebcamPanel(webcam);

        panel.setImageSizeDisplayed(true);

        panel.setBounds(400,50,640,480);
        JComboBox<String> jComboBox = new JComboBox<>();

        for (int i=0;i<Webcam.getWebcams().size();i++) {
            jComboBox.addItem(Webcam.getWebcams().get(i).toString());
        }
        jComboBox.setBounds(80, 50, 225, 20);

        JButton jButton = new JButton("Wybierz");
        jButton.setBounds(100, 100, 90, 20);

        JButton ButtonGrab = new JButton("Save Image!");
        ButtonGrab.setBounds(20, 140, 130, 20);



        JLabel jLabel = new JLabel();
        jLabel.setBounds(90, 100, 400, 100);


        JFrame window = new JFrame("Webcam");
        window.add(panel);

        window.add(jButton);
        window.add(jComboBox);
        window.add(ButtonGrab);

        window.add(jLabel);


        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setSize(1280,1024);
        window.setVisible(true);




        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                panel.stop();
                window.remove(panel);

               // Webcam cam2 =Webcam.getWebcamByName(jComboBox.getSelectedItem().toString());
                /*Webcam cam2 = Webcam.getWebcamByName("Live! Cam Sync 1080p");
                System.out.println(Webcam.getWebcams());
                WebcamPanel pan2=new WebcamPanel(cam2);
                pan2.setBounds(400,50,640,480); // exactly the same as panel 0 !!
                window.add(pan2);
*/
                String selectedFruit = "You selected " + jComboBox.getSelectedItem().toString();
                jLabel.setText(selectedFruit);
            }
        });

       ButtonGrab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // grabbing an image.
                System.out.println(jComboBox.getSelectedIndex());
                FrameGrabber grabber = new OpenCVFrameGrabber(jComboBox.getSelectedIndex());
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