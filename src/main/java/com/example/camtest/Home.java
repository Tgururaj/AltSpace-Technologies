package com.example.camtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class Home extends AppCompatActivity {
    //replace existing ip address with current ip address
    public static final String STOCK_APP_BASE_URL = "http://192.168.10.146:8080/stockapp";


    public Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnCamera =(Button)findViewById(R.id.scanItem);
        Button btnViewStock =(Button)findViewById(R.id.viewInventory);
        Button btnLogout =(Button)findViewById(R.id.logout);

        final Context thisActivity = this;
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deployCam = new Intent(Home.this, ScanQR.class);
                startActivity(deployCam);
            }
        });
        btnViewStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seeStock = new Intent(Home.this, ViewInventory.class);
                startActivity(seeStock);
                                    }
            });
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(Home.this, Login.class);
                startActivity(logout);
                finish();
            }
        });

    }

    }
