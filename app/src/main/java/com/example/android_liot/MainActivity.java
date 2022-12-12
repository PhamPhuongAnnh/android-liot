package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout all_devices  = findViewById(R.id.all_device_layout);
        View view = getLayoutInflater().inflate(R.layout.device_card, null);
        TextView name1 = view.findViewById(R.id.device_name);
        name1.setText("Device 1");
        all_devices.addView(view);

        View view2 = getLayoutInflater().inflate(R.layout.device_card, null);
        TextView name2 = view2.findViewById(R.id.device_name);
        name2.setText("Device 2");
        all_devices.addView(view2);

        mQueue = Volley.newRequestQueue(this);
        jsonParse();

    }

    void jsonParse() {
        String url = "http://192.168.71.1/schema";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject properties = response.getJSONObject("properties");
                            Iterator<String> keys = properties.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                JSONObject value = properties.getJSONObject(key);
                                Log.i("log" , value.toString());
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        mQueue.add(request);
    }
}