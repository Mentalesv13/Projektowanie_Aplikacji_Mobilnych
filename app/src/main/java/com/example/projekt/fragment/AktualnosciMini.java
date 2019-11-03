package com.example.projekt.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.projekt.R;

public class AktualnosciMini extends Fragment {
    TextView descText;
    ImageButton show1, hide1;
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {

View view = inflater.inflate(R.layout.fragment_aktualnoscimini, container, false);
        descText = view.findViewById(R.id.ll3_tv2);
        show1 = view.findViewById(R.id.show);
        show1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                show1.setVisibility(View.INVISIBLE);
                hide1.setVisibility(View.VISIBLE);
                descText.setMaxLines(Integer.MAX_VALUE);

            }
        });
        hide1 = view.findViewById(R.id.hide);
        hide1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hide1.setVisibility(View.INVISIBLE);
                show1.setVisibility(View.VISIBLE);
                descText.setMaxLines(3);

            }
        });

        return view;

    }
}
