package com.example.camtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateStock extends AppCompatActivity {

    private boolean io;
    private TextView counter;
    private Button subtract;
    private Button add;
    private Button reset;
    private int counterNum;
    private TextView productName;
    private TextView productID;
    private ToggleButton stockIO;
    private Button submit;
    String prodID;

    final Activity thisContext= this;

    private View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.plusButton:
                    increment();
                    break;
                case R.id.minusButton:
                    decrement();
                    break;
                case R.id.resetButton:
                    reset();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stock);

        Bundle b = this.getIntent().getExtras();
        String target = Home.STOCK_APP_BASE_URL+"/webapis/updateStock";


    counter = (TextView) findViewById(R.id.counter);
    subtract = (Button) findViewById(R.id.minusButton);
    subtract.setOnClickListener(onClickListener);
    add = (Button) findViewById(R.id.plusButton);
    add.setOnClickListener(onClickListener);
    reset = (Button) findViewById(R.id.resetButton);
    reset.setOnClickListener(onClickListener);

    productName = (TextView) findViewById(R.id.product_name);
    productID = (TextView) findViewById(R.id.product_id);

        if(b!=null) {

            prodID = (String) b.get("productID");
            String prodName = (String) b.get("productName");

            productName.setText(prodName);
            productID.setText(prodID);

        }

        stockIO = (ToggleButton) findViewById(R.id.inOrOut);
        stockIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stockIO.isChecked()){
                    io=true;
                } else{
                    io=false;
                }
            }
        });

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(thisContext);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sessionID", Login.sessionID);
                    jsonObject.put("transactionType", io);
                    jsonObject.put("productID", prodID);
                    jsonObject.put("itemCount", counterNum);
                    jsonObject.put("username", Login.username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, target, jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String requestStatus = response.getString("requestStatus");
                                    if (requestStatus.equals("success")) {
                                        Toast.makeText(thisContext, "Item updated successfully", Toast.LENGTH_SHORT).show();
                                        Intent viewStock = new Intent(thisContext, Home.class);
                                        finish();
                                        startActivity(viewStock);
                                    }
                                    else
                                    {
                                        boolean sessionValidity=response.getBoolean("isSessionValid");
                                        String reason = response.getString("reason");
                                        if(sessionValidity){
                                            Intent goHome = new Intent(thisContext, Home.class);
                                            Toast.makeText(thisContext,reason, Toast.LENGTH_SHORT).show();
                                            startActivity(goHome);
                                        } else{
                                            Intent retryLogin = new Intent(thisContext, Login.class);
                                            Toast.makeText(thisContext,reason, Toast.LENGTH_SHORT).show();
                                            startActivity(retryLogin);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(objectRequest);
            }
        });

    }

    private void reset(){

        counterNum=0;
        counter.setText(counterNum + "");
    }

    private void increment(){
        counterNum++;
        counter.setText(counterNum + "");
    }

    private void decrement(){
        if(counterNum>0) {
            counterNum--;
            counter.setText(counterNum + "");
        }
    }

}