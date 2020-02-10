package com.kushal.qrparking.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.activities.EditAccountActivity;
import com.kushal.qrparking.activities.LoginRegisterActivity;
import com.kushal.qrparking.activities.MyBookingActivity;
import com.kushal.qrparking.activities.MyVehiclesActivity;
import com.kushal.qrparking.activities.adminactivities.UpdateUserActivity;
import com.kushal.qrparking.activities.owneractivities.AddParkingSpotActivity;
import com.kushal.qrparking.controllers.Auth;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private TextView usernameView;
    private Button btnMyBookings,btnEditAccount,btnMyVehicle,btnLogout,btnUpdateUserType,btnAddParkingSpot;
    private LinearLayout adminButtons,ownerButtons;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        usernameView = view.findViewById(R.id.welcomeUsernameView);
        btnMyBookings = view.findViewById(R.id.myBookingsBtn);
        btnEditAccount = view.findViewById(R.id.editAccountBtn);
        btnMyVehicle = view.findViewById(R.id.myVehiclesBtn);
        btnAddParkingSpot = view.findViewById(R.id.adminAddParkingSpot);
        btnUpdateUserType = view.findViewById(R.id.adminUpdateUserType);
        btnUpdateUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(getActivity(),UpdateUserActivity.class,new ArrayList());
            }
        });
        btnLogout = view.findViewById(R.id.logoutBtn);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.logoout();
                Global.openActivity(getActivity(), LoginRegisterActivity.class,new ArrayList());
            }
        });

        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(getActivity(), EditAccountActivity.class,new ArrayList());
            }
        });
        btnMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(getActivity(), MyBookingActivity.class,new ArrayList());
            }
        });
        btnAddParkingSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(getActivity(), AddParkingSpotActivity.class,new ArrayList());
            }
        });
        btnMyVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(getActivity(), MyVehiclesActivity.class,new ArrayList());
            }
        });

        adminButtons = view.findViewById(R.id.adminButtons);
        ownerButtons = view.findViewById(R.id.ownerButtons);
        if ( Auth.loggedInUser.getType().equals("owner") ) ownerButtons.setVisibility(View.VISIBLE);
        if ( Auth.loggedInUser.getType().equals("admin") ) {
            adminButtons.setVisibility(View.VISIBLE);
            btnMyBookings.setVisibility(View.GONE);
            btnMyVehicle.setVisibility(View.GONE);
        }

        usernameView.setText("Welcome, "+Auth.loggedInUser.getUsername());

        return view;
    }

}
