package com.example.android_liot;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Stack;

public class Singleton {
    private static Singleton mInstance;
    private RequestQueue mQueue;

    public boolean loggedIn = false;
    public String username;
    public String password;

    public String getSelectedIp() {
        return selectedIp;
    }

    public void setSelectedIp(String selectedIp) {
        this.selectedIp = selectedIp;
    }

    public String selectedIp;
    public Stack<JSONObject> properties = new Stack<JSONObject>();


    private Singleton(Context context) {
        mQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
    public static synchronized Singleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Singleton(context);
        }
        return mInstance;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProperties(Stack<JSONObject> properties) {
        this.properties = properties;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Stack<JSONObject> getProperties() {
        return properties;
    }

    public RequestQueue getQueue() {
        return mQueue;
    }
}

