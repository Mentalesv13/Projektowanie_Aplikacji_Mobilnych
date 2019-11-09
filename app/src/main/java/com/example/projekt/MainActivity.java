package com.example.projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projekt.fragment.Blank;
import com.example.projekt.fragment.Contact;
import com.example.projekt.fragment.NewsMini;
import com.example.projekt.fragment.Repertoire;
import com.example.projekt.fragment.SpektakleView;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.login.HomeActivity;
import com.example.projekt.maps.MapsActivity;
import com.example.projekt.ui.Events.Events;
import com.example.projekt.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private SessionManager session;
    private DatabaseHandler db;
    private HashMap<String,String> user = new HashMap<>();
    MenuItem navAccount;
    NavController navController;
    TextView navHeader;
    TextView navText;
    Fragment fragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_repertoire, R.id.nav_spectacle,
                R.id.nav_events, R.id.nav_news, R.id.nav_map,R.id.nav_contact,R.id.nav_login)
                .setDrawerLayout(drawer)
                .build();
        Menu menu = navigationView.getMenu();
        navAccount = menu.findItem(R.id.nav_login);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        View headerView = navigationView.getHeaderView(0);
        navHeader = headerView.findViewById(R.id.navHeader);
        navText = headerView.findViewById(R.id.navText);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                             @Override
                                                             public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                                                                 int id = menuItem.getItemId();

                                                                 String title = getString(R.string.app_name);
                                                                     switch (id) {
                                                                         case R.id.nav_home: {
                                                                             fragment = new HomeFragment();
                                                                             title = "Home";
                                                                             break;
                                                                         }
                                                                         case R.id.nav_repertoire: {
                                                                             fragment = new Repertoire();
                                                                             title = "Repertoire";
                                                                             break;
                                                                         }

                                                                         case R.id.nav_spectacle: {
                                                                             fragment = new SpektakleView();
                                                                             title = "Spectacle";
                                                                             break;
                                                                         }

                                                                         case R.id.nav_events: {
                                                                             fragment = new Events();
                                                                             title = "Events";
                                                                             break;
                                                                         }
                                                                         case R.id.nav_news: {
                                                                             fragment = new NewsMini();
                                                                             title = "News";
                                                                             break;
                                                                         }
                                                                         case R.id.nav_map: {
                                                                             title = "";
                                                                             Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                                                                             startActivity(myIntent);
                                                                             break;
                                                                         }
                                                                         case R.id.nav_contact: {
                                                                             fragment = new Contact();
                                                                             title = "Contact";
                                                                             break;
                                                                         }
                                                                         case R.id.nav_login: {
                                                                             title = "";
                                                                             Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
                                                                             startActivity(myIntent);
                                                                             break;
                                                                         }

                                                                         default:
                                                                             return true;
                                                                     }
                                                                 if (checkNetworkConnection()==true) {
                                                                     if (fragment != null) {
                                                                         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                                                         ft.replace(R.id.content_frame, fragment);
                                                                         ft.commit();
                                                                     }
                                                                     if (getSupportActionBar() != null) {
                                                                         getSupportActionBar().setTitle(title);
                                                                     }
                                                                     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                                                     drawer.closeDrawer(GravityCompat.START);
                                                                 }else {
                                                                     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                                                     drawer.closeDrawer(GravityCompat.START);
                                                                     noInternetConnectionDialog();
                                                                     FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                                                     ft.replace(R.id.content_frame, new Blank());
                                                                     ft.commit();
                                                                 }
                                                                 return true;
                                                             }
        });
        //NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Fetching user details from
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            String name = user.get("name");
            String email = user.get("email");
            navHeader.setText("Hello! " + name);
            navText.setText(email);
            navAccount.setTitle("Your Account");
            navAccount.setIcon(R.drawable.ic_login_black);

        }
        else
        {
            navHeader.setText(getString(R.string.nav_header_title));
            navText.setText(getString(R.string.nav_header_subtitle));
            String redString = getResources().getString(R.string.log_in);
            navAccount.setTitle(Html.fromHtml(redString));

            navAccount.setIcon(R.drawable.ic_login_red);
        }
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {

            String name = user.get("name");
            String email = user.get("email");
            navHeader.setText("Hello! " + name);
            navText.setText(email);
            navAccount.setTitle("Your Account");
            navAccount.setIcon(R.drawable.ic_login_black);
        }
       else
        {
            navHeader.setText(getString(R.string.nav_header_title));
            navText.setText(getString(R.string.nav_header_subtitle));
            String redString = getResources().getString(R.string.log_in);
            navAccount.setTitle(Html.fromHtml(redString));
            navAccount.setIcon(R.drawable.ic_login_red);
        }

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void noInternetConnectionDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_internet_connection, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("NoInternetConnection");
        dialogBuilder.setCancelable(false);

        //final EditText mEditEmail = dialogView.findViewById(R.id.etEmailR);

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
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment);
                            ft.commit();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Check Internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        else return false;
    }
}

