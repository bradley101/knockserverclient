package com.bradley.knockserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by bradley on 22-12-2016.
 */

public class GroupIO implements Runnable {
    ArrayList<Socket> openSockets;

    public GroupIO() {
        openSockets = new ArrayList<>();
    }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(61000);
            while (true) {
                Socket socket = serverSocket.accept();
                openSockets.add(socket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class NewClient implements Runnable {
        Socket clientSocet;
        BufferedReader in;
        PrintWriter out;

        NewClient (Socket socket) {
            this.clientSocet = socket;
            try {
                in = new BufferedReader(new InputStreamReader(this.clientSocet.getInputStream()));
                out = new PrintWriter(this.clientSocet.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                in = null;
                out = null;
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void run() {

        }

        class ClientInputThread implements Runnable {

            @Override
            public void run() {

            }
        }

        class ClientOutputThread implements Runnable {

            @Override
            public void run() {

            }
        }
    }
}
