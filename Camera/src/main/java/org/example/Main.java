package org.example;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static int MAX_WIDTH = 9152;
    static int MAX_HEIGHT = 6944;
    static int PREV_WIDTH= 640;
    static int PREV_HEIGHT=480;
    static String[] AvaliableResolutions = { "640x480","1280x720" ,"1920x1080","2312x1736", "3840x2160", "4624x3472", "9152x6944" };

    static final AtomicBoolean priority = new AtomicBoolean(false);


    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {

        Object lock = new Object();

        JButton jButtonChooseCamera = new JButton("Initialize Camera");
        jButtonChooseCamera.setBounds(100, 100, 150, 20);


        JButton jButtonStartRecord = new JButton("Make video");
        jButtonStartRecord.setBounds(170, 170, 130, 20);


        JButton jButtonGrab = new JButton("Capture Image!");
        jButtonGrab.setEnabled(false); // cannot grab image before choosing camera.
        jButtonGrab.setBounds(20, 140, 130, 20);



        JComboBox RecordResolution = new JComboBox(AvaliableResolutions);
        JComboBox CaptureImageResolution = new JComboBox(AvaliableResolutions);
        RecordResolution.setSelectedIndex(0);
        CaptureImageResolution.setSelectedIndex(0);


        //BOT SIDE
        JPanel buttonPanelTop = new JPanel(new GridLayout(2, 0));
        buttonPanelTop.add(jButtonStartRecord);
        buttonPanelTop.add(jButtonGrab);
        buttonPanelTop.add(jButtonChooseCamera);


        buttonPanelTop.add(RecordResolution);
        buttonPanelTop.add(CaptureImageResolution);


        JPanel BottomSidePanel = new JPanel();
        BottomSidePanel.setBackground(Color.DARK_GRAY);

        //LEFT SIDE
        JPanel LeftSidePanel = new JPanel();
        LeftSidePanel.setBackground(Color.ORANGE);


        //RIGHT SIDE
        ImagePanel RightSidePanel = new ImagePanel();
        RightSidePanel.setBackground(Color.YELLOW);

        //TOP SIDE
        JPanel TopSidePanel = new JPanel();
        TopSidePanel.setBackground(Color.PINK);
        TopSidePanel.add(buttonPanelTop);

        //CENTER SIDE
        JPanel CenterSidePanel = new JPanel();
        CenterSidePanel.setBackground(Color.LIGHT_GRAY);


        CanvasFrame window = new CanvasFrame("Main");

        window.setSize(1280, 1024);
        window.setPreferredSize(new Dimension(1920,1080));
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
        //graphicsDevice.setFullScreenWindow(window);


        final FrameGrabber[] cam = new FrameGrabber[1]; // current camera

        final Frame[] GrabbedFrame = {null};


        jButtonChooseCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                jButtonGrab.setEnabled(true);
                CaptureVideo.Capture(cam,priority,window,LeftSidePanel,PREV_WIDTH,PREV_HEIGHT,lock);

            }
        });

        jButtonGrab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CaptureFrame.Capture(cam,GrabbedFrame,priority,window,RightSidePanel,PREV_WIDTH,PREV_HEIGHT,MAX_WIDTH,MAX_HEIGHT,lock);
            }
        });

        jButtonStartRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                    VideoRecording.Record(cam,GrabbedFrame,priority,window,MAX_WIDTH,MAX_HEIGHT,10,100,lock);
            }
        });


    }
}