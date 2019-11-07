package com.example.projekt.ui.Events;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.entity.Event;
import com.example.projekt.helper.Functions;
import com.example.projekt.helper.LoadingDialog;
import com.example.projekt.login.RequestManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.projekt.login.RequestManager.TAG;

public class Events extends Fragment {

    //private DatabaseHandler db;
    Map<Integer, Event> Events = new HashMap<>();
    //List<String> Events = new ArrayList<>();
    View view;

    LoadingDialog loadingDialog;


    @SuppressLint("UseSparseArrays")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wydarzenia, container, false);

        loadingDialog = new LoadingDialog(getActivity());
        //showCustomLoadingDialog(view);
        //
        //loadingDialog.showDialog();
        final EventTask task = new EventTask();
        task.execute();
        //createEventLayout();
        return view;

    }

    private void createEventLayout(){


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //ConstraintLayout parent = (ConstraintLayout) inflater.inflate(R.layout.fragment_wydarzenia, null);
        LinearLayout abc = view.findViewById(R.id.llEvents);
        for (int i = 0; i < Events.size(); i++) {
            Event temp = Events.get(i);
            View custom = inflater.inflate(R.layout.fragment_wydarzenia2, null);
            TextView tv = (TextView) custom.findViewById(R.id.llEvent_tv1);
            TextView tv1 = (TextView) custom.findViewById(R.id.llEvent_tv2);
            tv.setText(temp.getName_event());
            tv1.setText(temp.getSdesc_event());
            abc.addView(custom);
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
        }, 2000);
    }


    class EventTask extends AsyncTask<Void,Void,Void> {
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

        @Override
        protected Void doInBackground(Void... voids) {
            //b = new DatabaseHandler(getActivity().getApplicationContext());

            String tag_string_req = "req_get_event";

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    Functions.GET_EVENT_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //Log.d(TAG, "GET_EVENT Response: " + response);

                    JSONParser parser = new JSONParser();

                    try {
                        Object obj = parser.parse(response);

                        JSONObject jsonObject = (JSONObject) obj;

                        String error = (String) jsonObject.get("error");
                        Log.d(TAG, "Error: " + error);
                        JSONArray eventsList = (JSONArray) jsonObject.get("events");
                        Iterator i = eventsList.iterator();

                        int tempID = 0;

                        while (i.hasNext()) {

                            JSONObject events = (JSONObject) i.next();
                            Long id = (Long)events.get("id_event");
                            String name = (String) events.get("name_event");
                            String sdesc = (String) events.get("sdesc_event");
                            String desc = (String) events.get("desc_event");

                            //Log.d(TAG, "ID: " + id + "Name: " + name + "Sdesc: " + sdesc + "desc: " + desc);

                            Events.put(tempID,new Event(id,name,sdesc,desc));

                            tempID++;
                        }

                        createEventLayout();
                        //loadingDialog.hideDialog();
                    } catch (Exception e) {
                        Log.d(TAG, "ERROR" );
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Events error: " + error.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                }
            });

            // Adding request to request queue
            RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
            return null;
        }
    }
}
