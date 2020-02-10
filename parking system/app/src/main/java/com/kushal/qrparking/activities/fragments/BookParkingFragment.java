package com.kushal.qrparking.activities.fragments;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.adapters.FragmentAdapter;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.controllers.BookingController;
import com.kushal.qrparking.controllers.VehicleController;
import com.kushal.qrparking.models.Vehicle;
import com.kushal.qrparking.retrofit.ResponseBody;
import com.kushal.qrparking.sensors.Sensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookParkingFragment extends Fragment implements VehicleController.OnVehiclesLoad, BookingController.BookingCallBack, SensorEventListener {

    public static int parkingToBookId = 0;
    private TextView nooSpotIdMsg,chosenParkingSpotIdForBookingView,labelChooseVehicle,bookingDateTimeView;
    private Spinner myVehiclesForBooking;
    private DatePicker bookingDate;
    private TimePicker bookingTime;
    private Button btnBookParking,btnCancelBooking;
    private Sensors sensors;

    public BookParkingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_parking, container, false);

        nooSpotIdMsg = view.findViewById(R.id.nooSpotIdMsg);
        myVehiclesForBooking = view.findViewById(R.id.myVehiclesForBooking);
        btnBookParking = view.findViewById(R.id.btnBookParking);
        btnCancelBooking = view.findViewById(R.id.btnCancelBooking);
        bookingDateTimeView = view.findViewById(R.id.bookingDateTimeView);
        bookingDate = view.findViewById(R.id.bookingDate);
        bookingTime = view.findViewById(R.id.bookingTime);
        labelChooseVehicle = view.findViewById(R.id.labelChooseVehicle);
        chosenParkingSpotIdForBookingView = view.findViewById(R.id.chosenParkingSpotIdForBookingView);

        handleIfZero();

        btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardActivity.chosenItemsPage.setCurrentItem(4);
                DashboardActivity.myActivity.setTitle("Search Parking Spot");
                DashboardActivity.navigationView.setCheckedItem(R.id.nav_search_parking_spots);
            }
        });

        //proximity for booking parking spot
//        sensors = new Sensors(getContext(), Sensor.TYPE_PROXIMITY,this);

        return view;
    }

    private void handleIfZero() {
        if ( parkingToBookId == 0 ){
            nooSpotIdMsg.setVisibility(View.VISIBLE);
            myVehiclesForBooking.setVisibility(View.GONE);
            bookingDateTimeView.setVisibility(View.GONE);
            bookingDate.setVisibility(View.GONE);
            bookingTime.setVisibility(View.GONE);
            btnCancelBooking.setVisibility(View.GONE);
            chosenParkingSpotIdForBookingView.setVisibility(View.GONE);
            labelChooseVehicle.setVisibility(View.GONE);
            btnBookParking.setVisibility(View.GONE);
        }else{
            VehicleController.getMyVehicles(this);
            btnBookParking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookSpot();
                }
            });
            chosenParkingSpotIdForBookingView.setText("Chosen parking spot : #"+parkingToBookId);
            nooSpotIdMsg.setVisibility(View.GONE);

        }
    }

    HashMap<String,Integer> allMyVehicles = new HashMap<>();
    List<String> myVehicleNames = new ArrayList<>();
    @Override
    public void onVehiclesLoad(List<Vehicle> vehicles) {

        for (Vehicle vehicle : vehicles) {
            allMyVehicles.put(vehicle.getName(),vehicle.getId());
            myVehicleNames.add(vehicle.getName());
        }

        ArrayAdapter<String> myVehiclesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, myVehicleNames);
        myVehiclesForBooking.setAdapter(myVehiclesAdapter);
        myVehiclesForBooking.setSelection(0);
    }

    private void bookSpot(){

        if( allMyVehicles.size() == 0 ){
            Global.alert(getActivity(),"No vehicles found for booking. Please add a vehicle to start booking.");
            return;
        }

        String chosenDate = bookingDate.getYear()+"-"+bookingDate.getMonth()+"-"+bookingDate.getDayOfMonth();

        int hour, minute;
        String am_pm;
        if (Build.VERSION.SDK_INT >= 23 ){
            hour = bookingTime.getHour();
            minute = bookingTime.getMinute();
        }
        else{
            hour = bookingTime.getCurrentHour();
            minute = bookingTime.getCurrentMinute();
        }
        if(hour > 12) {
            am_pm = "PM";
            hour = hour - 12;
        }
        else
        {
            am_pm="AM";
        }
        String chosenTime = hour +":"+ minute+" "+am_pm;

        String chosenDateTime = chosenDate+" "+chosenTime;
        int chosenVehicleId = allMyVehicles.get(myVehiclesForBooking.getSelectedItem().toString());
        BookingController.bookParking(Auth.loggedInUser.getId(),chosenVehicleId,parkingToBookId,chosenDateTime,this);
    }

    @Override
    public void bookingCallBack(BookingController.BookingBody responseBody) {
        Global.alert(getActivity(),responseBody.getMessage());
        if (responseBody.getStatus().equals("success")){
            //redirect to my bookings
            BookingController.viewQrBookingId = responseBody.getBookingId();
            DashboardActivity.openFragment("Qr Code",R.id.nav_view_qr,3);
            parkingToBookId = 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Auth.isLoggedIn()){
            if ( event.values[0] < 2 ){
                // book if object is near to proximity sensor
                bookSpot();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
