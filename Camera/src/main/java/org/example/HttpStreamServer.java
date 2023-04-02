package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpStreamServer implements Runnable {

    private ServerSocket serverSocket;
    private Socket socket;
    private final String boundary = "stream";
    private OutputStream outputStream;
    public BufferedImage imag;


    public HttpStreamServer() {}

    public void startStreamingServer() throws IOException {
        serverSocket = new ServerSocket(8080);
        socket = serverSocket.accept();
        writeHeader(socket.getOutputStream(), boundary);
    }

    private void writeHeader(OutputStream stream, String boundary) throws IOException {
        stream.write(("HTTP/1.0 200 OK\r\n" +
                "Connection: close\r\n" +
                "Max-Age: 0\r\n" +
                "Expires: 0\r\n" +
                "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
                "Pragma: no-cache\r\n" +
                "Content-Type: multipart/x-mixed-replace; " +
                "boundary=" + boundary + "\r\n" +
                "\r\n" +
                "--" + boundary + "\r\n").getBytes());
    }

    public void pushImage(BufferedImage frame) throws IOException {
        if (frame == null)
            return;
        try {
            outputStream = socket.getOutputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(frame, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            outputStream.write(("Content-type: image/jpeg\r\n" +
                    "Content-Length: " + imageBytes.length + "\r\n" +
                    "\r\n").getBytes());
            outputStream.write(imageBytes);
            outputStream.write(("\r\n--" + boundary + "\r\n").getBytes());
        } catch (Exception ex) {
            socket = serverSocket.accept();
            writeHeader(socket.getOutputStream(), boundary);
        }
    }

    public void run() {
        try {
            System.out.print("go to  http://localhost:8080 with browser");

            startStreamingServer();
            while (true) {
                pushImage(imag);
            }
        } catch (IOException e) {
            return;
        }
    }

    public void stopStreamingServer() throws IOException {
        socket.close();
        serverSocket.close();
    }

}