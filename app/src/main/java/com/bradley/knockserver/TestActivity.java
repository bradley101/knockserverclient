package com.bradley.knockserver;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by bradley on 08-01-2017.
 */

public class TestActivity extends AppCompatActivity{
    String[] messages = {"Hello", "World", "From", "@bradley"};
    ListView lv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        lv = (ListView) findViewById(R.id.test_list_view);
        lv.setAdapter(new LVAdp(this, R.layout.chat_message_bubble_layout));
    }

    class LVAdp extends ArrayAdapter<String> {
        LayoutInflater inflater;
        public LVAdp(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.chat_message_bubble_layout, parent, false);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.chat_message);
            tv.setText(messages[position]);
            return convertView;
        }

        @Override
        public int getCount() {
            return messages.length;
        }
    }
}
