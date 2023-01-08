package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DetailDeviceActivity extends AppCompatActivity {
    JSONObject current;
    Singleton singleton;
    RequestQueue mQueue;
    LinearLayout detail_device_layout;
    String selectedIp;
    Context context;

    @Override
    public void onBackPressed() {
        if (singleton.properties.size() > 1) {
            singleton.properties.pop();
            finish();
            startActivity(getIntent());
        } else {
            Intent intent = new Intent(DetailDeviceActivity.this, ListDeviceActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_device);
        detail_device_layout = findViewById(R.id.detail_device_layout);
        singleton = Singleton.getInstance(this);
        current = singleton.properties.peek();
        mQueue = singleton.getQueue();
        selectedIp = singleton.getSelectedIp();
        context = this;
        try {
            Iterator<String> keys = current.keys();
            detail_device_layout.removeAllViews();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject field = current.getJSONObject(key);
                View view;

                String id_str = field.getString("id");
                String title_str = field.getString("title");
                String obj_type = field.getString("type");

                switch (obj_type) {
                    case "bool": {
                        view = getLayoutInflater().inflate(R.layout.layout_bool, null);
                        TextView title = view.findViewById(R.id.title_bool);
                        Switch switch1 = view.findViewById(R.id.switch_bool);
                        mQueue.add(new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/data?field=" + id_str, null, response -> {
                            try {
                                boolean state = response.getBoolean("value");
                                switch1.setChecked(state);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }, error -> {
                        }));
                        title.setText(title_str);
                        switch1.setOnCheckedChangeListener((compoundButton, b) -> {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put(id_str, b);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://" + selectedIp + "/data", obj, new Response.Listener<JSONObject>() {
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
                        });
                        detail_device_layout.addView(view);
                        break;
                    }
                    case "number": {
                        view = getLayoutInflater().inflate(R.layout.layout_integer, null);
                        TextView title = view.findViewById(R.id.title_integer);
                        SeekBar seekBar = view.findViewById(R.id.seek_bar_integer);
                        mQueue.add(new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/data?field=" + id_str, null, response -> {
                            try {
                                seekBar.setProgress(response.getInt("value"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {

                        }));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            seekBar.setMin(field.getInt("minimum"));
                        }
                        seekBar.setMax(field.getInt("maximum"));
                        title.setText(title_str);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put(id_str, seekBar.getProgress());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                mQueue.add(new JsonObjectRequest(Request.Method.POST, "http://" + selectedIp + "/data", obj, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(context, "Request SEnt", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("log", "onErrorResponse: ", error);
                                    }
                                }));
                            }
                        });
                        detail_device_layout.addView(view);
                        break;
                    }
                    case "object": {
                        view = getLayoutInflater().inflate(R.layout.layout_object, null);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    singleton.getProperties().push(field.getJSONObject("properties"));
                                    finish();
                                    startActivity(getIntent());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        TextView title = view.findViewById(R.id.title_object);
                        title.setText(title_str);
                        detail_device_layout.addView(view);
                        break;
                    }
                    default: {
                        view = getLayoutInflater().inflate(R.layout.layout_text, null);
                        TextView title = view.findViewById(R.id.title_text);
                        title.setText(title_str);
                        TextView textView = view.findViewById(R.id.text_field_text);
                        mQueue.add(new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/data?field=" + id_str, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String text = response.getString("value");
                                    textView.setText(text);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }));
                        view.findViewById(R.id.text_field_send).setOnClickListener(view1 -> {
                            String content = textView.getText().toString();
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put(id_str, content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mQueue.add(new JsonObjectRequest(Request.Method.POST, "http://" + selectedIp + "/data", obj, response -> {
                                Toast.makeText(context, "Request SEnt", Toast.LENGTH_SHORT).show();
                            }, error -> {
                                Log.e("log", "onErrorResponse: ", error);
                            }));

                        });
                        detail_device_layout.addView(view);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}