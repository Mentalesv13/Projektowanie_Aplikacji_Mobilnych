package com.example.projekt.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.projekt.R;

public class NewsMini extends Fragment {
    TextView descText;
    ImageButton show1, hide1;
    TextView header;
    View view;
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_aktualnoscimini, container, false);
        header = view.findViewById(R.id.aktualnoscimini_tv1);
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

        header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, new NewsMini());
                    ft.commit();

                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("News");

                //DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
                //drawer.closeDrawer(GravityCompat.START);
            }
        });

        return view;

    }
}
