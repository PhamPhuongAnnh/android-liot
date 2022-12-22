package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayList<String> device_ips = new ArrayList<>();
    String selectedIp;
    //ArrayList<String> device_names = new ArrayList<>();


    LinearLayout all_device_layout;
    LinearLayout detail_device_layout;
    Button btnBack;
    RequestQueue mQueue;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mQueue = Volley.newRequestQueue(this);
        device_ips.add("192.168.71.1");

        setContentView(R.layout.activity_main);
        all_device_layout = findViewById(R.id.all_device_layout);
        detail_device_layout = findViewById(R.id.detail_device_layout);


        for (int i = 0; i < device_ips.size(); i++) {
            selectedIp = device_ips.get(i);
            View view = getLayoutInflater().inflate(R.layout.device_card, null);
            TextView name1 = view.findViewById(R.id.device_name);
            name1.setText("Device 1");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/schema", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                response.put("type", "object");
                                properties.clear();
                                properties.push(response.getJSONObject("properties"));
                                findViewById(R.id.all_device_layout_parent).setVisibility(View.GONE);
                                findViewById(R.id.detail_device_layout_parent).setVisibility(View.VISIBLE);

                                showDetail();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("log", "onErrorResponse: ", error);
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
        properties.pop();
        if (properties.empty()) {
            findViewById(R.id.all_device_layout_parent).setVisibility(View.VISIBLE);
            findViewById(R.id.detail_device_layout_parent).setVisibility(View.GONE);
        } else {
            showDetail();
        }

    }

    void viewAllDevice() {

    }

    void viewDevice() {

    }

    public void showDetail() {
        JSONObject current = properties.peek();
        try {
            Iterator<String> keys = current.keys();
            detail_device_layout.removeAllViews();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject field = current.getJSONObject(key);
                View view = null;

                String id_str = field.getString("id");
                String title_str = field.getString("title");
                String obj_type = field.getString("type");

                if (obj_type.equals("bool")) {
                    view = getLayoutInflater().inflate(R.layout.layout_bool, null);
                    TextView title = view.findViewById(R.id.title_bool);
                    Switch switch1 = view.findViewById(R.id.switch_bool);
                    title.setText(title_str);
                    switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put(id_str, b);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://" + selectedIp + "/data",obj, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("log", "onErrorResponse: ", error);
                                }
                            });
                            mQueue.add(request);
                        }
                    });
                    detail_device_layout.addView(view);
                } else if (obj_type.equals("integer")) {
                    view = getLayoutInflater().inflate(R.layout.layout_integer, null);
                    TextView title = view.findViewById(R.id.title_integer);
                    title.setText(title_str);
                    detail_device_layout.addView(view);
                } else if (obj_type.equals("object")) {
                    view = getLayoutInflater().inflate(R.layout.layout_object, null);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                properties.push(field.getJSONObject("properties"));
                                showDetail();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    TextView title = view.findViewById(R.id.title_object);
                    title.setText(title_str);
                    detail_device_layout.addView(view);
                } else {
                    view = getLayoutInflater().inflate(R.layout.layout_text, null);
                    TextView title = view.findViewById(R.id.title_text);
                    title.setText(title_str);
                    TextView textView = view.findViewById(R.id.text_field_text);
                    view.findViewById(R.id.text_field_send).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String content = textView.getText().toString();
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put(id_str, content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://" + selectedIp + "/data",obj, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(context, "Request SEnt", Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("log", "onErrorResponse: ", error);
                                }
                            });
                            mQueue.add(request);

                        }
                    });
                    detail_device_layout.addView(view);
                }
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