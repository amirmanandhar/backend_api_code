package com.kushal.qrparking;

import android.util.Log;

import com.kushal.qrparking.api.UsersAPI;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

public class LoginTest {

    @Test
    public void loginTest(){
        UsersAPI usersAPI = APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<ResponseBody> loginTestCall = usersAPI.authenticateUser("kushal","kushal123");

        loginTestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                assertTrue(response.isSuccessful());
                assertEquals("success",responseBody.getStatus());
                String token = responseBody.getMessage();
                assertThat(token,is(IsNull.notNullValue()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });

    }

    @Test
    public void loginEmptyValuesTest(){
        UsersAPI usersAPI = APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<ResponseBody> loginTestCall = usersAPI.authenticateUser("","");

        loginTestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                assertTrue(response.isSuccessful());
                assertEquals("fail",responseBody.getStatus());
                assertThat(responseBody.getMessage(),is(IsNull.notNullValue()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });

    }

}
