package com.example.camtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class ViewInventory extends AppCompatActivity {
    String[] viewStockHeaders={"Product ID", "Product Name", "Current Balance"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);
        String target = Home.STOCK_APP_BASE_URL+"/webapis/viewStock";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionID", Login.sessionID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
final Activity thisContext= this;
TableLayout tl = (TableLayout)findViewById(R.id.viewStockTable);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, target, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String requestStatus = response.getString("requestStatus");
                            if (requestStatus.equals("success")) {
                                String itemsJSONArray = response.getString("stockItems");
                                ArrayList<HashMap<String, Object>> result = new ObjectMapper().readValue(itemsJSONArray,
                                        ArrayList.class);
                                String[][] listOfItems = new String[result.size()][3];
                                int rowIndex = 0;
                                for (HashMap<String, Object> row : result) {

                                    String prodID = (String) row.get("productID");
                                    String prodName = (String) row.get("productName");
                                    String currentBalance = ((Integer) row.get("currentBalance")).toString();

                                    listOfItems[rowIndex][0] = prodID;
                                    listOfItems[rowIndex][1] = prodName;
                                    listOfItems[rowIndex][2] = currentBalance;

                                    rowIndex++;
                                }
                                if (listOfItems == null || listOfItems.length == 0) {
                                    TextView textView = (TextView) findViewById(R.id.textView4);
                                    textView.setText("No Items Available!");
                                } else {
                                    updateTableData(tl, listOfItems);
                                }
                            }
                            else{
                                Toast.makeText(thisContext, "session expired!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
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

    private void updateTableData(TableLayout tl, String[][] data) {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.LTGRAY);

        TextView col0 = createHeaderColumnTextView("No.");
        headerRow.addView(col0);

        TextView col1 = createHeaderColumnTextView("Product ID");
        headerRow.addView(col1);

        TextView col2 = createHeaderColumnTextView("Product Name");
        headerRow.addView(col2);

        TextView col3 = createHeaderColumnTextView("Stock On Hand");
        headerRow.addView(col3);

        // add header row
        tl.addView(headerRow);

        // add data rows
        for(int i = 0; i < data.length; i++)
        {
            TableRow dataRow = new TableRow(this);

            TextView dataCell = createDataCellTextView(String.valueOf(i + 1),
                    View.TEXT_ALIGNMENT_INHERIT);
            dataRow.addView(dataCell);

            for(int j = 0; j < data[i].length; j++)
            {
                int textAlignment = View.TEXT_ALIGNMENT_INHERIT;
                if (j == 2)
                {
                    textAlignment = View.TEXT_ALIGNMENT_CENTER;
                }

                dataCell = createDataCellTextView(data[i][j], textAlignment);
                dataRow.addView(dataCell);
            }

            tl.addView(dataRow);
        }
    }

    private TextView createHeaderColumnTextView(String text)
    {
        TextView headerCell = new TextView(this);
        headerCell.setText(text);
        headerCell.setTypeface(headerCell.getTypeface(), Typeface.BOLD);
        headerCell.setTextColor(Color.BLACK);

        return headerCell;
    }

    private TextView createDataCellTextView(String text, int textAlignment)
    {
        TextView dataCell = new TextView(this);
        dataCell.setText(text);
        dataCell.setTextAlignment(textAlignment);
        dataCell.setTextColor(Color.BLACK);

        return dataCell;
    }

}