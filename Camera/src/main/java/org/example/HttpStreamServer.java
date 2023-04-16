package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 \file HttpStreamServer.java
 \brief Plik z klasą HttpStreamServer.
*/

/**
 \brief Klasa służąca do wysyłania obrazu z kamery na serwer http w czasie rzeczywistym.
*/
public class HttpStreamServer implements Runnable {
    //! Poszczególne oktetyu adresu IP.
    private int octA, octB, octC, octD;

    //! Socket serwera do komunikacji sieciowej.
    private ServerSocket serverSocket;

    //! Socket do komunikacji sieciowej.
    private Socket socket;

    //! String ustawiający odpowiednio transmisję.
    private final String boundary = "stream";

    //! Stream do komunikacji z serwerem.
    private OutputStream outputStream;

    //! Klatka do wyświetlenia na serwerze.
    public BufferedImage imag;


    //! Setter adresu IP
    public HttpStreamServer(int octA, int octB, int octC, int octD) {
        this.octA = octA;
        this.octB = octB;
        this.octC = octC;
        this.octD = octD;
    }

    //! Funkcja rozpoczynająca transmisję na serwer http.
    public void startStreamingServer() throws IOException {

        InetAddress addr = InetAddress.getByName(octA+"."+octB+"."+octC+"."+octD); // specify address.
        serverSocket = new ServerSocket(8080, 50, addr);
        socket = serverSocket.accept();
        writeHeader(socket.getOutputStream(), boundary);
    }

    //! Funkcja wpisująca niezbędne parametry do działania serwera.
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

    //! Funkcja wysyłająca obraz z kamery na serwer.
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

    //! Funkcja główna wątka, która cyklicznie wysyła obraz z kamery na serwer.
    public void run() {
        try {
            System.out.print("go with browser to: "+octA+"."+octB+"."+octC+"."+octD+":8080");
            startStreamingServer();
            while (true) {
                pushImage(imag);
            }
        } catch (IOException e) {
            return;
        }
    }

    //! Funkcja zatrzymująca serwer HTTP.
    public void stopStreamingServer() throws IOException {
        socket.close();
        serverSocket.close();
    }

}