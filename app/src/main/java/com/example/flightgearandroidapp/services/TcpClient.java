package com.example.flightgearandroidapp.services;
import android.util.Log;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TcpClient {

    //params
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
        //build map for values
        this.paths = new HashMap<>();
        this.paths.put("AILERON", "/controls/flight/aileron");
        this.paths.put("ELEVATOR", "/controls/flight/elevator");

        startConnection();
    }

    /**
     * send message to server
     * @param parameter
     * @param value
     */
    public void sendMessage(String parameter, String value) {

        parameter = parameter.toUpperCase();
        //get path from map
        if (!this.paths.containsKey(parameter)) {
            return;
        }
        //build mesasge
        final String message = "set " + (this.paths.get(parameter) + " ") + (value + "\r\n");
        //create new thread
        final Thread thread = new Thread() {
            @Override
            public void run() {
                //check if connection of
                if (writer != null && socket != null && socket.isConnected()) {
                    try {
                        String msg = message;
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

    /**
     * start connection to server
     */
    private void startConnection() {

        final Thread thread = new Thread(){
            //connect to the server thread
            public void run() {
                try {
                    //get ip
                    InetAddress serverAddr = InetAddress.getByName(serverIp);

                    //create a socket to make the connection with the server
                    socket = new Socket(serverAddr, port);
                    try {
                        //get stream writer
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
    /**
     * get socket status
     * @return
     */
    public boolean isConnected() {
        //hi hilla - test!
        if(socket == null)
            return false;
        return socket.isConnected();
    }

}