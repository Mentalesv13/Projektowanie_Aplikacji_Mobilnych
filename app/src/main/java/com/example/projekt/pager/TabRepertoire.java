package com.example.projekt.pager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.projekt.R;
import com.example.projekt.entity.Repertoire;
import com.example.projekt.helper.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TabRepertoire extends Fragment {
    private DatabaseHandler db;
    String TAG;
    HashMap<Integer,Repertoire> Repertoires;
    View view;

    public TabRepertoire(String TAG) {
        this.TAG = TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = new DatabaseHandler(getContext());
        view = inflater.inflate(R.layout.repertoire_pattern, container, false);
        Repertoires = db.getRepertoireDetail(TAG);
        createRepertoireLayout();
        return view;
    }

    private void createRepertoireLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final LinearLayout abc = view.findViewById(R.id.repertoireLayout);
        Calendar calendar = Calendar.getInstance();
        if (Repertoires.size()>0) {
            for (int i = 0; i < Repertoires.size(); i++) {
                Repertoire temp = Repertoires.get(i);
                final View custom = inflater.inflate(R.layout.repertoire_element, null);
                custom.setTag(i);

                TextView repertoireName = (TextView) custom.findViewById(R.id.repertoireName);
                TextView repertoireTime = (TextView) custom.findViewById(R.id.repertoireTime);
                TextView repertoireDate = (TextView) custom.findViewById(R.id.repertoireDate);
                TextView repertoireDay = (TextView) custom.findViewById(R.id.repertoireDay);
                CardView btnBuy = custom.findViewById(R.id.cardViewReservation1);
                CardView btnReserve = custom.findViewById(R.id.cardViewReservation2);;

                repertoireName.setText(temp.getName_repertoire());
                String tempTime = temp.getTime_repertoire();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                String date = df.format(Calendar.getInstance().getTime());
                try {
                    Date d1 = df.parse(date);

                    Date d2 = df.parse(tempTime);

                    if(d1.compareTo(d2) > 0) {
                        btnBuy.setVisibility(View.GONE);
                        btnReserve.setVisibility(View.GONE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String tempDate = tempTime;
                tempDate = tempDate.substring(0, 10);
                tempTime = tempTime.substring(11, 16);

                //Log.d(TAG, date.toString());

                String[] dates = tempDate.split("-");
                repertoireTime.setText(tempTime);

                if (dates[2].charAt(0) == '0') {
                    repertoireDate.setText(dates[2].substring(1));
                } else repertoireDate.setText(dates[2]);

                repertoireDay.setText(Zellercongruence(Integer.parseInt(dates[2]), Integer.parseInt(dates[1]), Integer.parseInt(dates[0])));
                abc.addView(custom);
            }
        }
        else {
            final View custom = inflater.inflate(R.layout.fragment_tab_lipiec, null);
            abc.addView(custom);
        }
    }

    String Zellercongruence(int day, int month,
                         int year)
    {
        if (month == 1)
        {
            month = 13;
            year--;
        }
        if (month == 2)
        {
            month = 14;
            year--;
        }
        int q = day;
        int m = month;
        int k = year % 100;
        int j = year / 100;
        int h = q + 13*(m+1)/5 + k + k/4 + j/4 + 5*j;
        h = h % 7;
        switch (h)
        {
            case 0 : return "Sat.";
            case 1 : return "Sun.";
            case 2 : return "Mon.";
            case 3 : return "Tue.";
            case 4 : return "Wed.";
            case 5 : return "Thu.";
            case 6 : return "Fri.";
        }
        return "err";
    }
}
