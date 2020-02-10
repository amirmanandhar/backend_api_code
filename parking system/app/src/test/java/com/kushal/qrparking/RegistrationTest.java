package com.kushal.qrparking;

import com.kushal.qrparking.api.UsersAPI;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import org.junit.Test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class RegistrationTest {

    @Test
    public void testRegistration(){

        UsersAPI usersAPI= APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<ResponseBody> registrationTestCall = usersAPI.registerUser("kushal123","kushal123","test@email.com","9846055785");

        try{
            registrationTestCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ResponseBody responseBody = response.body();
                    assertTrue(response.isSuccessful());
                    assertEquals("success",responseBody.getStatus());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
