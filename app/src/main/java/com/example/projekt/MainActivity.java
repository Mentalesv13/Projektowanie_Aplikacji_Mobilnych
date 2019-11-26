package com.example.projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projekt.fragment.Contact;
import com.example.projekt.fragment.HomeFragment;
import com.example.projekt.fragment.LoginFragment;
import com.example.projekt.fragment.NewsMini;
import com.example.projekt.fragment.RepertoireView;
import com.example.projekt.fragment.SpektakleView;
import com.example.projekt.helper.DatabaseHandler;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.maps.MapsActivity;
import com.example.projekt.ui.events.Events;
import com.example.projekt.ui.home.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private SessionManager session;
    private DatabaseHandler db;
    Handler mHandler = new Handler();
    private HashMap<String, String> user = new HashMap<>();
    MenuItem navAccount;
    NavController navController;
    NavigationView navigationView;
    DrawerLayout drawer;
    TextView navHeader;
    TextView navText;
    Fragment fragment = null;
    int fragmentFlag = 0;
    int navigationTitle = 0;
    Runnable mPendingRunnable;
    String title = "Home";
    private boolean isDrawerOpen = false;

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

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_repertoire, R.id.nav_spectacle,
                R.id.nav_events, R.id.nav_news, R.id.nav_map, R.id.nav_contact, R.id.nav_login)
                .setDrawerLayout(drawer)
                .build();
        Menu menu = navigationView.getMenu();
        navAccount = menu.findItem(R.id.nav_login);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        View headerView = navigationView.getHeaderView(0);

        navHeader = headerView.findViewById(R.id.navHeader);
        navText = headerView.findViewById(R.id.navText);


        fragmentFlag = 0;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new MainFragment());
        ft.addToBackStack(String.valueOf(R.id.nav_home));
        ft.commit();
        navigationView.setCheckedItem(R.id.nav_home);

        drawer.closeDrawer(GravityCompat.START);


        drawer.setDrawerListener(this);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_home: {
                        fragment = new MainFragment();
                        title = "Home";
                        navigationTitle = R.id.nav_home;
                        break;
                    }
                    case R.id.nav_repertoire: {
                        fragment = new RepertoireView();
                        title = "Repertoire";
                        navigationTitle = R.id.nav_repertoire;
                        break;
                    }

                    case R.id.nav_spectacle: {
                        fragment = new SpektakleView();
                        title = "Spectacle";
                        navigationTitle = R.id.nav_spectacle;
                        break;
                    }

                    case R.id.nav_events: {
                        fragment = new Events();
                        title = "Events";
                        navigationTitle = R.id.nav_events;
                        break;
                    }
                    case R.id.nav_news: {
                        fragment = new NewsMini();
                        title = "News";
                        navigationTitle = R.id.nav_news;
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
                        navigationTitle = R.id.nav_contact;
                        break;
                    }
                    case R.id.nav_login: {
                        title = "Account";
                        //Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
                        //startActivity(myIntent);
                        if(session.isLoggedIn()){fragment = new HomeFragment();
                        }else
                        fragment = new LoginFragment();
                        navigationTitle = R.id.nav_login;
                        //navigationView.setCheckedItem(R.id.nav_login);
                        break;
                    }

                    default:
                        return true;
                }

                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        if (fragment != null && (fragmentFlag != navigationTitle)) {
                            fragmentFlag = navigationTitle;
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment);
                            ft.addToBackStack(String.valueOf(navigationTitle));
                            ft.commit();
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(title);
                            }
                        }
                    }
                };
                //if (checkNetworkConnection()==true) {

//                if (fragment != null && (fragmentFlag != navigationTitle)) {
//                    fragmentFlag = navigationTitle;
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.content_frame, fragment);
//                    ft.addToBackStack(String.valueOf(navigationTitle));
//                    ft.commit();
//                }
                //if (getSupportActionBar() != null) {
                //    getSupportActionBar().setTitle(title);
                //}
                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                drawer.closeDrawer(GravityCompat.START);
                // }else {
                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                //drawer.closeDrawer(GravityCompat.START);
                //noInternetConnectionDialog();
                //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //ft.replace(R.id.content_frame, new Blank());
                //ft.commit();
                //}
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

        } else {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {

            String name = user.get("name");
            String email = user.get("email");
            navHeader.setText("Hello! " + name);
            navText.setText(email);
            navAccount.setTitle("Your Account");
            navAccount.setIcon(R.drawable.ic_login_black);
        } else {
            navHeader.setText(getString(R.string.nav_header_title));
            navText.setText(getString(R.string.nav_header_subtitle));
            String redString = getResources().getString(R.string.log_in);
            navAccount.setTitle(Html.fromHtml(redString));
            navAccount.setIcon(R.drawable.ic_login_red);
        }

        return NavigationUI.navigateUp(navController, mAppBarConfiguration);
                //|| super.onSupportNavigateUp();
    }


    private void noInternetConnectionDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_internet_connection, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("NoInternetConnection");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("Reload", new DialogInterface.OnClickListener() {
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

                        if (checkNetworkConnection()) {
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
        ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else return false;
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count <= 1) {
            finish();
        }
        else {
            int title = Integer.parseInt(fragmentManager.getBackStackEntryAt(count-2).getName());
            if (count == 2) {
                // here I am using a NavigationDrawer and open it when transitioning to the initial fragment
                // a second back-press will result in finish() being called above.
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            }
            super.onBackPressed();
            fragmentFlag = title;
            //Log.v(TAG, "onBackPressed - title="+title);

            navigationView.setCheckedItem(title);
            String titles = (String) navigationView.getCheckedItem().getTitle();
            getSupportActionBar().setTitle(titles);
        }
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        if(slideOffset > .55 && !isDrawerOpen){
            onDrawerOpened(drawerView);
            isDrawerOpen = true;
        } else if(slideOffset < .45 && isDrawerOpen) {
            onDrawerClosed(drawerView);
            isDrawerOpen = false;
        }
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

        invalidateOptionsMenu();

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
            mPendingRunnable = null;
        }
    }


    @Override
    public void onDrawerStateChanged(int newState) {

    }

}

