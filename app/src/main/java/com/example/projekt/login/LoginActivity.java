//package com.example.projekt.login;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.example.projekt.MainActivity;
//import com.example.projekt.R;
//import com.example.projekt.helper.DatabaseHandler;
//import com.example.projekt.helper.Functions;
//import com.example.projekt.helper.SessionManager;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class LoginActivity extends AppCompatActivity {
//    private static final String TAG = LoginActivity.class.getSimpleName();
//
//    private static String KEY_UID = "uid";
//    private static String KEY_NAME = "name";
//    private static String KEY_EMAIL = "email";
//    private static String KEY_CREATED_AT = "created_at";
//
//    private Button btnLogin;
//    private TextView btnLinkToRegister, btnForgotPass;
//    private EditText inputEmail, inputPassword;
//    private ProgressDialog pDialog;
//
//    private SessionManager session;
//    private DatabaseHandler db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        inputEmail = findViewById(R.id.etLogin);
//        inputPassword = findViewById(R.id.etPass);
//        btnLogin = findViewById(R.id.btnLogin);
//        btnLinkToRegister = findViewById(R.id.tResend);
//        btnForgotPass = findViewById(R.id.tForgot);
//
//        // Progress dialog
//        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);
//
//        // create sqlite database
//        db = new DatabaseHandler(getApplicationContext());
//
//        // session manager
//        session = new SessionManager(getApplicationContext());
//
//        // check user is already logged in
//        if (session.isLoggedIn()) {
//            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(i);
//            finish();
//        }
//
//        // Hide Keyboard
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        init();
//    }
//
//    private void init() {
//        // Login button Click Event
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//
//                            String email = inputEmail.getText().toString().trim();
//                String password = inputPassword.getText().toString().trim();
//                if (checkNetworkConnection() == true) {
//                    // Check for empty data in the form
//                    if (!email.isEmpty() && !password.isEmpty()) {
//                        if (Functions.isValidEmailAddress(email)) {
//                            // login user
//                            loginProcess(email, password);
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Email is not valid (youremail@gmail.com)!", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        // Prompt user to enter credentials
//                        Toast.makeText(getApplicationContext(), "Please fill all fields!", Toast.LENGTH_LONG).show();
//                    }
//                }
//                else
//                {
//                    noInternetConnectionDialog();
//                }
//            }
//
//        });
//
//        // Link to Register Screen
//        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(i);
//            }
//        });
//
//        // Forgot Password Dialog
//        btnForgotPass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                forgotPasswordDialog();
//            }
//        });
//    }
//
//    private void forgotPasswordDialog() {
//        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.reset_password, null);
//
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle("Forgot Password");
//        dialogBuilder.setCancelable(false);
//
//        final EditText mEditEmail = dialogView.findViewById(R.id.etEmailR);
//
//        dialogBuilder.setPositiveButton("Reset",  new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // empty
//            }
//        });
//
//        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//
//        mEditEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(mEditEmail.getText().length() > 0){
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
//                } else {
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(final DialogInterface dialog) {
//                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                b.setEnabled(false);
//
//                b.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (checkNetworkConnection() == true) {
//                            String email = mEditEmail.getText().toString();
//
//                            if (!email.isEmpty()) {
//                                if (Functions.isValidEmailAddress(email)) {
//                                    resetPassword(email);
//                                    dialog.dismiss();
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Email is not valid!", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Fill all values!", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                        else
//                            {
//                            noInternetConnectionDialog();
//                        }
//                    }
//                    {}
//                });
//            }
//        });
//
//        alertDialog.show();
//    }
//
//    private void noInternetConnectionDialog() {
//        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.fragment_internet_connection, null);
//
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle("NoInternetConnection");
//        dialogBuilder.setCancelable(false);
//
//        //final EditText mEditEmail = dialogView.findViewById(R.id.etEmailR);
//
//        dialogBuilder.setPositiveButton("Reload",  new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // empty
//            }
//        });
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//
//        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(final DialogInterface dialog) {
//                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                b.setEnabled(true);
//
//                b.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    if (checkNetworkConnection()==true) {
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Check Internet connection!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//
//        alertDialog.show();
//    }
//
//    public boolean checkNetworkConnection() {
//        ConnectivityManager connectManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnected())
//        {
//            return true;
//        }
//        else return false;
//    }
//
//
//    private void loginProcess(final String email, final String password) {
//        // Tag used to cancel the request
//        String tag_string_req = "req_login";
//
//        pDialog.setMessage("Logging in ...");
//        showDialog();
//
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                Functions.LOGIN_URL, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Login Response: " + response);
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//
//                    // Check for error node in json
//                    if (!error) {
//                        // user successfully logged in
//                        JSONObject json_user = jObj.getJSONObject("user");
//
//                        Functions logout = new Functions();
//                        logout.logoutUser(getApplicationContext());
//
//                        if(Integer.parseInt(json_user.getString("verified")) == 1){
//                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
//                            Intent upanel = new Intent(LoginActivity.this, MainActivity.class);
//                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(upanel);
//
//                            session.setLogin(true);
//                            finish();
//                        } else {
//                            Bundle b = new Bundle();
//                            b.putString("email", email);
//                            Intent upanel = new Intent(LoginActivity.this, EmailVerify.class);
//                            upanel.putExtras(b);
//                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(upanel);
//                            finish();
//                        }
//
//                    } else {
//                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("message");
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", email);
//                params.put("password", password);
//
//                return params;
//            }
//
//        };
//
//        // Adding request to request queue
//        RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }
//
//    private void resetPassword(final String email) {
//        // Tag used to cancel the request
//        String tag_string_req = "req_reset_pass";
//
//        pDialog.setMessage("Please wait...");
//        showDialog();
//
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                Functions.RESET_PASS_URL, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Reset Password Response: " + response);
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//
//                    if (!error) {
//                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Reset Password Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<>();
//
//                params.put("tag", "forgot_pass");
//                params.put("email", email);
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//
//        };
//
//        // Adding request to volley request queue
//        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
//        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
//        RequestManager.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }
//
//    private void showDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }
//}
