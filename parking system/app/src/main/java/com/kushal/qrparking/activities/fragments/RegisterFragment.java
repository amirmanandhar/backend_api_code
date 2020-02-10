package com.kushal.qrparking.activities.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.api.UsersAPI;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText regUsername,regPassword,regEmail,regPhone;
    TextView messageView;
    private Button btnRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        regUsername = view.findViewById(R.id.regUsername);
        regPassword = view.findViewById(R.id.regPassword);
        regEmail = view.findViewById(R.id.regEmail);
        regPhone = view.findViewById(R.id.regPhoneNumber);
        btnRegister = view.findViewById(R.id.btnRegister);
        messageView = view.findViewById(R.id.messageView);
        btnRegister.setOnClickListener(this);

        return view;
    }

    private void registerUser(){

        String givenUsername,givenPassword,givenEmail,givenPhone;

        givenEmail = regEmail.getText().toString();
        givenPhone = regPhone.getText().toString();
        givenUsername = regUsername.getText().toString();
        givenPassword = regPassword.getText().toString();

        UsersAPI usersAPI= APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<ResponseBody> voidCall = usersAPI.registerUser(givenUsername,givenPassword,givenEmail,givenPhone);

        voidCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                if ( !response.isSuccessful() ){
                    Global.alert(getContext(),"Code: "+response.code());
                    return;
                }
                if( responseBody.getStatus().equals("success") ){
                    Global.showMessage(messageView,"Registration successful! Login now.");
                    regEmail.setText("");
                    regPhone.setText("");
                    regUsername.setText("");
                    regPassword.setText("");
                }else{
                    if( responseBody.getStatus().equals("taken") ){
                        regUsername.requestFocus();
                    }
                    Global.showMessage(messageView,responseBody.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Global.showMessage(messageView,t.getMessage());
                if( t.getLocalizedMessage().contains("timed out") ){
//                    Global.showMessage(messageView,"Something went wrong, please check if the server is running or not!");
                    Global.showNotification(getContext(),"QRParking Login","Something went wrong, please check if the server is running or not!",true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if ( v.getId() == R.id.btnRegister ){
            if(regEmail.getText().length() > 0 && regPhone.getText().length() > 0 ){
                if ( regUsername.getText().length() >= 5 && regPassword.getText().length() >= 5 ){
                    registerUser();
                }else{
                    Global.showMessage(messageView,"Username and password must be at least 5 words long.");
                }
            }else{
                Global.showMessage(messageView,"All registration credentials required");
            }
        }

    }

}
