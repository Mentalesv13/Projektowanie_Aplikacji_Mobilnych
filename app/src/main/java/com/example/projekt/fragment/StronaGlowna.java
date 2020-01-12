package com.example.projekt.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import com.example.projekt.R;

public class StronaGlowna extends Fragment {
    public static ViewFlipper viewFlipper;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strona_glowna, container, false);
        image1 = view.findViewById(R.id.image1);
        image1.setImageResource(R.drawable.les_miserables);
        image2 = view.findViewById(R.id.image2);
        image2.setImageResource(R.drawable.miss_saigon);
        image3 = view.findViewById(R.id.image3);
        image3.setImageResource(R.drawable.madagaskar);
        viewFlipper = view.findViewById(R.id.v_flipper);
        viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();
        return view;
    }

    @Override
    public void onResume() {
        viewFlipper.startFlipping();
        super.onResume();
    }

}