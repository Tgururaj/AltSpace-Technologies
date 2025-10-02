package com.example.camtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    public static String sessionID;
    public static String username;

    EditText editTextUsername;
    EditText editTextPassword;
    Button getCreds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
            editTextUsername=(EditText)findViewById(R.id.username);
            editTextPassword=(EditText)findViewById(R.id.password);
            getCreds=(Button)findViewById(R.id.login);


            final Context thisActivity = this;
            getCreds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = editTextUsername.getText().toString();
                    String password = editTextPassword.getText().toString();

                    String target = Home.STOCK_APP_BASE_URL+"/webapis/login";

                    RequestQueue requestQueue = Volley.newRequestQueue(thisActivity);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username", username);
                        jsonObject.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Login.username = username;
    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, target, jsonObject,
        new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
               try {
                   String requestStatus = response.getString("requestStatus");
                    if (requestStatus.equals("success")) {
                        sessionID= response.getString("sessionID");
                        Intent goHome = new Intent(thisActivity, Home.class);
                        startActivity(goHome);
                        finish();
                        }
                    else{
                        Intent retryLogin = new Intent(thisActivity, Login.class);
                        startActivity(retryLogin);
                        Toast.makeText(thisActivity, "incorrect username and password",
                                Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(thisActivity, "Error occurred while logging in!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(objectRequest);
                }
            });
         }
      }