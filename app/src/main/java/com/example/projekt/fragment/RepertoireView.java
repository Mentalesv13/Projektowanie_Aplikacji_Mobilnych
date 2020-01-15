package com.example.projekt.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.entity.Repertoire;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.Functions;
import com.example.projekt.helper.LoadingDialog;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.login.RequestManager;
import com.example.projekt.pager.PagerAdapter;
import com.example.projekt.pager.TabRepertoire;
import com.google.android.material.tabs.TabLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import static com.example.projekt.login.RequestManager.TAG;

public class RepertoireView extends Fragment {

    private DatabaseHandler db;
    HashMap<Integer, Repertoire> Repertoire;
    View view;
    LoadingDialog loadingDialog;
    SessionManager session;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView repertuarBanner;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_repertuar, container, false);
        repertuarBanner = view.findViewById(R.id.repertuarBanner);
        repertuarBanner.setImageResource(R.drawable.spektakle);

        db = new DatabaseHandler(getContext());
        session = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());

        mSwipeRefreshLayout = view.findViewById(R.id.swifeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.black, android.R.color.holo_red_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final RepertoireTask task = new RepertoireTask();
                task.execute();
            }
        });
        if (!session.isRepertoireUp()) {
            final RepertoireTask task = new RepertoireTask();
            task.execute();
        }
        else {
            loadingDialog.showDialog();
            Repertoire= db.getRepertoireDetail();
            createRepertoireLayout();
        }
        loadingDialog.hideDialog();
        return view;
    }

    class RepertoireTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            loadingDialog.showDialog();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @SuppressLint("UseSparseArrays")
        @Override
        protected Void doInBackground(Void... voids) {

            Repertoire = new HashMap<>();

            String tag_string_req = "req_get_event";

                StringRequest strReq = new StringRequest(Request.Method.GET,
                        Functions.GET_REPERTOIRE_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        JSONParser parser = new JSONParser();

                        try {
                            Object obj = parser.parse(response);

                            JSONObject jsonObject = (JSONObject) obj;

                            //String error = (String) jsonObject.get("error");
                            //Log.d(TAG, "Error: " + error);
                            Log.d(TAG, response);
                            JSONArray eventsList = (JSONArray) jsonObject.get("repertoire");
                            Iterator i = eventsList.iterator();
                            db.resetRepertoire();
                            int tempID = 0;

                            while (i.hasNext()) {
                                JSONObject events = (JSONObject) i.next();
                                Long id = (Long) events.get("id");
                                String name = (String) events.get("name");
                                String date = (String) events.get("date");
                                String tag = (String) events.get("tag");
                                Log.d(TAG, "ID: " + id + "Name: " + name + "date: " + date + "tag: " + tag);

                                Repertoire.put(tempID, new Repertoire(id, name, date, tag));
                                db.addRepertoire(id.toString(), name, date, tag);

                                tempID++;
                            }
                            createRepertoireLayout();
                            session.setRepertoire(true);
                            loadingDialog.hideDialog();
                            //loadingDialog.hideDialog();
                        } catch (Exception e) {
                            Log.d(TAG, "ERROR");
                            if(session.isRepertoireUp()){session.setRepertoire(true);
                                Repertoire = db.getRepertoireDetail();
                                createRepertoireLayout();
                            }
                            else {
                                if(checkNetworkConnection()){
                                    session.setRepertoire(false);
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
                        Log.e(TAG, "Repertoire error: " + error.getMessage());
                        if(session.isRepertoireUp()){session.setRepertoire(true);
                            Repertoire = db.getRepertoireDetail();
                            createRepertoireLayout();}
                        else {
                            if(!checkNetworkConnection()){
                                session.setRepertoire(false);
                                noInternetConnectionDialog();
                            }
                        }
                        Toast.makeText(getActivity().getBaseContext(), "Connection problem", Toast.LENGTH_LONG).show();
                        loadingDialog.hideDialog();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
//            if(checkNetworkConnection() && !session.isRepertoireUp()){
//                session.setRepertoire(false);
//                noInternetConnectionDialog();
//            }
            int socketTimeout = 15000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            strReq.setRetryPolicy(policy);
                RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);



            return null;
        }
    }
    public void showCustomLoadingDialog(View view) {

        loadingDialog.showDialog();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.hideDialog();
            }
        }, 3000);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createRepertoireLayout() {

        // Adding request to request queue
        String[] monthName = {"JANUARY", "FEBRUARY",
                "MARCH", "APRIL", "MAY", "JUNE", "JULY",
                "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER",
                "DECEMBER"};
        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        if (Repertoire.size()>0) {
            tabLayout.removeAllTabs();
            tabLayout.addTab(tabLayout.newTab().setText(month));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 1) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 2) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 3) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 4) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 5) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 6) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 7) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 8) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 9) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 10) % 12]));
            tabLayout.addTab(tabLayout.newTab().setText(monthName[(cal.get(Calendar.MONTH) + 11) % 12]));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
            final PagerAdapter _adapter = new PagerAdapter(getChildFragmentManager());
            _adapter.add(new TabRepertoire(String.valueOf(cal.get(Calendar.MONTH) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 1) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 2) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 3) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 4) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 5) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 6) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 7) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 8) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 9) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 10) % 13 + 1)));
            _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 11) % 13 + 1)));
            pager.setAdapter(_adapter);
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        pager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled( int position, float v, int i1 ) {
//            }
//
//            @Override
//            public void onPageSelected( int position ) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged( int state ) {
//                enableDisableSwipeRefresh( state == ViewPager.SCROLL_STATE_IDLE );
//            }
//        } );

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }
    private void enableDisableSwipeRefresh(boolean enable) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
                            final RepertoireTask task = new RepertoireTask();
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
