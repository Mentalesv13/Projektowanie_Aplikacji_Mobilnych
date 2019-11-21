package com.example.projekt.fragment;

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

import java.util.HashMap;
import java.util.Iterator;

import static com.example.projekt.login.RequestManager.TAG;

public class SpektakleView extends Fragment {

    private DatabaseHandler db;
    HashMap<Integer, Spektakle> Spectacles;
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
        //images.add(R.drawable.people);
        loadingDialog = new LoadingDialog(getActivity());
        if (!session.isSpectacleUp()) {
            final SpectacleTask task = new SpectacleTask();
            task.execute();
        } else {
            Spectacles = db.getSpectaclesDetail();
            createSpectaleLayout();
        }

        return view;
    }

    private void createSpectaleLayout() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout abc = view.findViewById(R.id.llEvents);

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
            final ImageView img = custom.findViewById(R.id.spectacle_image);
            img.setTag(i);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageOnClickDialog((Integer)img.getTag());
                }
            });
            final ImageView zoom = custom.findViewById(R.id.spectacle_image);
            zoom.setTag(i);
            zoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageOnClickDialog((Integer)zoom.getTag());
                }
            });
            TextView tv = (TextView) custom.findViewById(R.id.spectacle_Name);
            TextView tv1 = (TextView) custom.findViewById(R.id.premiere_date);
            TextView tv2 = (TextView) custom.findViewById(R.id.spectacle_desc);

            Picasso.with(getContext()).load("https://ldzmusictheatre.000webhostapp.com/images/"+ temp.getImgUrl()).into(img);
            tv.setText(temp.getName());
            tv1.setText(temp.getDate());
            tv2.setText(temp.getDesc());
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


    class SpectacleTask extends AsyncTask<Void, Void, Void> {
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

            Spectacles = new HashMap<>();

            String tag_string_req = "req_get_event";

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
                        //loadingDialog.hideDialog();
                    } catch (Exception e) {
                        Log.d(TAG, "ERROR");
                        session.setSpectacle(false);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Events error: " + error.getMessage());
                    session.setSpectacle(false);
                    Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                }
            });

            // Adding request to request queue
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
        Picasso.with(getContext()).load("https://ldzmusictheatre.000webhostapp.com/images/"+ temp.getImgUrl()).into(bigImage);
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
}