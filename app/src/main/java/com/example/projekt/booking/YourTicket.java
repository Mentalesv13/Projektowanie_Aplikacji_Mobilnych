package com.example.projekt.booking;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.SlideMain;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.Functions;
import com.example.projekt.helper.LoadingDialog;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.login.RequestManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.projekt.login.RequestManager.TAG;

public class YourTicket extends AppCompatActivity {
    private DatabaseHandler db;
    SessionManager session;
    LoadingDialog loadingDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String email;
    Button btnBack;

    int images[] = {R.drawable.people, R.drawable.premiere, R.drawable.megaphone, R.drawable.haze};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ticket);
        db = new DatabaseHandler(this);
        session = new SessionManager(this);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0,0);
            }
        });
        email = db.getEmail();
        Log.e("TICKET", email);
        loadingDialog = new LoadingDialog(this);
        mSwipeRefreshLayout = findViewById(R.id.swifeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.black, android.R.color.holo_red_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final TicketTask task = new TicketTask();
                task.execute();
            }
        });
            final TicketTask task = new TicketTask();
            task.execute();
    }


    class TicketTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            loadingDialog.showDialog();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected Void doInBackground(Void... voids) {



            String tag_string_req = "req_get_event";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    Functions.GET_ORDERS_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    JSONParser parser = new JSONParser();

                    try {
                        Object obj = parser.parse(response);

                        JSONObject jsonObject = (JSONObject) obj;

                        String error = (String) jsonObject.get("error");
                        Log.d(TAG, "Error: " + error);
                        Log.d(TAG, "response: " + response);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final LinearLayout abc = findViewById(R.id.llayout);



                        abc.removeAllViews();
                        JSONArray ordersList = (JSONArray) jsonObject.get("orders");

                        Iterator i = ordersList.iterator();

                        while (i.hasNext()) {
                            JSONObject events = (JSONObject) i.next();

                            String name = (String) events.get("performanceName");
                            String date = (String) events.get("performanceDate");
                            //String time = (String) events.get("performanceTime");
                            String order = (String) events.get("orderId");
                            String status = (String) events.get("status");
                            JSONArray ticketsList = (JSONArray) events.get("tickets");


                            final View custom = inflater.inflate(R.layout.order_layout, null);

                            final LinearLayout abc1 = custom.findViewById(R.id.pof_llayout);


                            Iterator j = ticketsList.iterator();

                            while (j.hasNext()) {
                                final View custom1 = inflater.inflate(R.layout.buy_element, null);
                                JSONObject tickets = (JSONObject) j.next();

                                String no = (String) tickets.get("place");
                                String sector = (String) tickets.get("sector");
                                String row = (String) tickets.get("row");
                                String id_seat_type = (String) tickets.get("id_seat_type");


                                final Switch switchReduced = custom1.findViewById(R.id.switchReduced);
                                final TextView seatPrice = custom1.findViewById(R.id.seatPrice);
                                final TextView textView4 = custom1.findViewById(R.id.textView4);
                                seatPrice.setVisibility(View.GONE);
                                textView4.setVisibility(View.GONE);
                                switchReduced.setEnabled(false);
                                if (id_seat_type.equals("5") || id_seat_type.equals("5") || id_seat_type.equals("5") || id_seat_type.equals("5"))
                                    switchReduced.setChecked(true);

                                final TextView seatRow = custom1.findViewById(R.id.seatRow);
                                final TextView seatPosition = custom1.findViewById(R.id.seatPosition);
                                final TextView seatNo = custom1.findViewById(R.id.seatNo);

                                seatRow.setText(row);
                                seatPosition.setText(sector);
                                seatNo.setText(no);

                                abc1.addView(custom1);
                            }



                            TextView Order = (TextView) custom.findViewById(R.id.Order);
                            TextView performanceName = (TextView) custom.findViewById(R.id.performanceName);
                            TextView performanceDate = (TextView) custom.findViewById(R.id.performanceDate);
                            //TextView performanceTime = (TextView) custom.findViewById(R.id.performanceTime);

                            performanceName.setText(name);
                            performanceDate.setText(date);
                            //performanceTime.setText(time);
                            Order.setText("#" + order + " " + status);
                            abc.addView(custom);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);

                        loadingDialog.hideDialog();
                    } catch (Exception e) {
                        Log.d(TAG, "ERROR");

                        e.printStackTrace();
                        loadingDialog.hideDialog();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Events error: " + error.getMessage());
                    session.setEvent(false);
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                    loadingDialog.hideDialog();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userEmail", email);

                    return params;
                }

            };
            // Adding request to request queue
            RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SlideMain.class);
        startActivity(intent);
        finish();
    }
}
