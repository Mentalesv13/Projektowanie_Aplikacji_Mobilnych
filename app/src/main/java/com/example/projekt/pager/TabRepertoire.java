package com.example.projekt.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.booking.SeatView;
import com.example.projekt.entity.Repertoire;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.Functions;
import com.example.projekt.login.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TabRepertoire extends Fragment {
    private DatabaseHandler db;
    String TAG;
    HashMap<Integer,Repertoire> Repertoires;
    View view;

    public TabRepertoire(String TAG) {
        this.TAG = TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = new DatabaseHandler(getContext());
        view = inflater.inflate(R.layout.repertoire_pattern, container, false);
        Repertoires = db.getRepertoireDetail(TAG);
        createRepertoireLayout();
        return view;
    }

    private void createRepertoireLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final LinearLayout abc = view.findViewById(R.id.repertoireLayout);
        Calendar calendar = Calendar.getInstance();
        if (Repertoires.size()>0) {
            for (int i = 0; i < Repertoires.size(); i++) {
                Repertoire temp = Repertoires.get(i);
                final View custom = inflater.inflate(R.layout.repertoire_element, null);

                TextView repertoireName = (TextView) custom.findViewById(R.id.repertoireName);
                TextView repertoireTime = (TextView) custom.findViewById(R.id.repertoireTime);
                TextView repertoireDate = (TextView) custom.findViewById(R.id.repertoireDate);
                TextView repertoireDay = (TextView) custom.findViewById(R.id.repertoireDay);
                final Button btnBuy = custom.findViewById(R.id.reservationBtn);
                final Button btnReserve = custom.findViewById(R.id.buyBtn);
                CardView cardBuy = custom.findViewById(R.id.cardViewBuy);
                CardView cardReserve = custom.findViewById(R.id.cardViewReservation);

                btnBuy.setTag(temp.getId_repertoire());
                btnReserve.setTag(temp.getId_repertoire());

                btnBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"Clcik",Toast.LENGTH_LONG);
                        getSeats(String.valueOf(btnBuy.getTag()));
                    }
                });

                btnReserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"Clcik",Toast.LENGTH_LONG);
                        getSeats(String.valueOf(btnReserve.getTag()));
                    }
                });

                repertoireName.setText(temp.getName_repertoire());
                String tempTime = temp.getTime_repertoire();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                String date = df.format(Calendar.getInstance().getTime());
                try {
                    Date d1 = df.parse(date);

                    Date d2 = df.parse(tempTime);

                    if(d1.compareTo(d2) > 0) {
                        cardBuy.setVisibility(View.GONE);
                        cardReserve.setVisibility(View.GONE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String tempDate = tempTime;
                tempDate = tempDate.substring(0, 10);
                tempTime = tempTime.substring(11, 16);

                //Log.d(TAG, date.toString());

                String[] dates = tempDate.split("-");
                repertoireTime.setText(tempTime);

                if (dates[2].charAt(0) == '0') {
                    repertoireDate.setText(dates[2].substring(1));
                } else repertoireDate.setText(dates[2]);

                repertoireDay.setText(Zellercongruence(Integer.parseInt(dates[2]), Integer.parseInt(dates[1]), Integer.parseInt(dates[0])));
                abc.addView(custom);
            }
        }
        else {
            final View custom = inflater.inflate(R.layout.fragment_tab_lipiec, null);
            abc.addView(custom);
        }
    }

    String Zellercongruence(int day, int month,
                         int year)
    {
        if (month == 1)
        {
            month = 13;
            year--;
        }
        if (month == 2)
        {
            month = 14;
            year--;
        }
        int q = day;
        int m = month;
        int k = year % 100;
        int j = year / 100;
        int h = q + 13*(m+1)/5 + k + k/4 + j/4 + 5*j;
        h = h % 7;
        switch (h)
        {
            case 0 : return "Sat.";
            case 1 : return "Sun.";
            case 2 : return "Mon.";
            case 3 : return "Tue.";
            case 4 : return "Wed.";
            case 5 : return "Thu.";
            case 6 : return "Fri.";
        }
        return "err";
    }


    public void getSeats(final String id){
        Log.d(TAG,"ID: " + id);
        String tag_string_req = "req_seats";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.GET_SEATS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Reset Password Response: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //boolean error = jsonObject.getBoolean("error");
                    Log.e("TAG", "Log: " + response);

//                    if (!error) {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                    }
                    String seats = (String) jsonObject.get("places");

                    Intent myIntent = new Intent(getActivity(), SeatView.class);
                    myIntent.putExtra("seats", seats);
                    startActivity(myIntent);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Reset Password Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to Seat url
                Map<String, String> params = new HashMap<>();

                params.put("id_repertoire", id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };

        // Adding request to volley request queue
        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, 0));
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
