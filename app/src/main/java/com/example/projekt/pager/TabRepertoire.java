package com.example.projekt.pager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.projekt.helper.LoadingDialog;
import com.example.projekt.login.RequestManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TabRepertoire extends Fragment {
    private String TAG;
    private HashMap<Integer,Repertoire> Repertoires;
    private View view;
    private LoadingDialog loadingDialog;
    private  String mode;
    private String btnValue;
    private String performanceName;
    private String performanceDate;
    private String performanceTime;

    public TabRepertoire(String TAG) {
        this.TAG = TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(getActivity());
        // Inflate the layout for this fragment
        DatabaseHandler db = new DatabaseHandler(getContext());
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

                final TextView repertoireName = (TextView) custom.findViewById(R.id.repertoireName);
                final TextView repertoireTime = (TextView) custom.findViewById(R.id.repertoireTime);
                final TextView repertoireDate = (TextView) custom.findViewById(R.id.repertoireDate);
                TextView repertoireDay = (TextView) custom.findViewById(R.id.repertoireDay);

                CardView cardBuy = custom.findViewById(R.id.cardViewBuy);
                CardView cardReserve = custom.findViewById(R.id.cardViewReservation);
                final Button btnBuy = custom.findViewById(R.id.buyBtn);
                final Button btnReserve = custom.findViewById(R.id.reservationBtn);


                String tempTime = temp.getTime_repertoire();

                final String tmpDate = tempTime.substring(0, 10);


                btnBuy.setTag(temp.getId_repertoire());
                btnReserve.setTag(temp.getId_repertoire());

                btnBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnValue = String.valueOf(btnBuy.getTag());
                        mode = "U";
                        performanceName = repertoireName.getText().toString();
                        performanceDate = tmpDate;
                        performanceTime = repertoireTime.getText().toString();

                        //getSeats(String.valueOf(btnBuy.getTag()),"U");
                        final seatTask sT= new seatTask();
                        sT.execute();
                    }
                });

                btnReserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mode = "R";
                        btnValue = String.valueOf(btnBuy.getTag());
                        performanceName = repertoireName.getText().toString();
                        performanceDate = tmpDate;
                        performanceTime = repertoireTime.getText().toString();
                        final seatTask sT= new seatTask();
                        sT.execute();
                    }
                });

                repertoireName.setText(temp.getName_repertoire());

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

                String tempDate = tempTime.substring(0, 10);
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

    public void getSeats(){
        //Log.d(TAG,"ID: " + id);
        String tag_string_req = "req_seats";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.GET_SEATS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Reset Password Response: " + response);
                JSONParser parser = new JSONParser();
                try {
                    Log.e("TAG", "Log: " + response);
                    Object obj = parser.parse(response);

                    JSONObject jsonObject = (JSONObject) obj;
                    //boolean error = jsonObject.getBoolean("error");
                    Log.e("TAG", "Log: " + response);

//                    if (!error) {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                    }

                    //String seats = "{\"Id_Performance\": \"6\"+\"Mode\":\"" + Mode + "\",\"Places:\"";
                    //seats = seats + (String) jsonObject.get("places") +"/}";
                    String seats = (String) jsonObject.get("places");
                    String price = (String) jsonObject.get("price");
                    JSONArray discountList = (JSONArray) jsonObject.get("discount");

                    Iterator i = discountList.iterator();
                    Double firstZone = null;
                    Double secondZone = null;
                    Double thirdZone = null;
                    Double fourthZone = null;

                    Double firstZoneR = null;
                    Double secondZoneR = null;
                    Double thirdZoneR = null;
                    Double fourthZoneR = null;

                    while (i.hasNext()) {
                        org.json.simple.JSONObject discount = (org.json.simple.JSONObject) i.next();
                        String name = (String) discount.get("name");
                        if(name.contains("first_zone_normal")) {
                            firstZone = (Double) discount.get("value");
                        }
                        else if(name.contains("second_zone_normal")){
                            secondZone = (Double) discount.get("value");
                        }
                        else if(name.contains("third_zone_normal")){
                            thirdZone = (Double) discount.get("value");
                        }
                        else if(name.contains("fourth_zone_normal")) {
                            fourthZone = (Double) discount.get("value");
                        }
                        else if(name.contains("first_zone_reduced")){
                            firstZoneR = (Double) discount.get("value");
                        }
                        else if(name.contains("second_zone_reduced")){
                            secondZoneR= (Double) discount.get("value");
                        }
                        else if(name.contains("third_zone_reduced")) {
                            thirdZoneR = (Double) discount.get("value");
                        }
                        else if(name.contains("fourth_zone_reduced")){
                            fourthZoneR = (Double) discount.get("value");
                        }

                    }

                    Intent myIntent = new Intent(getActivity(), SeatView.class);
                    myIntent.putExtra("seats", seats);
                    myIntent.putExtra("id", String.valueOf(btnValue));
                    myIntent.putExtra("performancename", performanceName);
                    myIntent.putExtra("performancedate", performanceDate);
                    myIntent.putExtra("performancetime", performanceTime);
                    myIntent.putExtra("price", price);
                    myIntent.putExtra("first", firstZone);
                    myIntent.putExtra("second", secondZone);
                    myIntent.putExtra("third", thirdZone);
                    myIntent.putExtra("fourth", fourthZone);
                    myIntent.putExtra("firstR", firstZoneR);
                    myIntent.putExtra("secondR", secondZoneR);
                    myIntent.putExtra("thirdR", thirdZoneR);
                    myIntent.putExtra("fourthR", fourthZoneR);
                    myIntent.putExtra("mode", mode);

                    startActivity(myIntent);
                    loadingDialog.hideDialog();
                } catch (org.json.simple.parser.ParseException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                    loadingDialog.hideDialog();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                loadingDialog.hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to Seat url
                Map<String, String> params = new HashMap<>();
                params.put("id_performance", btnValue);
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

    class seatTask extends AsyncTask<Void,Void,Void> {
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

        @SuppressLint("UseSparseArrays")
        @Override
        protected Void doInBackground(Void... voids) {

        getSeats();
        return null;
        }
    }
}
