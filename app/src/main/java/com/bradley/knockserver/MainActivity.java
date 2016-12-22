package com.bradley.knockserver;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    EditText et;
    TextView tv;
    Button btn;
    ProgressDialog pd;
    BufferedReader in;
    String s;
    PrintWriter out;
    Thread ithread, othread;
    final String TAG = "KnockServer";

    void log(String s) {
        Log.i(TAG, s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.edit_text);
        tv = (TextView) findViewById(R.id.tv);
        btn = (Button) findViewById(R.id.btn);

        pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Waiting for a client");
        //pd.show();

        new Thread(new InputThread()).start();
        new Thread(new OutputThread()).start();
    }

    class InputThread implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket server = new ServerSocket(60000);
                //pd.show();
                Socket socket = server.accept();
                //pd.hide();
                //pd.cancel();
                log("Socket opened");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while ((s = in.readLine()) != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.append("\nThat -> " + s);
                        }
                    });

                    log("\nThat -> " + s);
                    if (s.equals("bye")) {
                        out.println("bye");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class OutputThread implements Runnable {

        @Override
        public void run() {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.append("\nMe -> " + et.getText().toString());
                            log("\nMe -> " + et.getText().toString());
                            out.println(et.getText().toString());
                            et.setText("");
                        }
                    });
                }
            });
        }
    }
}
