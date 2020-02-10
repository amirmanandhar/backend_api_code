package com.kushal.qrparking.controllers;

import android.util.Log;

import com.kushal.qrparking.api.VehicleAPI;
import com.kushal.qrparking.models.Vehicle;
import com.kushal.qrparking.retrofit.APIUrl;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleController {

    public interface OnVehiclesLoad{
        void onVehiclesLoad(List<Vehicle> vehicles);
    }

    public static void getMyVehicles(final OnVehiclesLoad onVehiclesLoad){
        VehicleAPI vehicleAPI = APIUrl.getRetrofitInstance().create(VehicleAPI.class);
        Call<List<Vehicle>> vehicleGetCall = vehicleAPI.getMyVehicle(
                Auth.loggedInUser.getId(),
                Auth.token
        );
        vehicleGetCall.enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                if (response.isSuccessful()){
                    onVehiclesLoad.onVehiclesLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                Log.d("response",t.getLocalizedMessage());
            }
        });
    }

}
