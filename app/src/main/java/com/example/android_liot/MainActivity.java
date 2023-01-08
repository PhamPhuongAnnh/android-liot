package com.example.android_liot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;

public class MainActivity extends AppCompatActivity {
    Button main_login;
    Button main_signup;
    Button main_list_device;
    ImageView info;
    RequestQueue mQueue;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, ListDeviceActivity.class);
        startActivity(intent);

        init();
        action();

    }
    public void init(){
        mQueue = Singleton.getInstance(this).getQueue();
        main_login = findViewById(R.id.main_login);
        main_signup = findViewById(R.id.main_signup);
        main_list_device = findViewById(R.id.main_list_device);
        info = findViewById(R.id.infor);

    }
    public void action(){
        main_login.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        main_signup.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });
        main_list_device.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListDeviceActivity.class);
            startActivity(intent);
        });
        info.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AppInfoActivity.class);
            startActivity(intent);
        });
    }
}