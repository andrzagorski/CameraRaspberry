//! Pakiet, w którym zostały zamknięte wszystkie klasy programu
package org.example;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.opencv.core.Core;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.opencv.highgui.HighGui.destroyAllWindows;
import static org.opencv.highgui.HighGui.destroyAllWindows;

/**
	\file Main.java
	\brief Plik z głównym programem
	
	gdzie napisany został też frontend z oknami i funkcjonalnymi przyciskami.
*/

/**
	\brief Klasa przechowująca kod głównego programu
	
	oraz cały frontend z oknem i funkcjonalnymi przyciskami. Na początku rozmiar głównego okna to 640x480
*/
public class Main {

	//! Oktet A adresu IP.
    static int OCT_A=157;

	//! Oktet B adresu IP.
    static int OCT_B=158;

	//! Oktet C adresu IP.
    static int OCT_C=126;

	//! Oktet D adresu IP.
    static int OCT_D=82;


    static final AtomicBoolean priority = new AtomicBoolean(false);

	//! Główna funkcja programu.
    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {

        Object lock = new Object();

        JButton jButtonChooseCamera = new JButton("Initialize Camera");
        jButtonChooseCamera.setBounds(100, 100, 150, 20);

        JButton jButtonStartRecord = new JButton("Make video");
        jButtonStartRecord.setBounds(170, 170, 130, 20);

        JButton jButtonGrab = new JButton("Capture Image!");
        jButtonGrab.setEnabled(false); // cannot grab image before choosing camera.
        jButtonGrab.setBounds(20, 140, 130, 20);

        JButton JbuttonSaveCaptured = new JButton("Save Captured Image");
        JbuttonSaveCaptured.setEnabled(false); // cannot grab image before choosing camera.
        jButtonGrab.setBounds(20, 140, 130, 20);

        JButton stopRecord = new JButton("stop recording");
        stopRecord.setEnabled(false);

        JComboBox<SelectedResolution.ResolutionOption> CaptureImageResolution = new JComboBox<>(SelectedResolution.ResolutionOption.values());
        SpinnerNumberModel model = new SpinnerNumberModel(OCT_A, 0, 255, 1); // zakres od 0 do 255 z krokiem 1
        SpinnerNumberModel model2 = new SpinnerNumberModel(OCT_B, 0, 255, 1); // zakres od 0 do 255 z krokiem 1
        SpinnerNumberModel model3 = new SpinnerNumberModel(OCT_C, 0, 255, 1); // zakres od 0 do 255 z krokiem 1
        SpinnerNumberModel model4 = new SpinnerNumberModel(OCT_D, 0, 255, 1); // zakres od 0 do 255 z krokiem 1
        JSpinner firstOctetIp = new JSpinner(model);
        JSpinner secondOctetIp = new JSpinner(model2);
        JSpinner thirdOctetIp = new JSpinner(model3);
        JSpinner fourthOctetIp = new JSpinner(model4);

        CaptureImageResolution.setSelectedIndex(0);

        JButton JBtnStartHttpServices = new JButton("Start Http Stream Server");
        JBtnStartHttpServices.setBounds(100, 100, 150, 20);
        JBtnStartHttpServices.setEnabled(false);

        JButton JBtnStopHttpServices = new JButton("Stop Http Stream Server");
        JBtnStopHttpServices.setBounds(100, 100, 150, 20);
        JBtnStopHttpServices.setEnabled(false);

        JPanel ipPanel = new JPanel(new GridLayout(0, 4));
        ipPanel.add(firstOctetIp);
        ipPanel.add(secondOctetIp);
        ipPanel.add(thirdOctetIp);
        ipPanel.add(fourthOctetIp);

        JPanel videoPanel = new JPanel(new GridLayout(3, 0));
        videoPanel.add(jButtonStartRecord); // Make video
        videoPanel.add(stopRecord);

        JPanel imagePanel = new JPanel(new GridLayout(3, 0));
        imagePanel.add(jButtonGrab); // Capture Image!
        imagePanel.add(CaptureImageResolution);
        imagePanel.add(JbuttonSaveCaptured);
        imagePanel.setBackground(Color.PINK);

        JPanel httpStreamServerPanel= new JPanel(new GridLayout(3, 0));
        httpStreamServerPanel.add(JBtnStartHttpServices);
        httpStreamServerPanel.add(JBtnStopHttpServices);
        httpStreamServerPanel.add(ipPanel);
        httpStreamServerPanel.setBackground(Color.DARK_GRAY);

        //buttonPanelTOP
        JPanel buttonPanelTop = new JPanel(new GridLayout(2, 2,15,15));

        buttonPanelTop.add(jButtonChooseCamera);//Initialize Camera
        buttonPanelTop.add(videoPanel);
        buttonPanelTop.add(imagePanel);
        buttonPanelTop.add(httpStreamServerPanel);
        buttonPanelTop.setVisible(true);




        //BOT SIDE
        JPanel BottomSidePanel = new JPanel();
        BottomSidePanel.setBackground(Color.DARK_GRAY);

        //LEFT SIDE
        JPanel LeftSidePanel = new JPanel();
        LeftSidePanel.setBackground(Color.ORANGE);

        //RIGHT SIDE
        JPanel RightSidePanel = new JPanel();
        RightSidePanel.setBackground(Color.YELLOW);

        //TOP SIDE
        JPanel TopSidePanel = new JPanel();
        TopSidePanel.setBackground(Color.PINK);
        TopSidePanel.add(buttonPanelTop);

        //CENTER SIDE
        JPanel CenterSidePanel = new JPanel();
        CenterSidePanel.setBackground(Color.LIGHT_GRAY);

        CanvasFrame window = new CanvasFrame("Camera");

        window.setSize(1280, 1024);
        window.setPreferredSize(new Dimension(1366,768));
        window.setLayout(new BorderLayout());
        window.add(TopSidePanel,BorderLayout.NORTH);
        window.add(BottomSidePanel,BorderLayout.SOUTH);
        window.add(LeftSidePanel,BorderLayout.WEST);
        window.add(RightSidePanel,BorderLayout.EAST);
        window.add(CenterSidePanel,BorderLayout.CENTER);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        window.setVisible(true);
        window.pack();
        final FrameGrabber[] cam = new FrameGrabber[1]; // current camera
        cam[0]= new OpenCVFrameGrabber(0);


        final Frame[] GrabbedFrame = {null};
        jButtonChooseCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SelectedResolution.ResolutionOption CaptureResolution = (SelectedResolution.ResolutionOption)CaptureImageResolution.getSelectedItem();

                try {
                            InitCam.initialize(cam[0],CaptureResolution.getWidth() ,CaptureResolution.getHeight());
                }
                catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                catch (FrameGrabber.Exception ex) {
                    throw new RuntimeException(ex);
                }

                jButtonGrab.setEnabled(true);
                JBtnStartHttpServices.setEnabled(true);
                CaptureVideo.Capture(cam,priority,window,LeftSidePanel,CaptureResolution.getWidth(),CaptureResolution.getHeight(),lock);
            }
        });
        jButtonGrab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JbuttonSaveCaptured.setEnabled(true);

                SelectedResolution.ResolutionOption CaptureResolution = (SelectedResolution.ResolutionOption)CaptureImageResolution.getSelectedItem();

                CaptureFrame.Capture(cam,GrabbedFrame,priority,window,RightSidePanel,CaptureResolution.getWidth(),CaptureResolution.getHeight(),CaptureResolution.getWidth(), CaptureResolution.getHeight(), lock);
            }
        });

        jButtonStartRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectedResolution.ResolutionOption CaptureResolution = (SelectedResolution.ResolutionOption)CaptureImageResolution.getSelectedItem();

                VideoRecording.Record(cam,GrabbedFrame,priority,window,CaptureResolution.getWidth(),CaptureResolution.getHeight());
                stopRecord.setEnabled(true);

            }
        });

        stopRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VideoRecording.stopRecording.set(true);
            }
        });

        JbuttonSaveCaptured.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CaptureFrame.SaveImage(window);
            }
        });
        JBtnStartHttpServices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CaptureVideo.startThreadStream();
                CaptureVideo.httpstream=true;
                JBtnStartHttpServices.setEnabled(false);
                JBtnStopHttpServices.setEnabled(true);
            }
        });
        JBtnStopHttpServices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CaptureVideo.StopStreamServer();
                JBtnStartHttpServices.setEnabled(true);
                JBtnStopHttpServices.setEnabled(false);
            }
        });
        firstOctetIp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                OCT_A = (int)firstOctetIp.getValue();
            }
        });
        secondOctetIp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                OCT_B = (int)secondOctetIp.getValue();
            }
        });
        thirdOctetIp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                OCT_C = (int)thirdOctetIp.getValue();
            }
        });
        fourthOctetIp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                OCT_D = (int)fourthOctetIp.getValue();
            }
        });
    }
}