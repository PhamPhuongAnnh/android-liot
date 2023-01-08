package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    EditText signup_name;
    EditText signup_username;
    EditText signup_pass;
    EditText signup_confirm_pass;
    Button signup_btn;

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
    }

    public void action() {
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (signup_confirm_pass.getText().toString() != ""
                        && signup_pass.getText().toString().equals(signup_confirm_pass.getText().toString())) {

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}