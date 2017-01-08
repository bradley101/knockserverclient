package com.bradley.knockserver;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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


public class ParentFunctionality {
    final String TAG = "KNOCKKNOCK";
    ArrayList<Socket> openSockets;
    ArrayList<BufferedReader> openReaders;
    ArrayList<PrintWriter> openWriters;
    ListView serverListView;
    EditText serverEditText;
    Button serverSendbutton;
    Activity parentActivity;
    String name;
    ArrayList<String> messagesText;
    ArrayList<Integer> messagesAlignment;
    ArrayAdapter<String> messagesListViewAdapter;

    void log (String s) {
        Log.i (TAG, s);
    }

    public ParentFunctionality(Activity context, ListView lv, EditText et, Button btn, String name) {
        openSockets = new ArrayList<>();
        openReaders = new ArrayList<>();
        openWriters = new ArrayList<>();
        messagesText = new ArrayList<>();
        messagesAlignment = new ArrayList<>();

        this.parentActivity = context;
        this.serverListView = lv;
        this.serverEditText = et;
        this.serverSendbutton = btn;
        this.name = name;
    }

    public void performFunction() {

        messagesListViewAdapter = new ArrayAdapter<String>(parentActivity, R.layout.chat_message_bubble_layout) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.chat_message_bubble_layout, parent, false);
                }
                TextView tv = (TextView) convertView.findViewById(R.id.chat_message);
                tv.setText(messagesText.get(position));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                params.addRule(messagesAlignment.get(position));
                return convertView;
            }

            @Override
            public int getCount() {
                return messagesText.size();
            }
        };

        serverListView.setAdapter(messagesListViewAdapter);

        new Thread(new GroupIO()).start();
        new Thread(new PerformOutput()).start();
    }

    class PerformOutput implements Runnable {

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
                            s = "\n" + name + "-> " + s;

                            updateUI(s, RelativeLayout.ALIGN_PARENT_RIGHT);

                            // send string to every client
                            for (PrintWriter o : openWriters) {
                                o.println(s);
                            }

                            log (s);
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
                        updateUI(s, RelativeLayout.ALIGN_PARENT_LEFT);

                        log(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateUI(final String s, final int align) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesText.add(s);
                messagesAlignment.add(align);
                messagesListViewAdapter.notifyDataSetChanged();
            }
        });
    }

}