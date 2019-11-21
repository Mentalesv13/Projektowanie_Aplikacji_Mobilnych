package com.example.projekt.fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
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
    TabLayout tabLayout;
    ViewPager pager;
    PagerAdapter _adapter;
    private DatabaseHandler db;
    HashMap<Integer, Repertoire> Repertoire;
    View view;
    LoadingDialog loadingDialog;
    SessionManager session;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_repertuar, container, false);
        db = new DatabaseHandler(getContext());
        session = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());

        if(!session.isRepertoireUp()) {
            final RepertoireTask task = new RepertoireTask();
            task.execute();
        }
        else {
            createRepertoireLayout();
        }
        return view;
    }

    class RepertoireTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            showCustomLoadingDialog(view);
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

            Repertoire = new HashMap<>();

            String tag_string_req = "req_get_event";
            if (!session.isRepertoireUp()) {
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

                            //loadingDialog.hideDialog();
                        } catch (Exception e) {
                            Log.d(TAG, "ERROR");
                            session.setRepertoire(false);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Repertoire error: " + error.getMessage());
                        session.setRepertoire(false);
                        Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                    }
                });
                RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
            }


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

    public void createRepertoireLayout() {
        // Adding request to request queue
        String[] monthName = {"JANUARY", "FEBRUARY",
                "MARCH", "APRIL", "MAY", "JUNE", "JULY",
                "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER",
                "DECEMBER"};
        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
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
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pager = (ViewPager) view.findViewById(R.id.pager);

        _adapter = new PagerAdapter(getChildFragmentManager());
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH))+1)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 2) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 3) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 4) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 5) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 6) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 7) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 8) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 9) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 10) % 13)));
        _adapter.add(new TabRepertoire(String.valueOf((cal.get(Calendar.MONTH) + 11) % 13)));
        pager.setAdapter(_adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

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
