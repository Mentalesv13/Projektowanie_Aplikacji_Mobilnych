package com.example.projekt.ui.events;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.entity.Event;
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
import java.util.Random;

import static com.example.projekt.login.RequestManager.TAG;

public class Events extends Fragment {

    private DatabaseHandler db;
    HashMap<Integer,Event> Events;
    View view;
    LoadingDialog loadingDialog;
    SessionManager session;
    int images[] = {R.drawable.people, R.drawable.premiere, R.drawable.megaphone, R.drawable.haze};


    @SuppressLint("UseSparseArrays")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DatabaseHandler(getContext());
        session = new SessionManager(getContext());
        view = inflater.inflate(R.layout.fragment_wydarzenia, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        if (!session.isEventUp()) {
            final EventTask task = new EventTask();
            task.execute();
        }
        else {
            Events = db.getEventsDetail();
            createEventLayout();
        }

        return view;
    }

    private void createEventLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final LinearLayout abc = view.findViewById(R.id.llEvents);

        for (int i = 0; i < Events.size(); i++) {
            Log.d(TAG,"INT: " + i);
            Event temp = Events.get(i);
            final View custom = inflater.inflate(R.layout.fragment_wydarzenia2, null);
            custom.setTag(i);
            custom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventOnClickDialog((Integer)custom.getTag());
                }
            });
            ImageView img = custom.findViewById(R.id.llEvent_img);
            TextView tv = (TextView) custom.findViewById(R.id.llEvent_tv1);
            TextView tv1 = (TextView) custom.findViewById(R.id.llEvent_tv2);

            Random generator = new Random();

            img.setImageResource(images[generator.nextInt(images.length)]);
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
        }, 3000);
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

        @SuppressLint("UseSparseArrays")
        @Override
        protected Void doInBackground(Void... voids) {

            Events = new HashMap<>();

                String tag_string_req = "req_get_event";

                StringRequest strReq = new StringRequest(Request.Method.GET,
                        Functions.GET_EVENT_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
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
                                Long id = (Long) events.get("id_event");
                                String name = (String) events.get("name_event");
                                String sdesc = (String) events.get("sdesc_event");
                                sdesc = sdesc + " \n[ Czytaj dalej... ]";
                                String desc = (String) events.get("desc_event");
                                //Log.d(TAG, "ID: " + id + "Name: " + name + "Sdesc: " + sdesc + "desc: " + desc);

                                Events.put(tempID, new Event(id, name, sdesc, desc));
                                db.addEvent(id.toString(), name,sdesc,desc);

                                tempID++;
                            }
                            createEventLayout();
                            session.setEvent(true);
                            //loadingDialog.hideDialog();
                        } catch (Exception e) {
                            Log.d(TAG, "ERROR");
                            session.setEvent(false);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Events error: " + error.getMessage());
                        session.setEvent(false);
                        Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                    }
                });

                // Adding request to request queue
                RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);


            return null;
        }
    }

    private void eventOnClickDialog(int index) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_wydarzenia3, null);
        Event temp = Events.get(index);
        TextView tv1 = (TextView) dialogView.findViewById(R.id.llEvent_tv1);
        TextView tv2 = (TextView) dialogView.findViewById(R.id.llEvent_tv2);
        tv1.setText(temp.getName_event());
        tv2.setText(temp.getDesc_event());

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("More info");
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
}
