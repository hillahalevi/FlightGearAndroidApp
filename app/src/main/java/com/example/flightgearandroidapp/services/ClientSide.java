package com.example.flightgearandroidapp.services;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    public void connect(String ip, int port) throws IOException {
        InetAddress serverAddr = InetAddress.getByName(ip);
        System.out.println("Connecting...");
        this.socket = new Socket();


        this.socket.connect(new InetSocketAddress(serverAddr, port), 5000);
        //this.socket = new Socket(serverAddr, port);
        System.out.println("Connected");
        this.outStream = this.socket.getOutputStream();
    }

    public void sendCommand(String parameter, String value) {
        new SendCommandTask().execute(parameter,value);
    }

    public void disconnect() {
        try {
            this.outStream.close();
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendCommandTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String parameter = strings[0].toUpperCase();
            String value = strings[1];
            if (!paths.containsKey(parameter)) {
                return null;
            }
            String msg = "set " + paths.get(parameter) + " " + value + " \r\n";
            byte[] command = msg.getBytes();

            try {
                outStream.write(command, 0, command.length);
                outStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

