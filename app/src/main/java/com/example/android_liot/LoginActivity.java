package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    String loginUrl = "http://171.244.57.168:8080/api/account/name";
    //String loginUrl = "http://192.168.103.23:8080/api/account/name";
    EditText login_username;
    EditText login_password;
    TextView forgot_password;
    TextView create_account;
    Button login_button;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        setup();
    }

    public void init() {
        login_button = findViewById(R.id.login_btn);
        login_password = findViewById(R.id.login_password);
        login_username = findViewById(R.id.login_username);
        forgot_password = findViewById(R.id.login_forgot_pass);
        create_account = findViewById(R.id.login_singup);
        mQueue = Singleton.getInstance(this).getQueue();
    }

    public void setup() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject body = new JSONObject();
                try {
                    body.put("username", login_username.getText().toString());
                    body.put("password", login_password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("", "onClick: " + body.toString());

                mQueue.add(new JsonObjectRequest(Request.Method.POST, loginUrl, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Singleton singleton = Singleton.getInstance(LoginActivity.this);
                                singleton.setUsername(login_username.getText().toString());
                                singleton.setPassword(login_password.getText().toString());
                                singleton.setLoggedIn(true);
                                Intent intent = new Intent(LoginActivity.this, ListDeviceActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }));
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }
}