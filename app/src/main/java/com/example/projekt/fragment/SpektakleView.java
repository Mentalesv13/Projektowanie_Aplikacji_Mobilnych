package com.example.projekt.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.booking.SeatView;
import com.example.projekt.entity.PoF;
import com.example.projekt.entity.Spektakle;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.Functions;
import com.example.projekt.helper.LoadingDialog;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.login.RequestManager;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.projekt.login.RequestManager.TAG;

public class SpektakleView extends Fragment implements onKeyboardVisibilityListener {

    private DatabaseHandler db;
    private String performanceName;
    private String performanceDate;
    private String performanceTime;
    private HashMap<Integer, Spektakle> Spectacles;
    private HashMap<Integer, PoF> PoFHashMap;
    View view;
    final DecimalFormat df = new DecimalFormat("####0.00");
    private LoadingDialog loadingDialog;
    private SessionManager session;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView mSearchView;
    private LinearLayout abc = null;
    private String mode = null;
    private AlertDialog.Builder dialogBuilder1 = null;
    private String btnValue = null;
    private ImageView spektakleBanner;


    @SuppressLint("UseSparseArrays")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DatabaseHandler(getContext());

        PoFHashMap = new HashMap<Integer, PoF>();
        session = new SessionManager(getContext());
        view = inflater.inflate(R.layout.fragment_spektakle, container, false);
        dialogBuilder1 = new AlertDialog.Builder(getContext(),R.style.MyAlertDialogTheme);
        abc = view.findViewById(R.id.llEvents);
        spektakleBanner = view.findViewById(R.id.spektakleBanner);
        spektakleBanner.setImageResource(R.drawable.spektakle);
        //images.add(R.drawable.people);
        //SearchView search = (SearchView) view.findViewById(R.id.spektakleSearch);
        //search.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        setKeyboardVisibilityListener(this);
        mSearchView = view.findViewById(R.id.spektakleSearch);

        mSearchView.setOnSearchClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSearchView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                //mSearchView.getLayoutParams().width= ViewGroup.LayoutParams.MATCH_PARENT;
                mSearchView.setQuery("", false);
                mSearchView.requestFocus();
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                createSpectaleLayout(query,false);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                createSpectaleLayout(newText,false);
                return false;
            }
        });

        SearchView.OnCloseListener onCloseListener = new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchView.clearFocus();
                createSpectaleLayout("",true);
                return false;
            }
        };
        mSearchView.setOnCloseListener(onCloseListener);

        int searchCloseButtonId = mSearchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
//        ImageView closeButton = (ImageView) this.mSearchView.findViewById(searchCloseButtonId);
//        // Set on click listener
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSearchView.clearFocus();
//                mSearchView.setQuery("",false);
//            }
//        });

        //mSearchView.clearFocus();
        //mSearchView .setLayoutParams(new SearchView.LayoutParams());
        loadingDialog = new LoadingDialog(getActivity());
        mSwipeRefreshLayout = view.findViewById(R.id.swifeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.black, android.R.color.holo_red_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final SpectacleTask task = new SpectacleTask();
                task.execute();
                mSearchView.setQuery("", false);
                mSearchView.clearFocus();
            }
        });

        if (!session.isSpectacleUp()) {
            final SpectacleTask task = new SpectacleTask();
            task.execute();
        } else {
            loadingDialog.showDialog();
            Spectacles = db.getSpectaclesDetail();
            createSpectaleLayout();
        }

        loadingDialog.hideDialog();

        return view;
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        //Toast.makeText(getActivity().getApplicationContext(), visible ? "Keyboard is active" : "Keyboard is Inactive", Toast.LENGTH_SHORT).show();
        if (!visible && abc.getChildCount()==0) mSearchView.setQuery("",false);
    }


    private void setKeyboardVisibilityListener(final onKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) view.findViewById(R.id.swifeRefresh)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    //Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    private void createSpectaleLayout() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //final LinearLayout abc = view.findViewById(R.id.llEvents);

        if (Spectacles.size()>0) {
            abc.removeAllViews();

            for (int i = 0; i < Spectacles.size(); i++) {
                //Log.d(TAG, "INT: " + i);
                Spektakle temp = Spectacles.get(i);
                final View custom = inflater.inflate(R.layout.spectacles_layout, null);
                custom.setTag(i);
                //custom.setOnClickListener(new View.OnClickListener() {
                //    @Override
                //    public void onClick(View v) {
                //        eventOnClickDialog((Integer)custom.getTag());
                //    }
                //});

                final Button btnBuy = custom.findViewById(R.id.buyBtn);
                final Button btnReserve = custom.findViewById(R.id.reservationBtn);
                final TextView tv = (TextView) custom.findViewById(R.id.spectacle_Name);
                tv.setText(temp.getName());
                btnBuy.setTag(temp.getSpektaklId());
                btnReserve.setTag(temp.getSpektaklId());

                btnBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mode = "U";
                        performanceName = tv.getText().toString();
                        getPerformanceOfSpectales(btnBuy.getTag().toString());

                        //getSeats(String.valueOf(btnBuy.getTag()),"U");
                    }
                });

                btnReserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mode = "R";
                        performanceName = tv.getText().toString();
                        getPerformanceOfSpectales(btnBuy.getTag().toString());
                        //getSeats(String.valueOf(btnReserve.getTag()), "R");
                    }
                });

                final ImageView img = custom.findViewById(R.id.spectacle_image);
                img.setTag(i);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageOnClickDialog((Integer) img.getTag());
                    }
                });
                final ImageView zoom = custom.findViewById(R.id.imgZoom);
                zoom.setImageResource(R.drawable.ic_zoom);
                zoom.setTag(i);
                zoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageOnClickDialog((Integer) zoom.getTag());
                    }
                });

                TextView tv1 = (TextView) custom.findViewById(R.id.premiere_date);
                TextView tv2 = (TextView) custom.findViewById(R.id.spectacle_desc);

                Picasso.with(getContext()).load("https://ldzmusictheatre.000webhostapp.com/images/" + temp.getImgUrl()).into(img);

                tv1.setText(temp.getDate());
                tv2.setText(temp.getDesc());
                abc.addView(custom);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void createSpectaleLayout(String newText, boolean type) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        HashMap<Integer, Spektakle> newSpectacles;
        newSpectacles = db.getSpectaclesDetail(newText);
        final LinearLayout abc = view.findViewById(R.id.llEvents);
        abc.removeAllViews();

        if(!type) {
            if (newSpectacles.size() != 0) {
                for (int i = 0; i < newSpectacles.size(); i++) {
                    //Log.d(TAG, "INT: " + i);
                    Spektakle temp = newSpectacles.get(i);
                    final View custom = inflater.inflate(R.layout.spectacles_layout, null);
                    custom.setTag(i);
                    //custom.setOnClickListener(new View.OnClickListener() {
                    //    @Override
                    //    public void onClick(View v) {
                    //        eventOnClickDialog((Integer)custom.getTag());
                    //    }
                    //});

                    final Button btnBuy = custom.findViewById(R.id.buyBtn);
                    final Button btnReserve = custom.findViewById(R.id.reservationBtn);

                    final TextView tv = (TextView) custom.findViewById(R.id.spectacle_Name);
                    tv.setText(temp.getName());

                    btnBuy.setTag(temp.getSpektaklId());
                    btnReserve.setTag(temp.getSpektaklId());

                    btnBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            performanceName = tv.getText().toString();
                            mode = "U";
                            getPerformanceOfSpectales(btnBuy.getTag().toString());
                            //getSeats(String.valueOf(btnBuy.getTag()),"U");
                        }
                    });

                    btnReserve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mode = "R";
                            getPerformanceOfSpectales(btnBuy.getTag().toString());
                            //getSeats(String.valueOf(btnReserve.getTag()), "R");
                        }
                    });

                    final ImageView img = custom.findViewById(R.id.spectacle_image);
                    img.setTag(i);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageOnClickDialog((Integer) img.getTag());
                        }
                    });
                    final ImageView zoom = custom.findViewById(R.id.spectacle_image);
                    zoom.setTag(i);
                    zoom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageOnClickDialog((Integer) zoom.getTag());
                        }
                    });

                    TextView tv1 = (TextView) custom.findViewById(R.id.premiere_date);
                    TextView tv2 = (TextView) custom.findViewById(R.id.spectacle_desc);

                    Picasso.with(getContext()).load("https://ldzmusictheatre.000webhostapp.com/images/" + temp.getImgUrl()).into(img);

                    tv1.setText(temp.getDate());
                    tv2.setText(temp.getDesc());
                    abc.addView(custom);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), "No search results", Toast.LENGTH_LONG).show();
            createSpectaleLayout();
        }
    }

    public void showCustomLoadingDialog() {

        loadingDialog.showDialog();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.hideDialog();
            }
        }, 3000);
    }

    class SpectacleTask extends AsyncTask<Void, Void, Void> {
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

            Spectacles = new HashMap<>();

            String tag_string_req = "req_get_spectacle";

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    Functions.GET_SPECTALES_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    JSONParser parser = new JSONParser();

                    try {
                        Object obj = parser.parse(response);

                        JSONObject jsonObject = (JSONObject) obj;

                        //String error = (String) jsonObject.get("error");
                        //Log.d(TAG, "Error: " + error);
                        JSONArray spectaclesList = (JSONArray) jsonObject.get("spectacles");
                        Iterator i = spectaclesList.iterator();
                        db.resetSpectacles();
                        int tempID = 0;

                        while (i.hasNext()) {
                            JSONObject spectacles = (JSONObject) i.next();
                            String id = (String) spectacles.get("id");
                            String name = (String) spectacles.get("name");
                            String desc = (String) spectacles.get("desc");
                            String date = (String) spectacles.get("date");
                            String imgUrl = (String) spectacles.get("imgUrl");
                            //Log.d(TAG, "ID: " + id + "Name: " + name + "Sdesc: " + sdesc + "desc: " + desc);

                            Spectacles.put(tempID, new Spektakle(Long.parseLong(id), name, desc, date, imgUrl));
                            db.addSpectacle(id, name, desc, date, imgUrl);

                            tempID++;
                        }
                        session.setSpectacle(true);
                        createSpectaleLayout();
                        loadingDialog.hideDialog();
                    } catch (Exception e) {
                        //noInternetConnectionDialog();
                        Log.d(TAG, "ERROR");
                        if(session.isSpectacleUp()){session.setSpectacle(true);
                            Spectacles = db.getSpectaclesDetail();
                            createSpectaleLayout();}
                        else {
                            if(checkNetworkConnection() && !session.isSpectacleUp() ){
                                session.setSpectacle(false);
                                noInternetConnectionDialog();
                            }
                        }

                        e.printStackTrace();
                        loadingDialog.hideDialog();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Events error: " + error.getMessage());
                    //noInternetConnectionDialog();
                    if(session.isSpectacleUp()){session.setSpectacle(true);
                        Spectacles = db.getSpectaclesDetail();
                        createSpectaleLayout();}
                    else {
                        if(!checkNetworkConnection()){
                            session.setSpectacle(false);
                            noInternetConnectionDialog();
                        }
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                    loadingDialog.hideDialog();
                }
            });
//            if(checkNetworkConnection() && !session.isSpectacleUp() ){
//                session.setSpectacle(false);
//                noInternetConnectionDialog();
//            }

            // Adding request to request queue
            int socketTimeout = 5000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            strReq.setRetryPolicy(policy);
            RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);

            return null;
        }
    }

    private void imageOnClickDialog(int index) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(),R.style.MyAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.show_image, null);
        Spektakle temp = Spectacles.get(index);
        ImageView bigImage = dialogView.findViewById(R.id.bigImage);
        bigImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(getContext()).load(Functions.IMAGES_URL + temp.getImgUrl()).into(bigImage);
        dialogBuilder.setView(dialogView);
        //dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.setCancelable(false);

        //final EditText mEditEmail = dialogView.findViewById(R.id.etEmailR);

        dialogBuilder.setPositiveButton("Back",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
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
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }


    public void getPerformanceOfSpectales(final String id){
        loadingDialog.showDialog();

        Log.d(TAG,"ID: " + id);
        String tag_string_req = "req_pof";
        StringRequest strReq = new StringRequest(Request.Method.POST,

                Functions.GET_POF_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Reset Password Response: " + response);
                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(response);

                    JSONObject jsonObject = (JSONObject) obj;
                    //boolean error = jsonObject.getBoolean("error");
                    Log.e("TAG", "Log: " + response);

//                    if (!error) {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                    }
                    PoFHashMap = new HashMap<Integer, PoF>();
                    JSONArray PoFlist = (JSONArray) jsonObject.get("performances");
                    Iterator i = PoFlist.iterator();

                    int tempID = 0;

                    while (i.hasNext()) {
                        JSONObject spectacles = (JSONObject) i.next();
                        String id = (String) spectacles.get("id_performance");
                        String name = (String) spectacles.get("date_performance");
                        //Log.d(TAG, "ID: " + id + "Name: " + name + "Sdesc: " + sdesc + "desc: " + desc);

                        PoFHashMap.put(tempID, new PoF(Long.parseLong(id), name));


                        tempID++;
                    }

                    pofTask poFtask = new pofTask();
                    poFtask.execute();

                    //String seats = "{\"Id_Performance\": \"6\"+\"Mode\":\"" + Mode + "\",\"Places:\"";
                    //seats = seats + (String) jsonObject.get("places") +"/}";
                    String id_performances = (String) jsonObject.get("places");
                    //Intent myIntent = new Intent(getActivity(), SeatView.class);
                    //myIntent.putExtra("seats", seats);
                    //myIntent.putExtra("id", String.valueOf(id));
                    //myIntent.putExtra("mode", Mode);
                    //startActivity(myIntent);

                } catch (ParseException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("id_spectacle", id);
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
        int socketTimeout = 5000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);
        RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void layoutPoF(View v) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pof_layout, null);


        final LinearLayout abc = dialogView.findViewById(R.id.pof_llayout);
        //abc.removeAllViews();
        boolean isPlayed = false;
        for (int i = 0; i < PoFHashMap.size(); i++) {
            //Log.d(TAG, "INT: " + i);
            PoF temp = PoFHashMap.get(i);
            final View custom = inflater.inflate(R.layout.pof_element, null);
            custom.setTag(temp.getId_performance());

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            try {
                Date d1 = df.parse(date);

                Date d2 = df.parse(temp.getDate_performace());

                if(d1.compareTo(d2) < 0) {
                    isPlayed = true;
                    final Button dateBtn = custom.findViewById(R.id.dateBtn);

                    dateBtn.setText(temp.getDate_performace());

                    dateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String date = dateBtn.getText().toString();
                            performanceDate = date.substring(0, 10);
                            performanceTime = date.substring(11, 16);
                            btnValue = custom.getTag().toString();
                            getSeats();
                        }
                    });
                }
                else {
                    final CardView dateCard = custom.findViewById(R.id.dateCard);
                    dateCard.setVisibility(View.GONE);
                }

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


            //custom.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        eventOnClickDialog((Integer)custom.getTag());
            //    }
            //});
            abc.addView(custom);
    }
        if (!isPlayed){
            TextView noPlayed = new TextView(getContext());
            noPlayed.setText("In the coming days the selected spectacle is not in the repertoire.");
            noPlayed.setTextSize(18);
            noPlayed.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            abc.setGravity(Gravity.CENTER);
            abc.addView(noPlayed);
        }


        dialogBuilder1.setView(dialogView);
        //dialogBuilder.setTitle("Select date");
        dialogBuilder1.setCancelable(false);


        dialogBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

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
        loadingDialog.showDialog();
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
        int socketTimeout = 5000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);
        RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    class pofTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final AlertDialog alertDialog = dialogBuilder1.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setEnabled(false);

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }
            });
            loadingDialog.hideDialog();
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @SuppressLint("UseSparseArrays")
        @Override
        protected Void doInBackground(Void... voids) {

            layoutPoF(view);
            return null;
        }
    }
    class seatTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {

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

    public boolean checkNetworkConnection() {
        ConnectivityManager connectManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else return false;
    }

    private void noInternetConnectionDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_internet_connection, null);
        ImageView imageWhoops = dialogView.findViewById(R.id.Whoops);
        imageWhoops.setImageResource(R.drawable.whoops);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("Reload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
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

                        if (checkNetworkConnection()) {
                            final SpectacleTask task = new SpectacleTask();
                            task.execute();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Check Internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }
}
