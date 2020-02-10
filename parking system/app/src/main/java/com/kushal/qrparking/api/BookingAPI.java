package com.kushal.qrparking.api;

import com.kushal.qrparking.activities.MyBookingActivity;
import com.kushal.qrparking.controllers.BookingController;
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

public interface BookingAPI {

    @FormUrlEncoded
    @POST("api/v1/booking/auth/")
    Call<BookingController.BookingBody> bookParking(
            @Field("userId") int userId,
            @Field("vehicleId") int vehicleId,
            @Field("parkingSpotId") int parkingSpotId,
            @Field("datetime") String datetime,
            @Header("token") String token
    );

    @DELETE("api/v1/booking/auth/{id}")
    Call<ResponseBody> delBooking(
            @Path("id") int bookingId,
            @Header("token") String token
    );

    @GET("api/v1/booking/auth/{userId}")
    Call<List<MyBookingActivity.MyBookingResponse>> getMyBookings(
            @Path("userId") int userId,
            @Header("token") String token
    );

    @GET("api/v1/booking/auth/viewQr/{bookingId}")
    Call<String> getQr(
            @Path("bookingId") int bookingId,
            @Header("token") String token
    );

}