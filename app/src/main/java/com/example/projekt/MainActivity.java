package com.example.projekt;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button menuButton;
    private Button menuX;
    private FragmentTransaction fragmentTransaction;

    private Repertuar fragmentRepertuar;
    private StronaGlowna fragmentStronaGlowna;
    private Spektakle fragmentSpektakle;
    private Wydarzenia fragmentWydarzenia;
    private Aktualnosci fragmentAktualnosci;
    private Bilety fragmentBilety;
    private Edukacja fragmentEdukacja;
    private Kontakt fragmentKontakt;
    private Menu fragmentMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuButton = (Button) findViewById(R.id.menu);
        menuX = (Button) findViewById(R.id.menux);

        fragmentRepertuar = new Repertuar();
        fragmentStronaGlowna = new StronaGlowna();
        fragmentSpektakle = new Spektakle();
        fragmentWydarzenia = new Wydarzenia();
        fragmentAktualnosci = new Aktualnosci();
        fragmentBilety = new Bilety();
        fragmentEdukacja = new Edukacja();
        fragmentKontakt = new Kontakt();
        fragmentMenu = new Menu();
        setFragment(fragmentStronaGlowna);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentStronaGlowna.viewFlipper.stopFlipping();
                menuButton.setVisibility(View.INVISIBLE);
                menuX.setVisibility(View.VISIBLE);
                setFragment(fragmentMenu);
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
        setFragment(fragmentStronaGlowna);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
        fragmentStronaGlowna.viewFlipper.startFlipping();
    }

    public void reperturarButton(View v)
    {
        setFragment(fragmentRepertuar);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
    }
    public void spektakleButton(View v)
    {
        setFragment(fragmentSpektakle);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
    }
    public void wydarzeniaButton(View v)
    {
        setFragment(fragmentWydarzenia);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
    }
    public void aktualnosciButton(View v)
    {
        setFragment(fragmentAktualnosci);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
    }
    public void biletyButton(View v)
    {
        setFragment(fragmentBilety);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
    }
    public void edukacjaButton(View v)
    {
        setFragment(fragmentEdukacja);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
    }
    public void kontaktButton(View v)
    {
        setFragment(fragmentKontakt);
        menuButton.setVisibility(View.VISIBLE);
        menuX.setVisibility(View.INVISIBLE);
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
        FragmentManager fm = getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_switch, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(0,0);
        fragmentTransaction.commit();
    }
}