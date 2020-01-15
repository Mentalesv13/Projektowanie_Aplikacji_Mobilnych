package com.example.projekt.booking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projekt.R;
import com.example.projekt.SlideMain;
import com.example.projekt.entity.Seat;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.Functions;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.login.RequestManager;
import com.example.projekt.payment.Config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.ROUND_HALF_UP;

public class SeatView extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SeatView.class.getSimpleName();

    private String seats = null;
    private String mode = null;
    private String price = null;
    private Double first = null;
    private Double second = null;
    private Double third = null;
    private Double fourth = null;
    private Double firstR = null;
    private Double secondR = null;
    private Double thirdR = null;
    private Double fourthR = null;
    private Double results = null;
    private String idPerformance = null;
    private String performanceName = null;
    private String performanceDate = null;
    private String performanceTime = null;
    private String orderId = null;

    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);

    private static final int REQUEST_CODE_PAYMENT = 1;

    int seatSize = 90;
    int seatGaping = 5;
    boolean doneFlag = false;
    boolean closeFlag = false;

    int STATUS_FIRST = 1;
    int STATUS_SECOND = 2;
    int STATUS_THIRD = 3;
    int STATUS_FOURTH = 4;
    int STATUS_BOOKED = 5;
    int STATUS_RESERVED = 6;
    char selectedCount = 0;
    String selectedIds = "";
    String email;
    boolean firstFlag = false;

    private ProgressDialog pDialog;
    private DatabaseHandler db;
    private SessionManager session;
    private String exaltedOrb = null;

    HashSet<Integer> seatID = new HashSet<>();
    HashMap<Integer, Seat> seatsMap;
    HashMap<Integer,Integer> seatsString;
    HashMap<Integer,Integer> seatsID;
    HashMap<Integer,Double> seatsType;
    HashMap<Integer,Character> seatsTag;
    List<TextView> seatViewList = new ArrayList<>();

    final DecimalFormat df = new DecimalFormat("####0.00");
    private static final String FORMAT = "%02d:%02d";

    JSONObject jsonSeat;
    JSONObject jsonRaw;
    JSONObject jsonFinal;
    JSONObject jsonBuy = new JSONObject();
    JSONArray jsonArray;
    JSONArray jsonArray1;
    JSONArray jsonArray2;

    ViewGroup layout;
    FloatingActionButton seatFab;
    TextView firstPrice, secondPrice, thirdPrice, fourthPrice;
    Button btnBack;
    TextView processing;

    long time = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        Bundle b = getIntent().getExtras();
        session = new SessionManager(getApplicationContext());
        seats = b.getString("seats");
        mode = b.getString("mode");
        first = b.getDouble("first");
        second = b.getDouble("second");
        third = b.getDouble("third");
        fourth = b.getDouble("fourth");
        firstR = b.getDouble("firstR");
        secondR = b.getDouble("secondR");
        thirdR = b.getDouble("thirdR");
        fourthR = b.getDouble("fourthR");
        price = b.getString("price");
        idPerformance = b.getString("id");
        performanceName = b.getString("performancename");
        performanceDate = b.getString("performancedate");
        performanceTime = b.getString("performancetime");
        time = 900000;
        results = 0.0;
        setContentView(R.layout.seatlayout);
        processing = findViewById(R.id.processingText);

        db = new DatabaseHandler(getApplicationContext());

        seatsMap = new HashMap<Integer, Seat>();
        seatsType = new HashMap<Integer, Double>();
        seatsTag = new HashMap<Integer, Character>();

        seatsType.put(STATUS_FIRST, first);
        seatsType.put(STATUS_SECOND, second);
        seatsType.put(STATUS_THIRD, third);
        seatsType.put(STATUS_FOURTH, fourth);

        seatsType.put(STATUS_BOOKED, 0.0);
        seatsType.put(STATUS_RESERVED,0.0);
        seatsTag.put(STATUS_FIRST, 'F');
        seatsTag.put(STATUS_SECOND, 'S');
        seatsTag.put(STATUS_THIRD, 'T');
        seatsTag.put(STATUS_FOURTH, 'V');

        firstPrice = findViewById(R.id.firstPrice);
        secondPrice = findViewById(R.id.secondPrice);
        thirdPrice = findViewById(R.id.thirdPrice);
        fourthPrice = findViewById(R.id.fourthPrice);

        firstPrice.setText(df.format(first*Double.parseDouble(price)) + " PLN");
        secondPrice.setText(price + " PLN");
        thirdPrice.setText(df.format(third*Double.parseDouble(price)) + " PLN");
        fourthPrice.setText(df.format(fourth*Double.parseDouble(price)) + " PLN");



        seatFab = findViewById(R.id.fabSeat);
        seatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seatFab.setEnabled(false);
                jsonSeat = new JSONObject();
                jsonRaw = new JSONObject();
                try {
                    jsonSeat.put("Id_Performance", idPerformance);
                    jsonSeat.put("Mode", mode);
                    jsonRaw.put("id_performance", idPerformance);
                } catch (JSONException e) {
                    seatFab.setEnabled(true);
                    e.printStackTrace();
                }
                if (seatID.size()!=0) {
                    processing.setVisibility(View.VISIBLE);
                    btnBack.setVisibility(View.INVISIBLE);

                    Iterator value = seatID.iterator();

                    try {
                        jsonArray = new JSONArray();
                        jsonArray1 = new JSONArray();
                        while (value.hasNext()) {
                            JSONObject jsonPlaces=new JSONObject();
                            JSONObject jsonPlaces1=new JSONObject();
                            String values = String.valueOf(value.next());

                            Seat temp = seatsMap.get(Integer.parseInt(values));

                            jsonPlaces.put("place_string", String.valueOf(temp.getSeatString()));
                            jsonPlaces.put("place_no", String.valueOf(temp.getSeatNumber()));
                            jsonPlaces.put("place_id", String.valueOf(temp.getSeat_id()));
                            jsonPlaces.put("place_row", String.valueOf(temp.getRow_id()));
                            jsonPlaces.put("place_type", (seatsTag.get(temp.getSeatTag()).toString()));


                            jsonPlaces1.put("id_seat_type", (temp.getSeatTag()));
                            jsonPlaces1.put("place_number", String.valueOf(temp.getSeatString()));
                            jsonPlaces1.put("sector", String.valueOf(temp.getSeatType()));
                            jsonPlaces1.put("place", String.valueOf(temp.getSeatNumber()));
                            jsonPlaces1.put("row", String.valueOf(temp.getRow_id()));
                            jsonArray1.put(jsonPlaces1);
                            jsonArray.put(jsonPlaces);
                        }


                        jsonSeat.put("Places", jsonArray);
                        jsonRaw.put("tickets", jsonArray1);

                        //Log.d("TAG", jsonRaw.toString());

                    } catch (JSONException e) {
                        Toast.makeText(SeatView.this,"Connection problem", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    JsonObjectRequest jsonObjReq1 = new JsonObjectRequest(Request.Method.POST,
                            Functions.POST_TICKET, jsonRaw,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Ticket", response.toString());

                                    try {
                                        orderId = response.getString("id_order");

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                        seatFab.setEnabled(true);
                                        processing.setVisibility(View.INVISIBLE);
                                        btnBack.setVisibility(View.VISIBLE);
                                        Toast.makeText(SeatView.this,"Connection problem", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            seatFab.setEnabled(true);
                            processing.setVisibility(View.INVISIBLE);
                            btnBack.setVisibility(View.VISIBLE);
                            Toast.makeText(SeatView.this,"Connection problem", Toast.LENGTH_LONG).show();
                            VolleyLog.d("TAG", "Error: " + error.getMessage());
                        }
                    }) {

                        /**
                         * Passing some request headers
                         * */
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };

                    jsonObjReq1.setTag("TAG");
                    // Adding request to request queue
                    queue.add(jsonObjReq1);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Functions.POST_SEAT_URL, jsonSeat,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("TAG", response.toString());

                                try {
                                    if (response.getString("error").contains("false")){ time = 900000; closeFlag = false; buyLayout();}
                                    else{Toast.makeText(getApplicationContext(),"The selected seat is reserved.",Toast.LENGTH_LONG).show();
                                    seats = response.getString("places");
                                    createLayout();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    seatFab.setEnabled(true);
                                    processing.setVisibility(View.INVISIBLE);
                                    btnBack.setVisibility(View.VISIBLE);
                                    Toast.makeText(SeatView.this,"Connection problem", Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        seatFab.setEnabled(true);
                        processing.setVisibility(View.INVISIBLE);
                        btnBack.setVisibility(View.VISIBLE);
                        Toast.makeText(SeatView.this,"Connection problem", Toast.LENGTH_LONG).show();
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                    }
                }) {

                    /**
                     * Passing some request headers
                     * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };

                jsonObjReq.setTag("TAG");
                // Adding request to request queue
                queue.add(jsonObjReq);

                }
                else {
                    Toast.makeText(SeatView.this,"No seat selected.", Toast.LENGTH_LONG).show();
                    seatFab.setEnabled(true);
                }
            }
        });
        layout = findViewById(R.id.layoutSeat);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                SeatView.this.onBackPressed();
            }
        });


        seats = "/" + seats;

        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(8 * seatGaping, 8 * seatGaping, 8 * seatGaping, 8 * seatGaping);
        layout.addView(layoutSeat);

        LinearLayout layout = null;

        int count = 0;
        int row = 1;
        int seat = 1;
        seatsString = new HashMap<>();
        seatsID = new HashMap<>();
        Boolean balcony = false;
        for (int index = 0; index < seats.length(); index++) {
            String seatType = null;
            if (seats.charAt(index) == '/') {
                if (row==21) {row=1; balcony=true;}
                if (firstFlag){
                    TextView view = new TextView(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                    layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                    view.setLayoutParams(layoutParams);
                    view.setPadding(0, 0, 0, 2 * seatGaping);
                    view.setGravity(Gravity.CENTER);
                    view.setTextColor(Color.BLACK);
                    view.setText(row + "");
                    row++;
                    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                    layout.addView(view);
                    firstFlag = false;
                }

                seat = 1;
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutSeat.addView(layout);

                if (index+6<seats.length()) {
                    if ((seats.charAt(index + 10) != '_' || seats.charAt(index + 15) != '_' || seats.charAt(index + 30) != '_') || seats.charAt(index + 40) != '_') {
                        TextView view = new TextView(this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                        layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                        view.setLayoutParams(layoutParams);
                        view.setPadding(0, 0, 0, 2 * seatGaping);
                        view.setGravity(Gravity.CENTER);
                        view.setTextColor(Color.BLACK);
                        view.setText(row + "");
                        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                        layout.addView(view);
                        firstFlag = true;
                    }
                }

            } else if (seats.charAt(index) == 'U') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_booked);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_BOOKED);
                //view.setText(count + "");
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;

            } else if (seats.charAt(index) == 'F') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_book);
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.BLACK);
                view.setTag(STATUS_FIRST);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }else if (seats.charAt(index) == 'S') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_secondzone);
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_SECOND);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }else if (seats.charAt(index) == 'T') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_thirdzone);
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_THIRD);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }
            else if (seats.charAt(index) == 'V') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);

                view.setBackgroundResource(R.drawable.ic_seats_vierzone);
                view.setText(seat + "");
                ;
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_FOURTH);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }

            else if (seats.charAt(index) == 'R') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_reserved);
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_RESERVED);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            } else if (seats.charAt(index) == '_') {
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setText("");
                layout.addView(view);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if ((int) view.getTag() == STATUS_FIRST) {
            if (selectedIds.contains(view.getId() + ",") ) {
                    selectedIds = selectedIds.replace(+view.getId() + ",", "");
                    view.setBackgroundResource(R.drawable.ic_seats_book);
                    selectedCount--;
                seatID.remove(view.getId());
            } else {

            if (selectedCount<8) {
                selectedIds = selectedIds + view.getId() + ",";
                view.setBackgroundResource(R.drawable.ic_seats_selected);
                selectedCount++;
                seatID.add(view.getId());
            }
            else {Toast.makeText(this, "Transaction places limit is 8 items", Toast.LENGTH_SHORT).show();}
            }
        }    else if ((int) view.getTag() == STATUS_SECOND) {
            if (selectedIds.contains(view.getId() + ",") ) {
                selectedIds = selectedIds.replace(+view.getId() + ",", "");
                view.setBackgroundResource(R.drawable.ic_seats_secondzone);
                selectedCount--;
                seatID.remove(view.getId());
            } else {

                if (selectedCount<8) {
                    selectedIds = selectedIds + view.getId() + ",";
                    view.setBackgroundResource(R.drawable.ic_seats_selected);
                    selectedCount++;
                    seatID.add(view.getId());
                }
                else {Toast.makeText(this, "Transaction places limit is 8 items", Toast.LENGTH_SHORT).show();}
            }
        }
        else if ((int) view.getTag() == STATUS_THIRD) {
            if (selectedIds.contains(view.getId() + ",") ) {
                selectedIds = selectedIds.replace(+view.getId() + ",", "");
                view.setBackgroundResource(R.drawable.ic_seats_thirdzone);
                selectedCount--;
                seatID.remove(view.getId());
            } else {

                if (selectedCount<8) {
                    selectedIds = selectedIds + view.getId() + ",";
                    view.setBackgroundResource(R.drawable.ic_seats_selected);
                    selectedCount++;
                    seatID.add(view.getId());
                }
                else {Toast.makeText(this, "Transaction places limit is 8 items", Toast.LENGTH_SHORT).show();}
            }
        }
        else if ((int) view.getTag() == STATUS_FOURTH) {
            if (selectedIds.contains(view.getId() + ",") ) {
                selectedIds = selectedIds.replace(+view.getId() + ",", "");
                view.setBackgroundResource(R.drawable.ic_seats_vierzone);
                selectedCount--;
                seatID.remove(view.getId());
            } else {

                if (selectedCount<8) {
                    selectedIds = selectedIds + view.getId() + ",";
                    view.setBackgroundResource(R.drawable.ic_seats_selected);
                    selectedCount++;
                    seatID.add(view.getId());
                }
                else {Toast.makeText(this, "Transaction places limit is 8 items", Toast.LENGTH_SHORT).show();}
            }
        }
        else if ((int) view.getTag() == STATUS_BOOKED) {
            Toast.makeText(this, "Seat " + view.getId() + " is Booked", Toast.LENGTH_SHORT).show();
        } else if ((int) view.getTag() == STATUS_RESERVED) {
            Toast.makeText(this, "Seat " + view.getId() + " is Reserved", Toast.LENGTH_SHORT).show();
        }
    }

    public void buyLayout() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SeatView.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.buy_layout, null);
        results = 0.0;
        final TextView resultPrice = dialogView.findViewById(R.id.priceAll);
        final TextView resultTax = dialogView.findViewById(R.id.Tax);
        final TextView prices = dialogView.findViewById(R.id.price);

        final TableRow tableRow = dialogView.findViewById(R.id.tableRow);
        final TableRow tableRowL = dialogView.findViewById(R.id.tableRowL);

        if (session.isLoggedIn()){
            tableRow.setVisibility(View.INVISIBLE);
            tableRowL.setVisibility(View.VISIBLE);
        } else {
            tableRowL.setVisibility(View.INVISIBLE);
            tableRow.setVisibility(View.VISIBLE);
        }

        final LinearLayout abc = dialogView.findViewById(R.id.pof_llayout);

        TextView namePerformance = dialogView.findViewById(R.id.performanceName);
        TextView datePerformance = dialogView.findViewById(R.id.performanceDate);
        TextView timePerformance = dialogView.findViewById(R.id.Price);

        namePerformance.setText(performanceName);
        datePerformance.setText(performanceDate);
        timePerformance.setText(performanceTime);


        final TextView buyTime = dialogView.findViewById(R.id.buyTime);

        //abc.removeAllViews();

        try {

                for (int i = 0; i < jsonArray.length(); i++) {

                    jsonBuy.put("Id_Performance", idPerformance);
                    jsonBuy.put("Mode", mode);

                    JSONObject places = jsonArray.getJSONObject(i);

                    final View custom = inflater.inflate(R.layout.buy_element, null);

                    String no = (String) places.get("place_no");
                    final String id = (String) places.get("place_id");
                    String row = (String) places.get("place_row");
                    Seat temp = seatsMap.get(Integer.parseInt(id));
                    final Switch switchReduced = custom.findViewById(R.id.switchReduced);
                    final TextView seatPrice = custom.findViewById(R.id.seatPrice);
                    seatPrice.setText(df.format(temp.getSeatPrice()) + " PLN");
                    switchReduced.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Seat temp1 = seatsMap.get(Integer.parseInt(id));

                            seatsMap.remove(Integer.parseInt(id));
                            temp1.setSeatReduced(!temp1.isSeatReduced());
                            if(isChecked) {
                                Double tempValue = temp1.getSeatPrice()*secondR;
                                results = results - temp1.getSeatPrice();
                                results = results + tempValue;
                                if (temp1.getSeatTag() <=4)
                                temp1.setSeatTag(temp1.getSeatTag()+4);
                                temp1.setSeatPrice(tempValue);
                                seatPrice.setText(df.format(tempValue) + " PLN");
                            }
                            else{
                                temp1.setSeatTag(temp1.getSeatTag()-4);
                                results = results - temp1.getSeatPrice();
                                results = results + temp1.getSeatPrice()/secondR;
                                temp1.setSeatPrice(temp1.getSeatPrice()/secondR);

                                seatPrice.setText(df.format(temp1.getSeatPrice()) + " PLN");
                            }

                            seatsMap.put(Integer.parseInt(id),temp1);
                            prices.setText(df.format(results) + " PLN");
                            resultTax.setText(df.format(results*0.08) + " PLN");
                            resultPrice.setText(df.format(results + results*0.08)+ " PLN");
                        }
                    });


                    final TextView seatRow = custom.findViewById(R.id.seatRow);
                    final TextView seatPosition = custom.findViewById(R.id.seatPosition);
                    final TextView seatNo = custom.findViewById(R.id.seatNo);

                    seatRow.setText(row);
                    seatNo.setText(no);
                    seatPosition.setText(temp.getSeatType());
                    results = results + temp.getSeatPrice();
                    abc.addView(custom);

                }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        prices.setText(df.format(results) + " PLN");
        resultTax.setText(df.format(results*0.08) + " PLN" );
        resultPrice.setText(df.format(results + results*0.08) + " PLN");

        dialogBuilder.setView(dialogView);
        //dialogBuilder.setTitle("Select date");
        dialogBuilder.setCancelable(true);



        dialogBuilder.setPositiveButton("Continue...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty

            }
        });


        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                closeFlag = true;
                seatCancel();
                orderCancel();
                seatFab.setEnabled(true);
                processing.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                dialog.dismiss();

            }
        });


        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                closeFlag = true;
                seatCancel();
                orderCancel();
                seatFab.setEnabled(true);
                processing.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(true);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Iterator value = seatID.iterator();
//                    json_places = "{\"Id_Performance\":\""+idPerformance+"\", \"Mode\": \""+mode + "\", \"Places\":[";
//                    json_places = json_places + "{\"place_no\":\"" + String.valueOf(value.next()) + "\"}";
//                    while (value.hasNext()) {
//                        json_places = json_places + ",{\"place_no\":\"" + String.valueOf(value.next()) + "\"}";
//                        }
//                    json_places = json_places + "]}";

                        jsonFinal = new JSONObject();

                        try {
                            jsonFinal.put("orderID", orderId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            jsonArray2 = new JSONArray();
                            while (value.hasNext()) {
                                JSONObject jsonPlaces=new JSONObject();
                                String values = String.valueOf(value.next());

                                Seat temp = seatsMap.get(Integer.parseInt(values));

                                jsonPlaces.put("id_seat_type", (temp.getSeatTag()));
                                jsonPlaces.put("place_id", String.valueOf(temp.getSeat_id()));
                                jsonPlaces.put("place_number", String.valueOf(temp.getSeatString()));
                                jsonPlaces.put("sector", String.valueOf(temp.getSeatType()));
                                jsonPlaces.put("place", String.valueOf(temp.getSeatNumber()));
                                jsonPlaces.put("row", String.valueOf(temp.getRow_id()));
                                jsonArray2.put(jsonPlaces);
                            }

                            jsonFinal.put("tickets", jsonArray2);
                            Log.d("ABC",jsonFinal.toString());

                        } catch (JSONException e) {
                            Toast.makeText(SeatView.this,"Connection problem", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        updateTicket();

                        if(session.isLoggedIn()){
                            email = db.getEmail();
                            if (!mode.equals("R")){
                            launchPayPalPayment();}
                            else {
                                sendTicket();
                                confirmLayout();
                            }
                        }

                        else  {
                            dialog.dismiss();
                            createNoAccountBuyLayout();
                        }

                    }
                });
            }
        });

        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                buyTime.setText("Remaining time: " + getFormattedTimeString(millisUntilFinished/1000));
                time = millisUntilFinished;
            }
            public void onFinish() {
                sessionTimeout();
            }
        }.start();

        alertDialog.show();
    }

    private void updateTicket() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq1 = new JsonObjectRequest(Request.Method.POST,
                Functions.POST_UTICKET, jsonFinal,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("UTicket", response.toString());
                        Log.d("UTicket", jsonFinal.toString());


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //seatFab.setEnabled(true);
                //processing.setVisibility(View.INVISIBLE);
                //btnBack.setVisibility(View.VISIBLE);
                Toast.makeText(SeatView.this,"Connection problem", Toast.LENGTH_LONG).show();
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq1.setTag("UTICKET");
        // Adding request to request queue
        queue.add(jsonObjReq1);
    }

    private void createAccount() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SeatView.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.booking_register_fragment, null);
        final RelativeLayout registerLayout = dialogView.findViewById(R.id.registerLayout);
        final RelativeLayout loginLayout = dialogView.findViewById(R.id.loginLayout);
        final RelativeLayout emailLayout = dialogView.findViewById(R.id.emailLayout);
        loginLayout.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
        emailLayout.setVisibility(View.GONE);
        //results = 0.0;
        //final TextView resultPrice = dialogView.findViewById(R.id.priceAll);

        final String KEY_UID = "uid";
        final String KEY_NAME = "name";
        final String KEY_EMAIL = "email";
        final String KEY_CREATED_AT = "created_at";
        final TextView buyTime = dialogView.findViewById(R.id.buyTime);
        final TextView tForgot = dialogView.findViewById(R.id.tForgot);

        final EditText inputName = dialogView.findViewById(R.id.etFullname);
        final EditText inputEmail = dialogView.findViewById(R.id.etLogin);
        final EditText textVerifyCode = dialogView.findViewById(R.id.etCode);
        final EditText inputPassword = dialogView.findViewById(R.id.etPass);

        final TextView otpCountDown = findViewById(R.id.tCountDown);

        final Button btnRegister = dialogView.findViewById(R.id.btnRegister);
        final Button btnLogin = dialogView.findViewById(R.id.btnLogin);
        final Button btnVerify = dialogView.findViewById(R.id.btnVerify);
        tForgot.setVisibility(View.INVISIBLE);
        final TextView tLogin = dialogView.findViewById(R.id.tResend);
        final TextView btnResend = dialogView.findViewById(R.id.tResend2);
        //FrameLayout frameLayoutLogin = dialogView.findViewById(R.id.)

        //abc.removeAllViews();
        tLogin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        registerLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
        emailLayout.setVisibility(View.GONE);
    }
});
        final TextView tRegister = dialogView.findViewById(R.id.tResend1);
        //FrameLayout frameLayoutLogin = dialogView.findViewById(R.id.)

        //abc.removeAllViews();
        tRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
                emailLayout.setVisibility(View.GONE);
            }
        });

        btnResend.setEnabled(false);
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tag_string_req = "req_resend_code";

                pDialog.setMessage("Resending code ...");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Functions.OTP_VERIFY_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Resend Code Response: " + response);
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                Toast.makeText(getApplicationContext(), "Code successfully sent to your email!", Toast.LENGTH_LONG).show();
                                btnResend.setEnabled(false);
                                new CountDownTimer(70000, 1000) { // adjust the milli seconds here

                                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                    public void onTick(long millisUntilFinished) {
                                        otpCountDown.setVisibility(View.VISIBLE);
                                        otpCountDown.setText(""+String.format(FORMAT,
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) ));
                                    }

                                    public void onFinish() {
                                        otpCountDown.setVisibility(View.GONE);
                                        btnResend.setEnabled(true);
                                    }
                                }.start();
                            } else {
                                Toast.makeText(getApplicationContext(), "Code sending failed!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Resend Code Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("tag", "resend_code");
                        params.put("email", email);

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }

                };
                // Adding request to request queue
                int socketTimeout = 15000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strReq.setRetryPolicy(policy);
                RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });



        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String otp = textVerifyCode.getText().toString();
                String tag_string_req = "req_verify_code";

                pDialog.setMessage("Checking in ...");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Functions.OTP_VERIFY_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Verification Response: " + response);
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                JSONObject json_user = jObj.getJSONObject("user");

                                Functions logout = new Functions();
                                logout.logoutUser(getApplicationContext());
                                db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                                session.setLogin(true);

                                if(session.isLoggedIn()){
                                    email = db.getEmail();
                                    if (!mode.equals("R")){
                                        launchPayPalPayment();}
                                    else {
                                        sendTicket();
                                        confirmLayout();
                                    }
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Verification Code", Toast.LENGTH_LONG).show();
                                textVerifyCode.setError("Invalid Verification Code");
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Verify Code Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<>();

                        params.put("tag", "verify_code");
                        params.put("email", email);
                        params.put("otp", otp);

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }

                };
                // Adding request to request queue
                int socketTimeout = 15000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strReq.setRetryPolicy(policy);
                RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });

        dialogBuilder.setView(dialogView);
        //dialogBuilder.setTitle("Select date");
        dialogBuilder.setCancelable(true);

        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                dialog.dismiss();

            }
        });



        final TextView inputEmailL = dialogView.findViewById(R.id.etEmail);
        final TextView inputPasswordL = dialogView.findViewById(R.id.etPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmailL.getText().toString().trim();
                final String password = inputPasswordL.getText().toString().trim();
                String tag_string_req = "req_login";

                pDialog.setMessage("Logging in ...");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Functions.LOGIN_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response);
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                // user successfully logged in
                                JSONObject json_user = jObj.getJSONObject("user");

                                Functions logout = new Functions();
                                logout.logoutUser(getApplicationContext());

                                    db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                                    session.setLogin(true);

                                if(session.isLoggedIn()){
                                    email = db.getEmail();
                                    if (!mode.equals("R")){
                                        launchPayPalPayment();}
                                    else {
                                        sendTicket();
                                        confirmLayout();
                                    }
                                }


                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", email);
                        params.put("password", password);

                        return params;
                    }

                };

                int socketTimeout = 15000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strReq.setRetryPolicy(policy);
                // Adding request to request queue
                RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);


            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                    final String name = inputName.getText().toString().trim();
                    email = inputEmail.getText().toString().trim();
                    final String password = inputPassword.getText().toString().trim();

                    // Check for empty data in the form
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                        if (Functions.isValidEmailAddress(email)) {
                            String tag_string_req = "req_register";

                            pDialog.setMessage("Registering ...");
                            showDialog();

                            StringRequest strReq = new StringRequest(Request.Method.POST,
                                    Functions.REGISTER_URL, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "Register Response: " + response);
                                    hideDialog();

                                    try {
                                        JSONObject jObj = new JSONObject(response);
                                        boolean error = jObj.getBoolean("error");
                                        if (!error) {
                                            Functions logout = new Functions();
                                            logout.logoutUser(getApplicationContext());
                                            registerLayout.setVisibility(View.GONE);
                                            emailLayout.setVisibility(View.VISIBLE);
                                            pDialog.dismiss();
                                            new CountDownTimer(70000, 1000) { // adjust the milli seconds here

                                                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                                public void onTick(long millisUntilFinished) {
                                                    otpCountDown.setVisibility(View.VISIBLE);
                                                    otpCountDown.setText(""+String.format(FORMAT,
                                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) ));
                                                }

                                                public void onFinish() {
                                                    otpCountDown.setVisibility(View.GONE);
                                                    btnResend.setEnabled(true);
                                                }
                                            }.start();



                                        } else {
                                            // Error occurred in registration. Get the error
                                            // message
                                            String errorMsg = jObj.getString("message");
                                            Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "Registration Error: " + error.getMessage(), error);
                                    Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            }) {

                                @Override
                                protected Map<String, String> getParams() {
                                    // Posting params to register url
                                    Map<String, String> params = new HashMap<>();
                                    params.put("name", name);
                                    params.put("email", email);
                                    params.put("password", password);

                                    return params;
                                }

                            };
                            int socketTimeout = 15000;
                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            strReq.setRetryPolicy(policy);
                            // Adding request to request queue
                            RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
                        } else {
                            Toast.makeText(getApplicationContext(), "Email is not valid (yourmail@gmail.com)!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_LONG).show();
                    }
                }
            });




//                dialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK &&
//                                event.getAction() == KeyEvent.ACTION_UP &&
//                                !event.isCanceled()) {
//                            dialog.cancel();
//                            return true;
//                        }
//                        return false;
//                    }
//                });

//        dialogBuilder.setPositiveButton("Continue...", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // empty
//            }
//        });



        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                //buyLayout();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
//                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                b.setEnabled(true);
//
//                b.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(session.isLoggedIn()){
//
//
//                        }
//
//                        else  {
//                            createNoAccountBuyLayout();
//                        }
//
//                    }
//                });
            }
        });

        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                buyTime.setText("Remaining time: " + getFormattedTimeString(millisUntilFinished/1000));
            }
            public void onFinish() {

                sessionTimeout();
            }
        }.start();


        alertDialog.show();
    }

    private void createNoAccountBuyLayout() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SeatView.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.account_buy_layout, null);
        //results = 0.0;
        Button accountBtn = dialogView.findViewById(R.id.accountBtn);

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        Button noAccountBtn = dialogView.findViewById(R.id.noAccountBtn);

        noAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyNoAccount();
            }
        });



        final TextView buyTime1 = dialogView.findViewById(R.id.buyTime);
        //abc.removeAllViews();


        dialogBuilder.setView(dialogView);
        //dialogBuilder.setTitle("Select date");
        dialogBuilder.setCancelable(true);

        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                closeFlag = true;
                seatCancel();
                orderCancel();
                createLayout();
                seatFab.setEnabled(true);
                processing.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                dialog.dismiss();

            }
        });


//        dialogBuilder.setPositiveButton("Continue...", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // empty
//            }
//        });



        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                buyLayout();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();




        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
//                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                b.setEnabled(true);
//
//                b.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(session.isLoggedIn()){
//
//
//                        }
//
//                        else  {
//                            createNoAccountBuyLayout();
//                        }
//
//                    }
//                });
            }
        });

        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                buyTime1.setText("Remaining time: " + getFormattedTimeString(millisUntilFinished/1000));
            }
            public void onFinish() {
                sessionTimeout();
            }
        }.start();


        alertDialog.show();
    }

    private void buyNoAccount() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SeatView.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.noaccount_layout, null);
        //results = 0.0;
        final EditText emailText = dialogView.findViewById(R.id.etLogin);
        Button btnRegister = dialogView.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailText.getText().toString().trim();
                if (Functions.isValidEmailAddress(email)) {
                    if (!mode.equals("R")){
                        launchPayPalPayment();}
                    else {
                        sendTicket();
                        confirmLayout();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Email is not valid (yourmail@gmail.com)!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final TextView buyTime1 = dialogView.findViewById(R.id.buyTime);
        //abc.removeAllViews();


        dialogBuilder.setView(dialogView);
        //dialogBuilder.setTitle("Select date");
        dialogBuilder.setCancelable(true);

        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                closeFlag = true;
                seatCancel();
                orderCancel();
                createLayout();
                seatFab.setEnabled(true);
                processing.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                dialog.dismiss();

            }
        });


//        dialogBuilder.setPositiveButton("Continue...", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // empty
//            }
//        });



        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();




        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
//                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                b.setEnabled(true);
//
//                b.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(session.isLoggedIn()){
//
//
//                        }
//
//                        else  {
//                            createNoAccountBuyLayout();
//                        }
//
//                    }
//                });
            }
        });

        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                buyTime1.setText("Remaining time: " + getFormattedTimeString(millisUntilFinished/1000));
            }
            public void onFinish() {
                sessionTimeout();
            }
        }.start();


        alertDialog.show();

    }


    public static String getFormattedTimeString(long timeInSeconds) {
        String timeStr = new String();
        long sec_term = 1;
        long min_term = 60 * sec_term;
        long result = Math.abs(timeInSeconds);


        int min = (int) (result / min_term);
        result = result % min_term;
        int sec = (int) (result / sec_term);

        if (timeInSeconds < 0) {
            timeStr = "-";
        }

        if (min >= 0) {
            timeStr += min;
        }
        if (sec >= 10) {
            timeStr += ":" + sec;
        } else if (sec ==0) {
            timeStr += ":" + "00";}
        else if (sec < 10) {
            timeStr += ":" + "0" + sec;}
        return timeStr;
    }

    public void createLayout (){
        layout = findViewById(R.id.layoutSeat);
        layout.removeAllViews();
        selectedIds = "";
        selectedCount = 0;
        results = 0.0;
        seats = "/" + seats;



        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(8 * seatGaping, 8 * seatGaping, 8 * seatGaping, 8 * seatGaping);
        layout.addView(layoutSeat);

        LinearLayout layout = null;

        int count = 0;
        int row = 1;
        int seat = 1;
        seatsString = new HashMap<>();
        seatID = new HashSet<>();
        seatsMap = new HashMap<Integer, Seat>();
        boolean balcony = false;
        for (int index = 0; index < seats.length(); index++) {
            String seatType = null;
            if (seats.charAt(index) == '/') {
                if (row==21) {row=1; balcony=true;}
                if (firstFlag){
                    TextView view = new TextView(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                    layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                    view.setLayoutParams(layoutParams);
                    view.setPadding(0, 0, 0, 2 * seatGaping);
                    view.setGravity(Gravity.CENTER);
                    view.setTextColor(Color.BLACK);
                    view.setText(row + "");
                    row++;
                    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                    layout.addView(view);
                    firstFlag = false;
                }

                seat = 1;
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutSeat.addView(layout);

                if (index+6<seats.length()) {
                    if ((seats.charAt(index + 10) != '_' || seats.charAt(index + 15) != '_' || seats.charAt(index + 30) != '_') || seats.charAt(index + 40) != '_') {
                        TextView view = new TextView(this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                        layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                        view.setLayoutParams(layoutParams);
                        view.setPadding(0, 0, 0, 2 * seatGaping);
                        view.setGravity(Gravity.CENTER);
                        view.setTextColor(Color.BLACK);
                        view.setText(row + "");
                        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                        layout.addView(view);
                        firstFlag = true;
                    }
                }

            } else if (seats.charAt(index) == 'U') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_booked);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_BOOKED);
                //view.setText(count + "");
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
//                seatsID.put(index-1, count);
//                seatsRow.put(count, row);
//                seatsTag.put(count, (int) view.getTag());
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;

            } else if (seats.charAt(index) == 'F') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_book);
                //view.setText(count + "");
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.BLACK);
                view.setTag(STATUS_FIRST);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
//                seatsID.put(index-1, count);
//                seatsRow.put(count, row);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }else if (seats.charAt(index) == 'S') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_secondzone);
                //view.setText(count + "");
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_SECOND);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
//                seatsID.put(index-1, count);
//                seatsRow.put(count, row);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }else if (seats.charAt(index) == 'T') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_thirdzone);
                //view.setText(count + "");
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_THIRD);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
//                seatsID.put(index-1, count);
//                seatsRow.put(count, row);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }
            else if (seats.charAt(index) == 'V') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);

                view.setBackgroundResource(R.drawable.ic_seats_vierzone);
                //view.setText(count + "");
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_FOURTH);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
//                seatsID.put(index-1, count);
//                seatsRow.put(count, row);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            }

            else if (seats.charAt(index) == 'R') {
                count++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGaping);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_reserved);
                //view.setText(count + "");
                view.setText(seat + "");

                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_RESERVED);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
                seatsString.put(count,index-1);
//                seatsID.put(index-1, count);
//                seatsRow.put(count, row);
                if(!balcony) {
                    seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"PARTER", false));
                }else {seatsMap.put(count, new Seat(row, count, seat, index - 1, (int) view.getTag(),Double.parseDouble(price)*seatsType.get(view.getTag()),"BALKON", false));}
                seat++;
            } else if (seats.charAt(index) == '_') {
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setText("");
                layout.addView(view);
            }
        }
        seatFab.setEnabled(true);
        processing.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        SeatView.super.onBackPressed();
                    }
                }).create().show();
    }

    private void seatCancel() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Functions.POST_SEATCANCELED_URL, jsonSeat,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("TAG", response.getString("error"));
                            Log.d("TAG", response.getString("places"));

                            seats = response.getString("places");
                            createLayout();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        String tag_string_req = "req_seatcancel";
        int socketTimeout = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        RequestManager.getInstance().addToRequestQueue(jsonObjReq , tag_string_req);
    }

    private void orderCancel() {
        StringRequest verifyReq = new StringRequest(Request.Method.POST,
                Functions.POST_ORDERCANCELED, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "UORDER: " + response.toString());

                try {
                    JSONObject res = new JSONObject(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "UORDER Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("orderId", orderId);

                //params.put("paymentClientJson", payment_client);

                return params;
            }
        };

        // Setting timeout to volley request as verification request takes sometime
        int socketTimeout = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(verifyReq);
        RequestManager.getInstance().addToRequestQueue(verifyReq,"UORDER");
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Preparing final cart amount that needs to be sent to PayPal for payment
     * */
    private PayPalPayment prepareFinalCart() {


        // Total amount
        BigDecimal subtotal = BigDecimal.valueOf(results);

        // If you have shipping cost, add it here
        BigDecimal shipping = new BigDecimal("0.0");
        double resultALL = (results * 0.08);
        //String dfformat = df.format(resultALL);
        //double prices = Double.parseDouble(dfformat);
        // If you have tax, add it here
        BigDecimal tax = new BigDecimal(resultALL);

        tax = tax.setScale(2,ROUND_HALF_UP);

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(
                shipping, subtotal, tax);

        BigDecimal amount = subtotal.add(shipping).add(tax);

        Log.e("TAG", tax.toString());
        PayPalPayment payment = new PayPalPayment(
                amount,
                Config.DEFAULT_CURRENCY,
                "ldzmusictheatre",
                Config.PAYMENT_INTENT);

        payment.paymentDetails(paymentDetails);

        // Custom field like invoice_number etc.,
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    private void launchPayPalPayment() {

        updateOrder();

        PayPalPayment thingsToBuy = prepareFinalCart();

        Intent intent = new Intent(SeatView.this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private void updateOrder() {

        StringRequest verifyReq = new StringRequest(Request.Method.POST,
                Functions.POST_UORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "UORDER: " + response.toString());

                try {
                    JSONObject res = new JSONObject(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "UORDER Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("userEmail", email);
                params.put("orderId", orderId);

                //params.put("paymentClientJson", payment_client);

                return params;
            }
        };

        // Setting timeout to volley request as verification request takes sometime
        int socketTimeout = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(verifyReq);
        RequestManager.getInstance().addToRequestQueue(verifyReq,"UORDER");

    }

    /**
     * Receiving the PalPay payment response
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG, confirm.toJSONObject().toString(4));
                        Log.e(TAG, confirm.getPayment().toJSONObject()
                                .toString(4));

                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        JSONObject payment_json = confirm.getPayment()
                                .toJSONObject();

                        double values = (results + (results * 0.08));

                        String total = df.format(values);
                        total = total.replaceAll(",",".");
                        String currency_code = payment_json.getString("currency_code");

                        exaltedOrb = currency_code;

                        Log.e(TAG, "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        // Now verify the payment on the server side
                        verifyPaymentOnServer(paymentId, payment_client, total, currency_code);

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ",
                                e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }

    /**
     * Verifying the mobile payment on the server to avoid bad payment
     * */
    private void verifyPaymentOnServer(final String paymentId,
                                       final String payment_client, final String total, final String currency) {
        Log.e("VERIFY LOG", "" + "paymentId: "+paymentId + " userEmail: " + email + " orderId: " + orderId + " total: " + total + " currency: " +  currency);
        // Showing progress dialog before making request
        pDialog.setMessage("Verifying payment...");
        showDialog();

        StringRequest verifyReq = new StringRequest(Request.Method.POST,
                Config.URL_VERIFY_PAYMENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "verify payment: " + response.toString());
                Log.d(TAG, "" + "paymentId: "+ paymentId + " userEmail: " + email + " orderId" + orderId + " total: " + total + " currency: " +  currency);

                //JSONObject res = new JSONObject(response);

                //boolean error = res.getBoolean("error");
                //String message = res.getString("message");

                sendTicket();
                confirmLayout();

                Toast.makeText(getApplicationContext(), "Success",
                        Toast.LENGTH_SHORT).show();


                // hiding the progress dialog
                hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Verify Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Connection Problem. Try again later..", Toast.LENGTH_SHORT).show();
                // hiding the progress dialog
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("paymentId", paymentId);
                params.put("userEmail", email);
                params.put("orderId", orderId);
                params.put("total", total);
                params.put("currency", currency);

                //params.put("paymentClientJson", payment_client);

                return params;
            }
        };

        // Setting timeout to volley request as verification request takes sometime
        int socketTimeout = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(verifyReq);
        RequestManager.getInstance().addToRequestQueue(verifyReq,"VERIFY");
    }

    private void confirmLayout() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SeatView.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.buy_confirmation, null);
        //results = 0.0;
        Button doneBtn = dialogView.findViewById(R.id.doneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isLoggedIn()){
                    Intent upanel = new Intent(SeatView.this, YourTicket.class);
                    upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(upanel);
                }
                else{
                    Intent upanel = new Intent(SeatView.this, SlideMain.class);
                    upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(upanel);
                }

                //finish();
            }
        });

        final LinearLayout abc = dialogView.findViewById(R.id.pof_llayout);

        TextView namePerformance = dialogView.findViewById(R.id.performanceName);
        TextView datePerformance = dialogView.findViewById(R.id.performanceDate);
        TextView timePerformance = dialogView.findViewById(R.id.performanceTime);
        TextView confirmEmail = dialogView.findViewById(R.id.email);
        confirmEmail.setText(email);

        TextView order = dialogView.findViewById(R.id.Order);
        order.setText("Order #"+orderId );

        final TextView prices = dialogView.findViewById(R.id.Price);
        prices.setText(df.format((results + results*0.08)) + " PLN");

        namePerformance.setText(performanceName);
        datePerformance.setText(performanceDate);
        timePerformance.setText(performanceTime);

        try {

            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject places = jsonArray2.getJSONObject(i);

                final View custom = inflater.inflate(R.layout.buy_element, null);

                String no = (String) places.get("place");
                final String id = (String) places.get("place_id");
                String row = (String) places.get("row");
                Seat temp = seatsMap.get(Integer.parseInt(id));
                final Switch switchReduced = custom.findViewById(R.id.switchReduced);
                final TextView seatPrice = custom.findViewById(R.id.seatPrice);
                seatPrice.setText(df.format(temp.getSeatPrice()) + " PLN");

                switchReduced.setEnabled(false);
                if (temp.getSeatTag() == 5 || temp.getSeatTag() == 6 || temp.getSeatTag() == 7 || temp.getSeatTag() == 8 )
                    switchReduced.setChecked(true);

                final TextView seatRow = custom.findViewById(R.id.seatRow);
                final TextView seatPosition = custom.findViewById(R.id.seatPosition);
                final TextView seatNo = custom.findViewById(R.id.seatNo);

                seatRow.setText(row);
                seatNo.setText(no);
                seatPosition.setText(temp.getSeatType());
                results = results + temp.getSeatPrice();
                abc.addView(custom);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
            }
        });
        alertDialog.show();

    }

    private void sendTicket() {
        doneFlag = true;
        double values = (results + (results * 0.08));

        String total = df.format(values);
        total = total.replaceAll(",",".");

        final String finalTotal = total;

        Log.e("TAG", finalTotal);

        StringRequest verifyReq = new StringRequest(Request.Method.POST,
                Functions.POST_SEND_TICKET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "SEND_TICKET: " + response.toString());

                //JSONObject res = new JSONObject(response);


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "UORDER Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("mode", mode);
                params.put("userEmail", email);
                params.put("orderId", orderId);
                params.put("performanceName", performanceName);
                params.put("performanceDate", performanceDate);
                params.put("performanceTime", performanceTime);
                params.put("price", finalTotal);

                if (mode.contains("R")){
                    params.put("currency", "PLN");
                }else {
                    params.put("currency", exaltedOrb);
                }

                //params.put("paymentClientJson", payment_client);

                return params;
            }
        };

        // Setting timeout to volley request as verification request takes sometime
        int socketTimeout = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);

        // Adding request to request queue
        RequestManager.getInstance().addToRequestQueue(verifyReq,"SEND_TICKET");
    }

    public void sessionTimeout() {
        if(!doneFlag) {
            seatCancel();
        }
        if(!closeFlag) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SeatView.this, R.style.MyAlertDialogTheme);
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.buy_session, null);
            //results = 0.0;
            Button doneBtn = dialogView.findViewById(R.id.doneBtn);

            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent upanel = new Intent(SeatView.this, SlideMain.class);
                    upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(upanel);
                    finish();
                }
            });
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);

            final AlertDialog alertDialog = dialogBuilder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                }
            });
            alertDialog.show();
        }
    }
}
