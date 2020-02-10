package com.kushal.qrparking.api;

import com.kushal.qrparking.models.Parking;
import com.kushal.qrparking.retrofit.ResponseBody;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ParkingAPI {

    @FormUrlEncoded
    @POST("api/v1/parking/spots/")
    Call<ResponseBody> addParkingSpot(
            @Field("title") String title,
            @Field("description") String description,
            @Field("longitude") String lon,
            @Field("latitude") String lat,
            @Field("ownerId") int ownerId,
            @Header("token") String token
    );

    @GET("api/v1/parking/spots/{ownerId}")
    Call<List<Parking>> getParkingSpots(
            @Path("ownerId") int ownerId
    );

    @GET("api/v1/parking/spots/")
    Call<List<Parking>> getAllParkingSpots();
}
