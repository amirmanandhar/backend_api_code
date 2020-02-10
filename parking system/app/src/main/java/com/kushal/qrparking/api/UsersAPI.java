package com.kushal.qrparking.api;

import com.kushal.qrparking.models.User;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsersAPI {
    @GET("api/v1/users/")
    Call<List<User>> getUsers();

    @FormUrlEncoded
    @POST("api/v1/users/")
    Call<ResponseBody> registerUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("api/v1/users/auth/")
    Call<ResponseBody> authenticateUser(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("api/v1/users/auth/")
    Call<User> getLoggedInUser(
            @Header("token") String token
    );

    @FormUrlEncoded
    @PUT("api/v1/users/auth/")
    Call<ResponseBody> updateUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("type") String type,
            @Field("id") String id,
            @Header("token") String token
    );
}
