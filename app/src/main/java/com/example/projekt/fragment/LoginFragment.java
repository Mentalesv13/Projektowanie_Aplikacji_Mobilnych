package com.example.projekt.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.projekt.R;
import com.example.projekt.SlideMain;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.Functions;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.login.EmailVerify;
import com.example.projekt.login.LoginActivity;
import com.example.projekt.login.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

    private Button btnLogin;
    private TextView btnLinkToRegister, btnForgotPass;
    private EditText inputEmail, inputPassword;
    private ProgressDialog pDialog;
    private ImageView btnF, btnT, btnP, btnI;

    private SessionManager session;
    private DatabaseHandler db;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_fragment, container, false);

        inputEmail = view.findViewById(R.id.etLogin);
        inputPassword = view.findViewById(R.id.etPass);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLinkToRegister = view.findViewById(R.id.tResend);
        btnForgotPass = view.findViewById(R.id.tForgot);
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
                //getActivity().overridePendingTransition(0, 0);
            }});
        btnT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/tm_lodz"));
                startActivity(browserIntent);
                //getActivity().overridePendingTransition(0, 0);
            }});
        btnP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.pinterest.com/teatrmuzyczny/"));
                startActivity(browserIntent);
                //getActivity().overridePendingTransition(0, 0);
            }});
        btnI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/teatrmuzycznylodz/"));
                startActivity(browserIntent);
                //getActivity().overridePendingTransition(0, 0);
            }});

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // create sqlite database
        db = new DatabaseHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        // Hide Keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        return view;
    }

    private void init() {
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (checkNetworkConnection()) {
                    // Check for empty data in the form
                    if (!email.isEmpty() && !password.isEmpty()) {
                        if (Functions.isValidEmailAddress(email)) {
                            // login user
                            loginProcess(email, password);
                        } else {
                            Toast.makeText(getActivity(), "Email is not valid (youremail@gmail.com)!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Prompt user to enter credentials
                        Toast.makeText(getActivity(), "Please fill all fields!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    noInternetConnectionDialog();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                //ft.replace(getActivity().getSupportFragmentManager().findFragmentByTag("fragment"), new RegisterFragment());
                // ft.commit();
                SlideMain activity;
                activity = (SlideMain) getActivity();
                activity.changeFragment(new RegisterFragment());

            }
        });

        // Forgot Password Dialog
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordDialog();
            }
        });
    }

    private void forgotPasswordDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.reset_password, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Forgot Password");
        dialogBuilder.setCancelable(false);

        final EditText mEditEmail = dialogView.findViewById(R.id.etEmailR);

        dialogBuilder.setPositiveButton("Reset",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();

        mEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mEditEmail.getText().length() > 0){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(false);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkNetworkConnection()) {
                            String email = mEditEmail.getText().toString();

                            if (!email.isEmpty()) {
                                if (Functions.isValidEmailAddress(email)) {
                                    resetPassword(email);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Email is not valid!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Fill all values!", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            noInternetConnectionDialog();
                        }
                    }
                    {}
                });
            }
        });

        alertDialog.show();
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


    private void loginProcess(final String email, final String password) {
        // Tag used to cancel the request
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
                        logout.logoutUser(getActivity());

                        if(Integer.parseInt(json_user.getString("verified")) == 1){
                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                            Intent upanel = new Intent(getActivity(), SlideMain.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);

                            session.setLogin(true);
                            getActivity().finish();
                        } else {
                            Bundle b = new Bundle();
                            b.putString("email", email);
                            Intent upanel = new Intent(getActivity(), EmailVerify.class);
                            upanel.putExtras(b);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
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

        // Adding request to request queue
        RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void resetPassword(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset_pass";

        pDialog.setMessage("Please wait...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.RESET_PASS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Password Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getActivity().getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Reset Password Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("tag", "forgot_pass");
                params.put("email", email);

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
        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
}