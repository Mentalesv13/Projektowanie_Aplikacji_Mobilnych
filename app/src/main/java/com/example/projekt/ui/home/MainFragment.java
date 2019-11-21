package com.example.projekt.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.projekt.R;

public class MainFragment extends Fragment {

        public static ViewFlipper viewFlipper;
        View view;

        int images [] = {R.drawable.les_miserables, R.drawable.les_miserables};
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
            if(checkNetworkConnection()==true){
                view = inflater.inflate(R.layout.fragment_strona_glowna, container, false);
                createHomeView();
        return view;}
            else
            {
                noInternetConnectionDialog();
                view = inflater.inflate(R.layout.fragment_blank, container, false);
                return view;
            }
    }

        @Override
        public void onResume() {
            if(checkNetworkConnection()==true) {
                viewFlipper.startFlipping();
            }
        super.onResume();
    }

    private void noInternetConnectionDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_internet_connection, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("NoInternetConnection");
        dialogBuilder.setCancelable(false);

        //final EditText mEditEmail = dialogView.findViewById(R.id.etEmailR);

        dialogBuilder.setPositiveButton("Reload",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(true);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (checkNetworkConnection()==true) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, new MainFragment());
                            ft.commit();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Check Internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        else return false;
    }

    public void createHomeView(){

        viewFlipper = view.findViewById(R.id.v_flipper);
        viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left );
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();
    }


}