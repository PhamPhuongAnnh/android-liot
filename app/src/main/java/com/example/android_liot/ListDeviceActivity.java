package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

public class ListDeviceActivity extends AppCompatActivity {
    String url = "http://171.244.57.168:8080/list_device";
    //String url = "http://192.168.103.23:8080/list_device";
    LinearLayout all_device_layout;
    ArrayList<String> device_ips = new ArrayList<>();
    ArrayList<String> device_title = new ArrayList<>();
    ArrayList<String> device_description = new ArrayList<>();
    Singleton singleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_device);
        RequestQueue mQueue = Singleton.getInstance(this).getQueue();
        all_device_layout = findViewById(R.id.all_device_layout);
        singleton = Singleton.getInstance(this);
        if (singleton.isLoggedIn()) {
            JSONObject body = new JSONObject();
            try {
                body.put("username", singleton.getUsername());
                body.put("password", singleton.getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mQueue.add(new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray devices = response.getJSONArray("payload");

                        } else {
                            Toast.makeText(ListDeviceActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }));
        }
        device_ips.add("192.168.71.1");
        device_title.add("Provision device");
        device_description.add("Connect to access point");

        all_device_layout.removeAllViews();
        for (int i = 0; i < device_ips.size(); i++) {
            String selectedIp = device_ips.get(i);
            View view = getLayoutInflater().inflate(R.layout.device_card, null);
            TextView name1 = view.findViewById(R.id.device_name);
            TextView des1 = view.findViewById(R.id.device_description);
            name1.setText(device_title.get(i));
            des1.setText(device_description.get(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    singleton.setSelectedIp(selectedIp);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/schema", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                response.put("type", "object");
                                Stack<JSONObject> properties = singleton.properties;
                                properties.clear();
                                properties.push(response.getJSONObject("properties"));
                                Intent intent = new Intent(ListDeviceActivity.this, DetailDeviceActivity.class);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ListDeviceActivity.this,error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mQueue.add(request);
                }
            });
            all_device_layout.addView(view);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListDeviceActivity.this, MainActivity.class);
        startActivity(intent);
    }
}