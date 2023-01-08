package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Stack;

class Device {
    String ip;
    String title;
    String description;
    JSONObject schema;
}

public class ListDeviceActivity extends AppCompatActivity {
    String url = "http://171.244.57.168:8080/api/list_device";

    LinearLayout all_device_layout;
    ArrayList<Device> devices = new ArrayList<>();
    Singleton singleton;

    private void display() {
        Device pDevice = new Device();
        pDevice.ip = "192.168.71.1";
        pDevice.title = "Provision device";
        pDevice.description = "Connect to access point";
        devices.add(pDevice);

        all_device_layout.removeAllViews();
        for (int i = 0; i < devices.size(); i++) {
            String currentIp = devices.get(i).ip;
            JSONObject currentSchema = devices.get(i).schema;

            View view = getLayoutInflater().inflate(R.layout.device_card, null);
            TextView name1 = view.findViewById(R.id.device_name);
            TextView des1 = view.findViewById(R.id.device_description);
            name1.setText(devices.get(i).title);
            des1.setText(devices.get(i).description);

            view.setOnClickListener(view1 -> {
                singleton.setSelectedIp(currentIp);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, "http://" + currentIp + "/schema", null, response -> {
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

                }, error -> {
                    if (error instanceof TimeoutError && !currentIp.equals("192.168.71.1")) {
                        try {
                            currentSchema.put("type", "object");
                            singleton.properties.clear();
                            singleton.properties.push(currentSchema.getJSONObject("properties"));
                        } catch (JSONException e) {
                            Toast.makeText(ListDeviceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(ListDeviceActivity.this, DetailDeviceActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ListDeviceActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                req.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                singleton.getQueue().add(req);
            });
            all_device_layout.addView(view);
        }

    }

    private void display_login() {
        if (singleton.isLoggedIn()) {
            FrameLayout loginFrame = findViewById(R.id.login_frame);
            loginFrame.setVisibility(View.GONE);
            FrameLayout logoutFrame = findViewById(R.id.toolbar_button_frame);
            logoutFrame.setVisibility(View.VISIBLE);
            Button logoutButton = findViewById(R.id.toolbar_button);
            logoutButton.setText("Logout");
            logoutButton.setOnClickListener(view -> {
                singleton.setLoggedIn(false);
                finish();
                startActivity(getIntent());
            });
            TextView title = findViewById(R.id.toolbar_title);
            title.setText("Devices");
        } else {
            FrameLayout loginFrame = findViewById(R.id.login_frame);
            loginFrame.setVisibility(View.VISIBLE);
            Button login = findViewById(R.id.login_button);
            login.setOnClickListener(view -> {
                Intent intent = new Intent(ListDeviceActivity.this, LoginActivity.class);
                startActivity(intent);
            });

            Button signup = findViewById(R.id.signup_button);
            signup.setOnClickListener(view -> {
                Intent intent = new Intent(ListDeviceActivity.this, SignupActivity.class);
                startActivity(intent);
            });

            FrameLayout logoutFrame = findViewById(R.id.toolbar_button_frame);
            logoutFrame.setVisibility(View.GONE);

            TextView title = findViewById(R.id.toolbar_title);
            title.setText("Android liot");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_device);
        RequestQueue mQueue = Singleton.getInstance(this).getQueue();
        all_device_layout = findViewById(R.id.all_device_layout);
        singleton = Singleton.getInstance(this);

        display_login();
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
                            JSONArray device_datas = response.getJSONArray("payload");
                            for (int i = 0; i < device_datas.length(); i++) {
                                JSONObject device_data = device_datas.getJSONObject(i);
                                Device device = new Device();
                                device.ip = device_data.getString("device_local_ip");
                                device.title = device_data.getString("device_title");
                                device.description = device_data.getString("device_title");
                                device.schema = device_data.getJSONObject("device_schema");
                                devices.add(device);
                            }
                            display();
                        } else {
                            Toast.makeText(ListDeviceActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ListDeviceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(ListDeviceActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                }
            }));
        } else {
            display();
        }
    }

    @Override
    public void onBackPressed() {
    }
}