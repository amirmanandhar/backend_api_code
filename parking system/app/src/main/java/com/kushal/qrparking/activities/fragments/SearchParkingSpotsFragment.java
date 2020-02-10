package com.kushal.qrparking.activities.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.api.ParkingAPI;
import com.kushal.qrparking.controllers.ParkingController;
import com.kushal.qrparking.models.Parking;
import com.kushal.qrparking.retrofit.APIUrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchParkingSpotsFragment extends Fragment implements GoogleMap.OnMarkerClickListener{

    SupportMapFragment mapFragment;
    private List<Parking> allParking;
    private GoogleMap map;
    private Button btnSearch;
    private TextView msg;
    private AutoCompleteTextView autoCompleteSearchKeyword;
    private static boolean isMapReady = false;
    private static int markerInfoMessageShown = 0;

    private List<String> allCountriesWhereParkingSpotsAreAvailable = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_parkign_spots, container, false);

        btnSearch = view.findViewById(R.id.btnSearchMap);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAndShow();
            }
        });
        msg = view.findViewById(R.id.messageView);
        autoCompleteSearchKeyword = view.findViewById(R.id.autoCompleteSearchKeyword);

        //initialize
        allParking = new ArrayList<>();

        //get all parking on start so that searching becomes faster
        //gets all parking from api and stores in allParking
        getAllParking();

        //start
        start();

        return view;
    }

    private void start(){
        timeOutReady();
    }

    private void timeOutReady(){
        autoCompleteSearchKeyword.setEnabled(false);
        btnSearch.setEnabled(false);
        autoCompleteSearchKeyword.setText("Making everything ready,please wait...");
        Global.alert(getContext(),"Making everything ready,please wait...");
        Global.setTimeOut(5000, new Global.callbackRun() {
            @Override
            public void toRun() {
                readyAdresses();
                autoCompleteSearchKeyword.setText("");
                autoCompleteSearchKeyword.setHint("City,country,parking place information...");
                Global.alert(getContext(),"Everything is ready!");
                autoCompleteSearchKeyword.setEnabled(true);
                btnSearch.setEnabled(true);

                if ( allParking != null ){
                    initMap();
                }else{
                    Global.alert(getContext(),"No parking found!");
                }
            }
        });
    }

    private void readyAdresses(){
        for (Parking parking:allParking){
            double currentLat = Double.parseDouble(parking.getLatitude());
            double currentLng = Double.parseDouble(parking.getLongitude());
            HashMap addressOfCurrentParkingSpot = (HashMap) ParkingController.getAddressFromLatLang(getContext(),currentLat,currentLng);
//            String knownName = addressOfCurrentParkingSpot.get("knownName").toString();
//            String address = addressOfCurrentParkingSpot.get("address").toString();
            String locality = "",country="",city="";
//            locality = addressOfCurrentParkingSpot.get("locality").toString();
            city = addressOfCurrentParkingSpot.get("city").toString();
            country = addressOfCurrentParkingSpot.get("country").toString();

            String[] allCountryArray = allCountriesWhereParkingSpotsAreAvailable.toArray(new String[allCountriesWhereParkingSpotsAreAvailable.size()]);

            if(!doesArrayContainThisValue(allCountryArray,country)) allCountriesWhereParkingSpotsAreAvailable.add(country);
            if(!doesArrayContainThisValue(allCountryArray,city)) allCountriesWhereParkingSpotsAreAvailable.add(city);
//            allCountriesWhereParkingSpotsAreAvailable.add(knownName);
//            allCountriesWhereParkingSpotsAreAvailable.add(address);
            if(!doesArrayContainThisValue(allCountryArray,locality)) allCountriesWhereParkingSpotsAreAvailable.add(locality);
            if(!doesArrayContainThisValue(allCountryArray,parking.getTitle())) allCountriesWhereParkingSpotsAreAvailable.add(parking.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, allCountriesWhereParkingSpotsAreAvailable);
        autoCompleteSearchKeyword.setAdapter(adapter);
    }

    public static boolean doesArrayContainThisValue(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initMap(){
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                mMap.clear(); //clear old markers
                mMap.getUiSettings().setZoomControlsEnabled(true);

                mMap.setOnMarkerClickListener(SearchParkingSpotsFragment.this);
                map = mMap;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        navigateToBooking(marker);
                    }
                });
//                mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(37.4219999, -122.0862462))
//                        .title("Our Office")
//                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.logo)));

            }
        });

    }
    private void searchAndShow(){
        String searchKeyword = autoCompleteSearchKeyword.getText().toString().toLowerCase();
        map.clear();
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(0,0))
                .zoom(0)
                .bearing(0)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        if( searchKeyword.length() > 0 ){
            for (Parking parking: allParking){
                double currentLat = Double.parseDouble(parking.getLatitude());
                double currentLng = Double.parseDouble(parking.getLongitude());

                HashMap addressOfCurrentParkingSpot = (HashMap) ParkingController.getAddressFromLatLang(getContext(),currentLat,currentLng);
                String city = addressOfCurrentParkingSpot.get("city").toString().toLowerCase();
                String knownName = addressOfCurrentParkingSpot.get("knownName").toString().toLowerCase();
                String country = addressOfCurrentParkingSpot.get("country").toString().toLowerCase();
                String address = addressOfCurrentParkingSpot.get("address").toString().toLowerCase();
//                String locality = addressOfCurrentParkingSpot.get("locality").toString().toLowerCase();
                if ( searchKeyword.contains(city) ) addNewMarker(parking,currentLat,currentLng,parking);
//                if ( searchKeyword.contains(knownName) ) addNewMarker(parking,currentLat,currentLng,parking);
                if ( searchKeyword.contains(country) ) addNewMarker(parking,currentLat,currentLng,parking);
//                if ( searchKeyword.contains(address) ) addNewMarker(parking,currentLat,currentLng,parking);
//                if ( locality.indexOf(searchKeyword) != -1 ) addNewMarker(parking,currentLat,currentLng,parking);

                if ( doesStringHasWanted(parking.getTitle(),searchKeyword) ) addNewMarker(parking,currentLat,currentLng,parking);
                if ( doesStringHasWanted(parking.getDescription(),searchKeyword) ) addNewMarker(parking,currentLat,currentLng,parking);
            }
            if ( totalMarkersAdded == 0 ){
                Snackbar.make(getView(), "No parking spots found around ", Snackbar.LENGTH_LONG).show();
            }
        }else{
            Snackbar.make(getView(), "Please enter search keyword ", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean doesStringHasWanted(String source,String wantedStr){
        return Pattern.compile(Pattern.quote(wantedStr), Pattern.CASE_INSENSITIVE).matcher(source).find();
    }

    private int totalMarkersAdded = 0;
    private void addNewMarker(Parking parking,double currentLat,double currentLng,Parking parkingSpot){
        Marker curMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat,currentLng))
                .title(parking.getTitle())
                .snippet(parking.getDescription())
        );
        curMarker.setTag(parking);
//        CameraPosition position = CameraPosition.builder()
//                .target(new LatLng(currentLat,currentLng))
//                .zoom(10)
//                .bearing(0)
//                .build();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 14.0f));
        totalMarkersAdded++;
    }

    public void getAllParking(){
        ParkingAPI parkingAPI= APIUrl.getRetrofitInstance().create(ParkingAPI.class);
        final Call<List<Parking>> allParkingCall = parkingAPI.getAllParkingSpots();
        allParkingCall.enqueue(new Callback<List<Parking>>() {
            @Override
            public void onResponse(Call<List<Parking>> call, Response<List<Parking>> response) {
                if ( response.isSuccessful() ){
                    allParking = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Parking>> call, Throwable t) {
                Global.showNotification(getContext(),"Search Failure","An error occured while searching parking spots",true);
                Log.d("error",t.getMessage());
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(markerInfoMessageShown < 4 ){
            Global.alert(getContext(),"Click on marker info popup to book the corresponding spot");
            markerInfoMessageShown++;
        }
        marker.showInfoWindow();
        return true;
    }

    private void navigateToBooking(Marker marker){
        Parking parking = (Parking) marker.getTag();
        BookParkingFragment.parkingToBookId = parking.getId();
        DashboardActivity.chosenItemsPage.setCurrentItem(2);
        DashboardActivity.myActivity.setTitle("Book Parking");
        DashboardActivity.navigationView.setCheckedItem(R.id.nav_book_parking);
    }
}
