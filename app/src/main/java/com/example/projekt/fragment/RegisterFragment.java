package com.example.projekt.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.SlideMain;
import com.example.projekt.helper.Functions;
import com.example.projekt.login.EmailVerify;
import com.example.projekt.login.RegisterActivity;
import com.example.projekt.login.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btnRegister;
    private TextView btnLinkToLogin;
    private EditText inputName, inputEmail, inputPassword;
    private ProgressDialog pDialog;
    private ImageView btnF, btnT, btnP, btnI;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_register_fragment, container, false);

        inputName = view.findViewById(R.id.etFullname);
        inputEmail = view.findViewById(R.id.etLogin);
        inputPassword = view.findViewById(R.id.etPass);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnLinkToLogin = view.findViewById(R.id.tResend);
        btnF = view.findViewById(R.id.btnFacebook);
        btnT = view.findViewById(R.id.btnTwitter);
        btnP = view.findViewById(R.id.btnPinterest);
        btnI = view.findViewById(R.id.btnInstagram);

        btnF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/teatrmuzycznylodz/timeline"));
                startActivity(browserIntent);
            }});
        btnT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/tm_lodz"));
                startActivity(browserIntent);
            }});
        btnP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.pinterest.com/teatrmuzyczny/"));
                startActivity(browserIntent);
            }});
        btnI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/teatrmuzycznylodz/"));
                startActivity(browserIntent);
            }});

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // Hide Keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        return view;
    }

    private void init() {
        // Login button Click Event
        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (checkNetworkConnection()) {
                    String name = inputName.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();

                    // Check for empty data in the form
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                        if (Functions.isValidEmailAddress(email)) {
                            registerUser(name, email, password);
                        } else {
                            Toast.makeText(getActivity(), "Email is not valid (yourmail@gmail.com)!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please enter your details!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    noInternetConnectionDialog();
                }
            }
        });

        // Link to Register Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.content_frame, new LoginFragment());
//                ft.commit();
                SlideMain activity;
                activity = (SlideMain) getActivity();
                activity.changeFragment(new LoginFragment());
            }
        });
    }

    private void registerUser(final String name, final String email, final String password) {
        // Tag used to cancel the request
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
                        logout.logoutUser(getActivity());

                        Bundle b = new Bundle();
                        b.putString("email", email);
                        Intent i = new Intent(getActivity(), EmailVerify.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtras(b);
                        startActivity(i);
                        pDialog.dismiss();
                        getActivity().finish();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage(), error);
                Toast.makeText(getActivity(), "Connection problem", Toast.LENGTH_LONG).show();
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

        // Adding request to request queue
        RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void noInternetConnectionDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_internet_connection, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("NoInternetConnection");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("Reload",  new DialogInterface.OnClickListener() {
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

                        if (checkNetworkConnection()==true) {
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Check Internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        else return false;
    }

    public void facebookButton(View v){
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/teatrmuzycznylodz/timeline"));
        startActivity(browserIntent);
    }

    public void twitterButton(View v){
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/tm_lodz"));
        startActivity(browserIntent);
    }

    public void instagramButton(View v){
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.instagram.com/teatrmuzycznylodz/"));
        startActivity(browserIntent);
    }

    public void pinterestButton(View v){
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.pinterest.com/teatrmuzyczny/"));
        startActivity(browserIntent);
    }
}