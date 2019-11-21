package com.example.projekt.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
        return 11;
    }


}