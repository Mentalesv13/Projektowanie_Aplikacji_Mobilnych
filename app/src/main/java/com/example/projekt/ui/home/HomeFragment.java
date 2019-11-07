package com.example.projekt.ui.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.example.projekt.R;

public class HomeFragment extends Fragment {

        public static ViewFlipper viewFlipper;

        int images [] = {R.drawable.les_miserables, R.drawable.les_miserables};
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strona_glowna, container, false);
        viewFlipper = view.findViewById(R.id.v_flipper);
        viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left );
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