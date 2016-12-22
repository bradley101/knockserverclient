package com.bradley.knockserver;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
class ParentFunctionalty {
    ArrayList<Socket> openSockets;
    ArrayList<BufferedReader> openReaders;
    ArrayList<PrintWriter> openWriters;
    TextView serverTextView;
    EditText serverEditText;
    Button serverSendbutton;
    Activity parentActivity;

    public ParentFunctionalty(Activity context, TextView tv, EditText et, Button btn) {
        openSockets = new ArrayList<>();
        openReaders = new ArrayList<>();
        openWriters = new ArrayList<>();

        this.parentActivity = context;
        this.serverTextView = tv;
        this.serverEditText = et;
        this.serverSendbutton = btn;
    }

    public void performFunction()  {
        new Thread(new GroupIO()).start();
        new Thread(new PerformOutput()).start();
    }

    public class PerformOutput implements Runnable {

        @Override
        public void run() {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverSendbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String s;
                            s = serverEditText.getText().toString();

                            // append my name as the sender
                            s = "Shantanu-> " + s;

                            // send string to every client
                            for (PrintWriter o : openWriters) {
                                o.println(s);
                            }
                        }
                    });
                }
            });
        }
    }

    public class GroupIO implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(61000);
                while (true) {
                    Socket socket = serverSocket.accept();
                    openSockets.add(socket);

                    // TODO make a new thread for each
                    // client that connects to the server
                    new Thread(new NewClient(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        class NewClient implements Runnable {
            Socket clientSocet;
            BufferedReader in;
            PrintWriter out;

            public NewClient(Socket socket) {
                this.clientSocet = socket;
                try {
                    in = new BufferedReader(new InputStreamReader(this.clientSocet.getInputStream()));
                    out = new PrintWriter(this.clientSocet.getOutputStream(), true);
                    openReaders.add(in);
                    openWriters.add(out);
                } catch (IOException e) {
                    e.printStackTrace();
                    in = null;
                    out = null;
                    openSockets.remove(socket);
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void run() {
                String s;
                try {
                    // TODO listen for incoming messages for a client
                    while ((s = in.readLine()) != null) {

                        // Sending the message to rest
                        // of the connected clients
                        for (PrintWriter o : openWriters) {
                            if (o.equals(out)) continue;
                            o.println(s);
                        }

                        // TODO Update server UI with the incoming message
                        updateUI(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void updateUI(final String s) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverTextView.append("\n" + s);
                }
            });
        }
    }
}