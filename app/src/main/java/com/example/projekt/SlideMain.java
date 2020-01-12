package com.example.projekt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.projekt.fragment.HomeFragment;
import com.example.projekt.fragment.LoginFragment;
import com.example.projekt.fragment.RepertoireView;
import com.example.projekt.fragment.SpektakleView;
import com.example.projekt.fragment.StronaGlowna;
import com.example.projekt.helper.SessionManager;
import com.example.projekt.maps.MapsActivity;
import com.example.projekt.menu.ResideMenu;
import com.example.projekt.menu.ResideMenuItem;
import com.example.projekt.ui.events.Events;


public class SlideMain extends FragmentActivity implements View.OnClickListener{

    private ResideMenu resideMenu;
    private SlideMain mContext;
    private SessionManager session;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemRepertoire;
    private ResideMenuItem itemSpectacle;
    private ResideMenuItem itemNews;
    private ResideMenuItem itemMap;
    private ResideMenuItem itemContact;
    private ResideMenuItem itemEvents;
    private ResideMenuItem itemLogin;
    private TextView title;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_navigation_main);
        mContext = this;
        session = new SessionManager(getApplicationContext());
        setUpMenu();
        if( savedInstanceState == null )
            changeFragment(new StronaGlowna());
    }

    private void setUpMenu() {
        // attach to current activity;
        title = findViewById(R.id.Title);
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(false);
        resideMenu.setBackground(R.drawable.blue);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
         resideMenu.setScaleValue(0.45f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.ic_home,     "Home");
        itemRepertoire  = new ResideMenuItem(this, R.drawable.ic_repertoire,  "Repertoire");
        itemSpectacle = new ResideMenuItem(this, R.drawable.ic_menu_slideshow, "Spectacle");
        itemEvents = new ResideMenuItem(this, R.drawable.ic_events, "Events");
        //itemNews  = new ResideMenuItem(this, R.drawable.ic_news,  "News");
        itemMap  = new ResideMenuItem(this, R.drawable.ic_map,  "Map");
        //itemContact  = new ResideMenuItem(this, R.drawable.ic_menu_send,  "Contact");
        if(session.isLoggedIn()) {
            itemLogin = new ResideMenuItem(this, R.drawable.ic_login, "Profile");
        }
        else {
            itemLogin = new ResideMenuItem(this, R.drawable.ic_login_red, "Log In");
        }


        itemHome.setOnClickListener(this);
        itemRepertoire.setOnClickListener(this);
        itemSpectacle.setOnClickListener(this);
        itemEvents.setOnClickListener(this);
        //itemNews.setOnClickListener(this);
        itemMap.setOnClickListener(this);
        //itemContact.setOnClickListener(this);
        itemLogin.setOnClickListener(this);


        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemRepertoire, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSpectacle, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemEvents, ResideMenu.DIRECTION_LEFT);
        //resideMenu.addMenuItem(itemNews, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemMap, ResideMenu.DIRECTION_LEFT);
        //resideMenu.addMenuItem(itemContact, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogin, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
//        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
//            }
//        });
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return resideMenu.dispatchTouchEvent(ev);
//    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            title.setText("Home");
            changeFragment(new StronaGlowna());
        }else if (view == itemRepertoire){
            changeFragment(new RepertoireView());
            title.setText("Repertoire");
        }else if (view == itemSpectacle){
            changeFragment(new SpektakleView());
            title.setText("Spectacle");
        }else if (view == itemEvents){
            changeFragment(new Events());
            title.setText("Events");
//        }else if (view == itemNews){
//            changeFragment(new NewsMini());
//            title.setText("News");
        }else if (view == itemMap){

            Intent myIntent = new Intent(SlideMain.this, MapsActivity.class);
            startActivity(myIntent);
            overridePendingTransition(0, 0);
//        }else if (view == itemContact){
//            changeFragment(new Contact());
//            title.setText("Contact");
        }else if (view == itemLogin){

            if(session.isLoggedIn()){title.setText("Profile");
                changeFragment(new HomeFragment());
            }else {
                title.setText("Log In");
                changeFragment(new LoginFragment());
            }
        }

        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
            closeKeyboard();
        }

        @Override
        public void closeMenu() {
            //Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    public void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

    public void setActionBarTitle(String home) {
        title.setText(home);
    }


}
