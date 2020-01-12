package com.example.projekt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.Functions;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.login.RequestManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Iterator;

import static com.example.projekt.login.RequestManager.TAG;

public class SplashScreen extends AppCompatActivity {
    private DatabaseHandler db;
    SessionManager session;
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        db = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        db.resetTables();
        session.setRepertoire(false);
        session.setEvent(false);
        session.setSpectacle(false);
        final SplashTask task = new SplashTask();
        task.execute();
        //StartAnimations();
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l = findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = findViewById(R.id.splashImage);
        iv.clearAnimation();
        iv.startAnimation(anim);
        TextView tv = findViewById(R.id.splashText);
        tv.clearAnimation();
        tv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 4000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreen.this,
                            SlideMain.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }

            }
        };
        splashTread.start();
    }


    class SplashTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {StartAnimations();
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

            //final HashMap<Integer, Event> Events = new HashMap<>();

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

                            //Events.put(tempID, new Event(id, name, sdesc, desc));
                            db.addEvent(id.toString(), name,sdesc,desc);

                            tempID++;
                        }
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
                    //Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                }
            });

            // Adding request to request queue
            RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);

            tag_string_req = "req_get_repertoire";

                strReq = new StringRequest(Request.Method.GET,
                        Functions.GET_REPERTOIRE_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        JSONParser parser = new JSONParser();

                        try {
                            Object obj = parser.parse(response);

                            JSONObject jsonObject = (JSONObject) obj;

                            //String error = (String) jsonObject.get("error");
                            //Log.d(TAG, "Error: " + error);
                            //Log.d(TAG, response);
                            JSONArray eventsList = (JSONArray) jsonObject.get("repertoire");
                            Iterator i = eventsList.iterator();

                            int tempID = 0;

                            while (i.hasNext()) {
                                JSONObject events = (JSONObject) i.next();
                                Long id = (Long) events.get("id");
                                String name = (String) events.get("name");
                                String date = (String) events.get("date");
                                String tag = (String) events.get("tag");
                                //Log.d(TAG, "ID: " + id + "Name: " + name + "date: " + date + "tag: " + tag);

                                //Repertoire.put(tempID, new Repertoire(id, name, date, tag));
                                db.addRepertoire(id.toString(), name, date, tag);

                                tempID++;
                            }
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
                        //Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                    }
                });
                RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);

            tag_string_req = "req_get_spectacle";

            strReq = new StringRequest(Request.Method.GET,
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

                            //Spectacles.put(tempID, new Spektakle(Long.parseLong(id), name, desc, date, imgUrl));
                            db.addSpectacle(id, name, desc, date, imgUrl);

                            tempID++;
                        }
                        session.setSpectacle(true);
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
                    Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                }
            });

            // Adding request to request queue
            RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);

            return null;
        }
    }

}