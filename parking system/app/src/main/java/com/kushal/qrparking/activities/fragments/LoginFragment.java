package com.kushal.qrparking.activities.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.api.UsersAPI;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText loginUsername,loginPassword;
    private Button btnLogin;
    private TextView messageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        loginUsername = view.findViewById(R.id.loginUsername);
        loginPassword = view.findViewById(R.id.loginPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        messageView = view.findViewById(R.id.messageView);
        loginUsername.requestFocus();

        return  view;
    }

    private void authenticateUser(){
        String givenUsername = loginUsername.getText().toString();
        String givenPassword = loginPassword.getText().toString();

        UsersAPI usersAPI= APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<ResponseBody> authenticateUserCall = usersAPI.authenticateUser(givenUsername,givenPassword);

        authenticateUserCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if ( response.code() == 200 ){
                    ResponseBody authResponseBody = response.body();
                    if ( authResponseBody.getStatus().equals("success") ){
                        //token comes in body's message
                        Global.alert(getContext(),"Logging in...");
                        Auth.login(authResponseBody.getMessage());
                        Global.openActivity(getActivity(), DashboardActivity.class,new ArrayList());
                    }else{
                        Global.showMessage(messageView,authResponseBody.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d("Login response error",t.getLocalizedMessage());
                if( t.getLocalizedMessage().contains("timed out") ){
//                    Global.showMessage(messageView,"Something went wrong, please check if the server is running or not!");
                    Global.showNotification(getContext(),"QRParking Login","Something went wrong, please check if the server is running or not!",true);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        authenticateUser();
    }

}
