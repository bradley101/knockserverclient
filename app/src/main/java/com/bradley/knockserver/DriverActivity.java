package com.bradley.knockserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

public class DriverActivity extends AppCompatActivity {

    TextView tv;
    EditText et;
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout);

        tv = (TextView) findViewById(R.id.display_ui);
        et = (EditText) findViewById(R.id.edit_text);
        btn = (Button) findViewById(R.id.btn);

        String operation = getIntent().getExtras().getString("operation");
        switch (operation) {
            case "stream":
                stream();
                break;

            case "connect":
                connect();
                break;
        }
    }

    private void stream() {
        ParentFunctionalty func = new ParentFunctionalty(this, tv, et, btn);
        func.performFunction();
    }

    private void connect() {

    }
}


















