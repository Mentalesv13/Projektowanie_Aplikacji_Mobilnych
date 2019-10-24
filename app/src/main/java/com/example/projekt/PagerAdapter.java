package com.example.projekt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> _fragments;

    public PagerAdapter(FragmentManager activity) {
        super(activity);

        this._fragments = new ArrayList<Fragment>();
    }

    public void add(Fragment fragment) {
        this._fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return this._fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Test";
    }

    @Override
    public int getCount() {
        return 3;
    }


}