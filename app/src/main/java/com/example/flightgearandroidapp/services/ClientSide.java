package com.example.flightgearandroidapp.services;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientSide {
    private Map<String, String> paths;
    private Socket socket;
    private OutputStream outStream;

    public ClientSide(){
        this.paths = new HashMap<>();
        this.paths.put("AILERON", "/controls/flight/aileron");
        this.paths.put("ELEVATOR", "/controls/flight/elevator");
    }

    public void connect(String ip, int port) {
        //TODO connect - check with dor server code - ask jenny
        try {
            InetAddress address = InetAddress.getByAddress(ip.getBytes());
            this.socket = new Socket(address,port);
            this.outStream = this.socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String parameter, String value) {
        parameter = parameter.toUpperCase();
        if (!this.paths.containsKey(parameter)) {
            return;
        }

        String msg = "set " + (this.paths.get(parameter) + " ") + (value + "\r\n");
        byte[] command = msg.getBytes();

//        try {
//            this.outStream.write(command, 0, command.length);
//            this.outStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void disconnect() {
        try {
            this.outStream.close();
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

