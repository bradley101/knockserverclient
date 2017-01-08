package com.bradley.knockserver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by bradley on 22-12-2016.
 */

public class DriverActivity extends AppCompatActivity {

    ListView messageContainer;
    EditText et;
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout);

        messageContainer = (ListView) findViewById(R.id.message_container);
        et = (EditText) findViewById(R.id.message);
        btn = (Button) findViewById(R.id.send_button);

        // TODO make an alert dialog to get name of the recipient
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
        final EditText nameEt = new EditText(DriverActivity.this);
        nameEt.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(nameEt);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = "no-name";
                name = nameEt.getText().toString();
                perform(name);
            }
        });
        builder.setTitle("Enter your name");
        builder.show();
    }

    public void perform(String name) {
        String operation = getIntent().getExtras().getString("operation");
        switch (operation) {
            case "stream":
                stream(name);
                break;

            case "connect":
                connect(name);
                break;
        }
    }

    private void stream(String name) {
        ParentFunctionalty func = new ParentFunctionalty(this, messageContainer, et, btn, name);
        func.performFunction();
    }

    private void connect(String name) {
        ConnectFunctionality conn = new ConnectFunctionality(this, messageContainer, et, btn, name);
        conn.connect();
    }
}


















