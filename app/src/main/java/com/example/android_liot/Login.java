package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    EditText login_username;
    EditText login_pass;
    TextView forgot_pass;
    TextView create_acount;
    Button login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        action();
    }
    public void init(){
        login_btn = findViewById(R.id.login_btn);
        login_pass = findViewById(R.id.login_pass);
        login_username = findViewById(R.id.login_username);
        forgot_pass = findViewById(R.id.login_forgot_pass);
        create_acount = findViewById(R.id.login_singup);
    }
    public void action(){
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ListDevice.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", login_username.getText().toString());
                bundle.putString("password", login_pass.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ChagnePass.class);
                startActivity(intent);
            }
        });
        create_acount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

    }
}