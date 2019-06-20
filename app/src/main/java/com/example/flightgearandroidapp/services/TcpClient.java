package com.example.flightgearandroidapp.services;
import android.util.Log;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TcpClient {

    public static final String TAG = TcpClient.class.getSimpleName();
    private  String serverIp;
    private int port;
    private Map<String, String> paths;
    private Socket socket;
    private PrintWriter writer;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
        this.paths = new HashMap<>();
        this.paths.put("AILERON", "/controls/flight/aileron");
        this.paths.put("ELEVATOR", "/controls/flight/elevator");

        startConnection();
    }


    public void sendMessage(String parameter, String value) {

        parameter = parameter.toUpperCase();
        if (!this.paths.containsKey(parameter)) {
            return;
        }
        final String message = "set " + (this.paths.get(parameter) + " ") + (value + "\r\n");
        final Thread thread = new Thread() {
            @Override
            public void run() {
                if (writer != null) {
                    try {
                        String msg = message + " \r\n";
                        writer.print(msg);
                        writer.flush();
                    } catch (Exception e) {
                        System.out.println("FAIL");
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        if (writer != null) {
            writer.flush();
            writer.close();
        }
        writer = null;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startConnection() {

        final Thread thread = new Thread(){
            //coonect to the server
            public void run() {
                try {
                    //here you must put your computer's IP address.
                    InetAddress serverAddr = InetAddress.getByName("10.0.2.2");

                    //create a socket to make the connection with the server
                    socket = new Socket(serverAddr, port);
                    try {
                        writer = new PrintWriter(socket.getOutputStream(),true);

                    } catch (Exception e) {
                        System.out.println("ERROR");
                    }
                } catch (Exception e) {
                    System.out.println("ERROR 2");
                    Log.e("TCP", "C: Error", e);
                }

            }
        };
        thread.start();
    }

    public boolean isConnected() {
        if(socket == null)
            return false;
        return socket.isConnected();
    }

}