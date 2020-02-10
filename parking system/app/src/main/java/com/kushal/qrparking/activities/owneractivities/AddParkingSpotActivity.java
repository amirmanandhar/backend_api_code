package com.kushal.qrparking.activities.owneractivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.activities.fragments.SearchParkingSpotsFragment;
import com.kushal.qrparking.api.ParkingAPI;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddParkingSpotActivity extends AppCompatActivity implements Auth.CheckUser, GoogleMap.OnMarkerClickListener {

    SupportMapFragment mapFragment;
    private GoogleMap map;
    private Button btnAddSpot,btnGoBack;
    private EditText addSpotTitle,addSpotDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_spot);

        //first check user's type
        checkUser();

        btnAddSpot = findViewById(R.id.btnAddSpot);
        btnAddSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSpot();
            }
        });
        addSpotTitle = findViewById(R.id.addSpotTitle);
        addSpotDescription = findViewById(R.id.addSpotDescription);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(AddParkingSpotActivity.this,DashboardActivity.class,new ArrayList());
            }
        });

        initMap();
    }

    private void initMap(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.addSpotMapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                mMap.clear(); //clear old markers
                mMap.getUiSettings().setZoomControlsEnabled(true);

                mMap.setOnMarkerClickListener(AddParkingSpotActivity.this);
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        addMarker(latLng.latitude,latLng.longitude);
                    }
                });
                map = mMap;

            }
        });

    }

    int totalMarkersAdded = 0;
    Marker currentMarker = null;
    private void addMarker(double currentLat,double currentLng){
        if(totalMarkersAdded == 1) {
            map.clear();
            totalMarkersAdded = 0;
        }
        currentMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat,currentLng))
                .title(addSpotTitle.getText().toString())
                .snippet(addSpotDescription.getText().toString())
        );
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 14.0f));
        totalMarkersAdded++;

    }

    private void addSpot(){
        String title = addSpotTitle.getText().toString();
        String description = addSpotDescription.getText().toString();

        if ( title.length() > 0 && description.length() > 0 ){
            if ( totalMarkersAdded == 1 && currentMarker != null ){
                String lon = String.valueOf(currentMarker.getPosition().longitude);
                String lat = String.valueOf(currentMarker.getPosition().latitude);
                ParkingAPI parkingAPI = APIUrl.getRetrofitInstance().create(ParkingAPI.class);
                Call<ResponseBody> addParkingCall  = parkingAPI.addParkingSpot(title,description,lon,lat,Auth.loggedInUser.getId(),Auth.token);

                addParkingCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody res = response.body();
                        if (res.getStatus().equals("success")){
                            map.clear();
                            totalMarkersAdded = 0;
                            addSpotDescription.setText("");
                            addSpotTitle.setText("");
                            Global.alert(getApplicationContext(),res.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Global.alert(getApplicationContext(),t.getLocalizedMessage());
                    }
                });

            }else{
                Global.alert(getApplicationContext(),"Please add marker on map for parking spot");
            }
        }else{
            Global.alert(getApplicationContext(),"Please enter title and description for spot");
        }
    }

    @Override
    public void checkUser() {
        //only allow user with type owner to access this activity
        Auth.unAllow("owner",AddParkingSpotActivity.this, DashboardActivity.class);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
