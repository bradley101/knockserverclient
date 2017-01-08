package com.bradley.knockserver;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by bradley on 22-12-2016.
 */

public class ConnectFunctionality {
    final String TAG = "KNOCKKNOCK";
    Activity context;
    ListView myListView;
    EditText myEditText;
    Button mySendButton;
    ArrayList<String> messagesText;
    ArrayList<Integer> messagesAlignment;
    ArrayAdapter<String> myListViewArrayAdapter;
    String name;

    void log (String s) {
        Log.i (TAG, s);
    }

    ConnectFunctionality(Activity a, ListView lv, EditText et, Button btn, String name) {
        context = a;
        myEditText = et;
        myListView = lv;
        mySendButton = btn;
        this.name = name;

        messagesText = new ArrayList<>();
        messagesAlignment = new ArrayList<>();
    }

    void connect() {
        myListViewArrayAdapter = new ArrayAdapter<String>(context, R.layout.chat_message_bubble_layout) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.chat_message_bubble_layout, parent);
                RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.chat_bubble_rl);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl.getLayoutParams();
                params.addRule(messagesAlignment.get(position));
                TextView tv = (TextView) v.findViewById(R.id.chat_message);
                tv.setText(messagesText.get(position));
                return v;
            }
        };
        new Thread(new ConnectToServer()).start();
    }

    class ConnectToServer implements Runnable {

        BufferedReader in;
        PrintWriter out;
        @Override
        public void run() {
            try {
                Socket socket = new Socket("192.168.43.1", 61000);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                new Thread(new Runnable() {
                    String s;
                    @Override
                    public void run() {
                        try {
                            while ((s = in.readLine()) != null) {
                                log(s);
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateUI(s, RelativeLayout.ALIGN_PARENT_LEFT);
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mySendButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String s = "\n" + name + "-> " + myEditText.getText().toString();
                                        log (s);
                                        out.println(s);
                                        updateUI(s, RelativeLayout.ALIGN_PARENT_RIGHT);
                                    }
                                });
                            }
                        });
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void updateUI(final String s, final int align) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messagesAlignment.add(align);
                    messagesText.add(s);
                    myListViewArrayAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
