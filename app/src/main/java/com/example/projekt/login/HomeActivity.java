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
//public class HomeActivity extends AppCompatActivity {
//    private static final String TAG = HomeActivity.class.getSimpleName();
//
//    private TextView txtName, txtEmail;
//    private Button btnChangePass, btnLogout;
//    private SessionManager session;
//    private DatabaseHandler db;
//
//    private ProgressDialog pDialog;
//
//    private HashMap<String,String> user = new HashMap<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        txtName = findViewById(R.id.tFullname);
//        txtEmail = findViewById(R.id.tEmail);
//        btnChangePass = findViewById(R.id.btnPassword);
//        btnLogout = findViewById(R.id.btnLogout);
//
//        // Progress dialog
//        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);
//
//        db = new DatabaseHandler(getApplicationContext());
//        user = db.getUserDetails();
//
//        // session manager
//        session = new SessionManager(getApplicationContext());
//
//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }
//
//        // Fetching user details from database
//        String name = user.get("name");
//        String email = user.get("email");
//
//        // Displaying the user details on the screen
//        txtName.setText(name);
//        txtEmail.setText(email);
//
//        // Hide Keyboard
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        init();
//    }
//
//    private void init() {
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logoutUser();
//            }
//        });
//
//        btnChangePass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
//                LayoutInflater inflater = getLayoutInflater();
//                View dialogView = inflater.inflate(R.layout.change_password, null);
//
//                dialogBuilder.setView(dialogView);
//                dialogBuilder.setTitle("Change Password");
//                dialogBuilder.setCancelable(false);
//
//                final EditText oldPassword = dialogView.findViewById(R.id.etOld);
//                final EditText newPassword = dialogView.findViewById(R.id.etNew);
//
//                dialogBuilder.setPositiveButton("Change",  new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // empty
//                    }
//                });
//
//                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                final AlertDialog alertDialog = dialogBuilder.create();
//
//                TextWatcher textWatcher = new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        if(oldPassword.getText().length() > 0 &&
//                                newPassword.getText().length() > 0){
//                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
//                        } else {
//                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//                        }
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                    }
//                };
//
//                oldPassword.addTextChangedListener(textWatcher);
//                newPassword.addTextChangedListener(textWatcher);
//
//                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(final DialogInterface dialog) {
//                        final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                        b.setEnabled(false);
//
//                        b.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if (checkNetworkConnection() == true) {
//                                    String email = user.get("email");
//                                    String old_pass = oldPassword.getText().toString();
//                                    String new_pass = newPassword.getText().toString();
//
//                                    if (!old_pass.isEmpty() && !new_pass.isEmpty()) {
//                                        changePassword(email, old_pass, new_pass);
//                                        dialog.dismiss();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Fill all values!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                                else {
//                                    noInternetConnectionDialog();
//                                }
//                            }
//                        });
//                    }
//                });
//
//                alertDialog.show();
//            }
//        });
//    }
//
//    private void logoutUser() {
//        session.setLogin(false);
//        // Launching the login activity
//        Functions logout = new Functions();
//        logout.logoutUser(getApplicationContext());
//        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void changePassword(final String email, final String old_pass, final String new_pass) {
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
//                params.put("tag", "change_pass");
//                params.put("email", email);
//                params.put("old_password", old_pass);
//                params.put("password", new_pass);
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
//                        if (checkNetworkConnection()) {
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
//}