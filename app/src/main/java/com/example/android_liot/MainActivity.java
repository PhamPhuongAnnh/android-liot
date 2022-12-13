package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    Stack<JSONObject> properties = new Stack<>();

    LinearLayout all_device_layout;
    LinearLayout detail_device_layout;
    Button btnBack;
    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new Controller(this);

        setContentView(R.layout.activity_main);
        all_device_layout = findViewById(R.id.all_device_layout);
        detail_device_layout = findViewById(R.id.detail_device_layout);


        View view = getLayoutInflater().inflate(R.layout.device_card, null);
        TextView name1 = view.findViewById(R.id.device_name);
        name1.setText("Device 1");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.all_device_layout_parent).setVisibility(View.GONE);
                findViewById(R.id.detail_device_layout_parent).setVisibility(View.VISIBLE);
                jsonParse();
                showDetail();
            }
        });
        all_device_layout.addView(view);
    }

    @Override
    public void onBackPressed() {
        properties.pop();
        if (properties.empty()) {
            findViewById(R.id.all_device_layout_parent).setVisibility(View.VISIBLE);
            findViewById(R.id.detail_device_layout_parent).setVisibility(View.GONE);
        } else {
            showDetail();
        }

    }

    View objectToView(JSONObject obj) {
        Log.i("log", obj.toString());
        try {
            String id_str = obj.getString("id");
            String title_str = obj.getString("title");
            String obj_type = obj.getString("type");
            if (obj_type.equals("bool")) {
                View view = getLayoutInflater().inflate(R.layout.layout_bool, null);
                TextView title = view.findViewById(R.id.title_bool);
                Switch switch1 = view.findViewById(R.id.switch_bool);
                title.setText(title_str);
                return view;
            } else if (obj_type.equals("integer")) {
                View view = getLayoutInflater().inflate(R.layout.layout_integer, null);
                TextView title = view.findViewById(R.id.title_integer);
                title.setText(title_str);
                return view;
            } else if (obj_type.equals("object")) {
                View view = getLayoutInflater().inflate(R.layout.layout_object, null);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                TextView title = view.findViewById(R.id.title_object);
                title.setText(title_str);
                return view;
            } else {
                View view = getLayoutInflater().inflate(R.layout.layout_text, null);
                TextView title = view.findViewById(R.id.title_text);
                title.setText(title_str);
                return view;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view = getLayoutInflater().inflate(R.layout.layout_text, null);
        return view;
    }

    public void showDetail() {
        JSONObject current = properties.peek();
        try {
            Iterator<String> keys = current.keys();
            detail_device_layout.removeAllViews();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject field = current.getJSONObject(key);
                View field_view = objectToView(field);
                detail_device_layout.addView(field_view);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void jsonParse() {
        String ret = this.getString(R.string.example_schema);
        try {
            JSONObject response = new JSONObject(ret);
            response.put("type", "object");
            properties.clear();
            properties.push(response.getJSONObject("properties"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}