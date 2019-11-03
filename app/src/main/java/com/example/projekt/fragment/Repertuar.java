package com.example.projekt.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projekt.pager.PagerAdapter;
import com.example.projekt.R;
import com.example.projekt.pager.TabCzerwiec;
import com.example.projekt.pager.TabLipiec;
import com.example.projekt.pager.TabMaj;

public class Repertuar extends Fragment {
    TabLayout tabLayout;
    ViewPager pager;
    private PagerAdapter _adapter;
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repertuar, container, false);
        tabLayout = (TabLayout)view.findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("MAJ"));
        tabLayout.addTab(tabLayout.newTab().setText("CZERWIEC"));
        tabLayout.addTab(tabLayout.newTab().setText("LIPIEC"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pager = (ViewPager) view.findViewById(R.id.pager);

        this._adapter = new PagerAdapter(getChildFragmentManager());
        this._adapter.add(new TabMaj());
        this._adapter.add(new TabCzerwiec());
        this._adapter.add(new TabLipiec());
        pager.setAdapter(this._adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
}
