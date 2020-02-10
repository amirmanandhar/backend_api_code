package com.kushal.qrparking.controllers;

import android.util.Log;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.activities.fragments.ViewQrFragment;
import com.kushal.qrparking.api.BookingAPI;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingController {

    public static class BookingBody{

        private String status;
        private String message;
        private int bookingId;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getBookingId() {
            return bookingId;
        }

        public void setBookingId(int bookingId) {
            this.bookingId = bookingId;
        }
    }

    public interface BookingCallBack{
        void bookingCallBack(BookingBody responseBody);
    }

    public static void bookParking(int userId, int vehicleId, int parkingSpotId, String datetime, final BookingCallBack bookingCallBack){
        BookingAPI bookingAPI = APIUrl.getRetrofitInstance().create(BookingAPI.class);
        Call<BookingBody> bookParkingCall = bookingAPI.bookParking(userId,vehicleId,parkingSpotId,datetime,Auth.token);

        bookParkingCall.enqueue(new Callback<BookingBody>() {
            @Override
            public void onResponse(Call<BookingBody> call, Response<BookingBody> response) {
                if (response.isSuccessful()){
                    BookingBody responseBody = response.body();
                    bookingCallBack.bookingCallBack(responseBody);
                }
            }

            @Override
            public void onFailure(Call<BookingBody> call, Throwable t) {
                Log.d("Failure",t.getLocalizedMessage());
            }
        });

    }

    public static interface qrCallBack{
        void cb(String imageTag);
    }

    public static void getQrForBooking(int bookingId, final BookingController.qrCallBack qrCallBack){
        BookingAPI bookingAPI = APIUrl.getRetrofitInstance().create(BookingAPI.class);
        Call<String> qrGetCall = bookingAPI.getQr(bookingId,Auth.token);

        qrGetCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String responseImageTag = response.body();
                    qrCallBack.cb(responseImageTag);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Failure",t.getLocalizedMessage());
            }
        });

    }

    public static int viewQrBookingId = 0;

}
