package com.example.projekt;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.projekt.fragment.AktualnosciMini;
import com.example.projekt.fragment.Bilety;
import com.example.projekt.fragment.InternetConnection;
import com.example.projekt.fragment.Kontakt;
import com.example.projekt.fragment.Menu;
import com.example.projekt.fragment.Repertuar;
import com.example.projekt.fragment.SpektakleView;
import com.example.projekt.fragment.StronaGlowna;
import com.example.projekt.fragment.Wydarzenia;
import com.example.projekt.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private Button menuButton, reloadButton, menuX, loginButton;
    private View logoButton;
    private ImageView instagramButton, facebookButton, twitterButton, pinterestButton;

    private FragmentTransaction fragmentTransaction;

    private Fragment fragmentCache;
    private Repertuar fragmentRepertuar;
    private StronaGlowna fragmentStronaGlowna;
    private SpektakleView fragmentSpektakle;
    private Wydarzenia fragmentWydarzenia;
    private AktualnosciMini fragmentAktualnosci;
    private Bilety fragmentBilety;
    private Kontakt fragmentKontakt;
    private Menu fragmentMenu;
    private InternetConnection fragmentInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuButton = (Button) findViewById(R.id.menu);
        reloadButton = (Button) findViewById(R.id.reload);
        menuX = (Button) findViewById(R.id.menux);
        pinterestButton = (ImageView) findViewById(R.id.facebookIcon);
        facebookButton = (ImageView) findViewById(R.id.pinterestIcon);
        twitterButton = (ImageView) findViewById(R.id.twitterIcon);
        instagramButton = (ImageView) findViewById(R.id.instagramIcon);
        logoButton = (View) findViewById(R.id.logo);
        loginButton = (Button) findViewById(R.id.bLogin);

        fragmentRepertuar = new Repertuar();
        fragmentStronaGlowna = new StronaGlowna();
        fragmentSpektakle = new SpektakleView();
        fragmentWydarzenia = new Wydarzenia();
        fragmentAktualnosci = new AktualnosciMini();
        fragmentBilety = new Bilety();
        fragmentKontakt = new Kontakt();
        fragmentMenu = new Menu();
        fragmentInternetConnection = new InternetConnection();

        fragmentCache = fragmentStronaGlowna;
        setFragment(fragmentCache);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentStronaGlowna.viewFlipper.stopFlipping();
                menuButton.setVisibility(View.INVISIBLE);
                pinterestButton.setVisibility(View.INVISIBLE);
                facebookButton.setVisibility(View.INVISIBLE);
                twitterButton.setVisibility(View.INVISIBLE);
                instagramButton.setVisibility(View.INVISIBLE);

                menuX.setVisibility(View.VISIBLE);
                fragmentCache = fragmentMenu;
                setFragment(fragmentCache);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setVisibility(View.INVISIBLE);
                pinterestButton.setVisibility(View.INVISIBLE);
                facebookButton.setVisibility(View.INVISIBLE);
                twitterButton.setVisibility(View.INVISIBLE);
                instagramButton.setVisibility(View.INVISIBLE);

                menuX.setVisibility(View.VISIBLE);
                setFragment(fragmentCache);
            }
        });

       menuX.setOnClickListener(new View.OnClickListener(){

       @Override
        public void onClick(View view) {
           menuButton.setVisibility(View.VISIBLE);
           menuX.setVisibility(View.INVISIBLE);
           MainActivity.this.onBackPressed();
       }
       });
    }

    public void glownaButton(View v){
        fragmentCache = fragmentStronaGlowna;
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        setFragment(fragmentCache);
        //fragmentStronaGlowna.viewFlipper.startFlipping();
    }

    public void reperturarButton(View v)
    {
        fragmentCache = fragmentRepertuar;
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        setFragment(fragmentRepertuar);
    }
    public void spektakleButton(View v)
    {
        fragmentCache = fragmentSpektakle;
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        setFragment(fragmentSpektakle);
    }
    public void wydarzeniaButton(View v)
    {
        fragmentCache = fragmentWydarzenia;
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        setFragment(fragmentWydarzenia);
    }
    public void aktualnosciButton(View v)
    {
        fragmentCache = fragmentAktualnosci;
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        setFragment(fragmentAktualnosci);
    }
    public void biletyButton(View v)
    {
        fragmentCache = fragmentBilety;
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        setFragment(fragmentBilety);
    }

    public void kontaktButton(View v)
    {
        fragmentCache = fragmentKontakt;
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        setFragment(fragmentKontakt);
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

    public void setFragment(Fragment fragment) {
        if (checkNetworkConnection() == true) {
            buttonShow();
            logoButton.setClickable(true);
            if (fragmentCache==fragmentMenu){
                menuX.setVisibility(View.VISIBLE);
            }
            else
                menuButton.setVisibility(View.VISIBLE);

            reloadButton.setVisibility(View.INVISIBLE);

            FragmentManager fm = getSupportFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_switch, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setCustomAnimations(0, 0);
            fragmentTransaction.commit();
        } else {
            noInternetConnection();
            FragmentManager fm = getSupportFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_switch, fragmentInternetConnection);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setCustomAnimations(0, 0);
            fragmentTransaction.commit();
        }
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

    public void noInternetConnection(){
        buttonHide();
        menuButton.setVisibility(View.INVISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        logoButton.setClickable(false);
        reloadButton.setVisibility(View.VISIBLE);
    }
    public void buttonShow(){
        pinterestButton.setVisibility(View.VISIBLE);
        facebookButton.setVisibility(View.VISIBLE);
        twitterButton.setVisibility(View.VISIBLE);
        instagramButton.setVisibility(View.VISIBLE);
    }
    public void buttonHide(){
        pinterestButton.setVisibility(View.INVISIBLE);
        facebookButton.setVisibility(View.INVISIBLE);
        twitterButton.setVisibility(View.INVISIBLE);
        instagramButton.setVisibility(View.INVISIBLE);
    }
}
