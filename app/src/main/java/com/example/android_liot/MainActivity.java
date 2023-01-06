package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
            TextView des1 = view.findViewById(R.id.device_description);
            name1.setText("Device " + (i + 1));
            des1.setText(device_ips.get(i));
            int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedIp = device_ips.get(finalI);
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

            all_device_layout.removeAllViews();
            for (int i = 0; i < device_ips.size(); i++) {
                selectedIp = device_ips.get(i);
                View view = getLayoutInflater().inflate(R.layout.device_card, null);
                TextView name1 = view.findViewById(R.id.device_name);
                TextView des1 = view.findViewById(R.id.device_description);
                if (selectedIp.equals("192.168.71.1")) {
                    name1.setText("New device");
                } else {
                    name1.setText("Device " + (i));
                }
                des1.setText(device_ips.get(i));
                int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedIp = device_ips.get(finalI);
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
        } else {
            showDetail();
        }

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
                    mQueue.add(new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/data?field=" + id_str, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean state = response.getBoolean("value");
                                switch1.setChecked(state);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }));
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
                        }
                    });
                    detail_device_layout.addView(view);
                } else if (obj_type.equals("number")) {
                    view = getLayoutInflater().inflate(R.layout.layout_integer, null);
                    TextView title = view.findViewById(R.id.title_integer);
                    SeekBar seekBar = view.findViewById(R.id.seek_bar_integer);
                    mQueue.add(new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/data?field=" + id_str, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                seekBar.setProgress(response.getInt("value"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
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
                    mQueue.add(new JsonObjectRequest(Request.Method.GET, "http://" + selectedIp + "/data?field=" + id_str, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String text = response.getString("value");
                                textView.setText(text);
                                if (id_str.equals("wifi_status_ip") && selectedIp.equals("192.168.71.1") && !text.equals("0.0.0.0")) {
                                    Log.i("log", "onResponse: store ip: " + text);
                                    device_ips.add(text);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }));
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