package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    String loginUrl = "http://171.244.57.168:8080/api/account/new";
    EditText signup_name;
    EditText signup_username;
    EditText signup_pass;
    EditText signup_confirm_pass;
    Button signup_btn;
    RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        action();
    }

    public void init() {
        signup_name = findViewById(R.id.signup_name);
        signup_username = findViewById(R.id.signup_username);
        signup_btn = findViewById(R.id.signup_btn);
        signup_pass = findViewById(R.id.signup_pass);
        signup_confirm_pass = findViewById(R.id.signup_pass1);
        mQueue = Singleton.getInstance(this).getQueue();
    }

    public void action() {
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(signup_pass.getText().toString().matches(".{1,6}r$")){
//                    if (signup_pass.getText().toString().equals(signup_confirm_pass.getText().toString())) {
//
//                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        System.out.println("1");
//                    } else {
//                        Toast.makeText(SignupActivity.this, "Password sai", Toast.LENGTH_SHORT).show();
//
//                        System.out.println("2");
//                    }
//                }else {
//                    Toast.makeText(SignupActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
//
//                    System.out.println("3");
//                }
                int flag = 1;
                if(signup_username.getText().toString().matches("[a-z_]{6,12}$")){
                    System.out.println("a1");
                }else{
                    flag = 0;
                    Toast.makeText(SignupActivity.this, "Username có độ tài từ 6 đến 12 ký tự, không có khoảng trắng và không dấu, không số, có thể sử dụng _", Toast.LENGTH_SHORT).show();
                    System.out.println("3");
                }
                if(signup_pass.getText().toString().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")){
                    if (signup_pass.getText().toString().equals(signup_confirm_pass.getText().toString())) {

                    } else {
                        flag = 0;
                        Toast.makeText(SignupActivity.this, "Nhập lại pass không trùng", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    flag = 0;
                    Toast.makeText(SignupActivity.this, "Password Tối thiểu tám ký tự, ít nhất một chữ cái, một số và một ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                    System.out.println("4");
                }
                if(signup_name.getText().toString().matches("[A-Za-z ]+")){
                    System.out.println("a3");
                }else{
                    flag = 0;
                    Toast.makeText(SignupActivity.this, "Name không chứa ký tự đặc biệt, không số", Toast.LENGTH_SHORT).show();
                    System.out.println("5");
                }
                if(flag == 1){
                    JSONObject body = new JSONObject();
                    try {
                        body.put("name", signup_name.getText().toString());
                        body.put("username", signup_username.getText().toString());
                        body.put("password", signup_pass.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("", "onClick: " + body.toString());
                    mQueue.add(new JsonObjectRequest(Request.Method.POST, loginUrl, body, response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Singleton singleton = Singleton.getInstance(SignupActivity.this);
                                singleton.setName(signup_name.getText().toString());
                                singleton.setUsername(signup_username.getText().toString());
                                singleton.setPassword(signup_pass.getText().toString());
                                singleton.setLoggedIn(true);
                                Intent intent = new Intent(SignupActivity.this, ListDeviceActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignupActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(SignupActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                    }));
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        System.out.println("1");
                }
            }
        });
    }
}