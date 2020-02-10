package com.kushal.qrparking.activities.adminactivities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class UpdateUserActivity extends AppCompatActivity implements Auth.CheckUser{

    private EditText userId;
    private TextView msgView;
    private Spinner userType;
    private Button btnUpdate,btnCancelUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        //first check user's type
        checkUser();

        userId = findViewById(R.id.updatesUserId);
        userType = findViewById(R.id.updatesUserType);
        msgView = findViewById(R.id.messageViewUpdate);
        btnCancelUpdate = findViewById(R.id.btnCancelUpdate);
        btnCancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(UpdateUserActivity.this,DashboardActivity.class,new ArrayList());
            }
        });
        btnUpdate = findViewById(R.id.btnUpdateUserType);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsersType();
            }
        });
        ArrayAdapter<CharSequence> userTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.userTypes, android.R.layout.simple_spinner_item);
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(userTypeAdapter);
        userType.setSelection(0);
    }

    private void updateUsersType() {

        UsersAPI usersAPI = APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<ResponseBody> updateCall = usersAPI.updateUser("","","","",userType.getSelectedItem().toString(),userId.getText().toString(),Auth.token);

        updateCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if ( response.isSuccessful() ){
                    ResponseBody responseBody = response.body();
                    if ( responseBody.getStatus().equals("success") ){
                        Global.showMessage(msgView,responseBody.getMessage()+" updated!");
                    }else{
                        Global.showMessage(msgView,responseBody.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Global.alert(getApplicationContext(),t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void checkUser() {
        //only allow user with type admin to access this activity
        Auth.unAllow("admin",UpdateUserActivity.this, DashboardActivity.class);
    }
}
