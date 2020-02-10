package com.kushal.qrparking.api;

import com.kushal.qrparking.models.Vehicle;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VehicleAPI {
    @FormUrlEncoded
    @POST("api/v1/vehicle/auth/")
    Call<ResponseBody> addVehicle(
            @Field("name") String name,
            @Field("brand") String brand,
            @Field("type") String type,
            @Field("vehicleNo") String vehicleNo,
            @Field("userId") int userId,
            @Header("token") String token
    );

    @DELETE("api/v1/vehicle/auth/{id}")
    Call<ResponseBody> deleteVehicle(
            @Path("id") int vehicelId,
            @Header("token") String token
    );

    @GET("api/v1/vehicle/auth/{userId}")
    Call<List<Vehicle>> getMyVehicle(
            @Path("userId") int userId,
            @Header("token") String token
    );


}
