package com.kushal.qrparking.controllers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.api.ParkingAPI;
import com.kushal.qrparking.models.Parking;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingController {


    public static HashMap getAddressFromLatLang(Context context, double latitude, double longitude){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude , 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String,String> addressInfo = new HashMap<>();

        String address = "",city = "",state="",country="",postalCode="",knownName="",locality="";
        if ( !addresses.equals(null)  ){
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            locality = addresses.get(0).getSubLocality();
        }

        addressInfo.put("address",address);
        addressInfo.put("city",city);
        addressInfo.put("state",state);
        addressInfo.put("country",country);
        addressInfo.put("postalCode",postalCode);
        addressInfo.put("knownName",knownName);
        addressInfo.put("locality",locality);

        return addressInfo;
    }
}
