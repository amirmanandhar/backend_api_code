package com.kushal.qrparking.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.api.UsersAPI;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccountActivity extends AppCompatActivity {

    private TextView msgView;
    private EditText updateTo;
    private Spinner toUpdate;
    private Button btnEdit,btnDash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        updateTo = findViewById(R.id.updateTo);
        toUpdate = findViewById(R.id.toUpdate);
        btnEdit = findViewById(R.id.btnEdit);
        btnDash = findViewById(R.id.btnDash);
        msgView = findViewById(R.id.messageView);

        ArrayAdapter<CharSequence> userTableAdapter = ArrayAdapter.createFromResource(this,
                R.array.userTableValues, android.R.layout.simple_spinner_item);
        userTableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toUpdate.setAdapter(userTableAdapter);
        toUpdate.setSelection(0);

        toUpdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String toUpdateChosen = toUpdate.getSelectedItem().toString();
                if (toUpdateChosen.equals("username")){
                    Global.alert(getApplicationContext(),"You have to login again on updating username");
                }
                if (toUpdateChosen.equals("password")) {
                    updateTo.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Global.alert(getApplicationContext(),"You have to login again on updating password");
                }else{
                    updateTo.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(EditAccountActivity.this,DashboardActivity.class,new ArrayList());
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });

    }

    private void edit(){
        if( updateTo.getText().length() < 1 ){
            Global.showMessage(msgView,"Please enter update value!");
            return;
        }
        String username = "",
                password = "",
                email = "",
                phone = "";

        final String toUpdateChosen = toUpdate.getSelectedItem().toString();

        if (toUpdateChosen.equals("username")) username = updateTo.getText().toString();
        if (toUpdateChosen.equals("password")) password = updateTo.getText().toString();
        if (toUpdateChosen.equals("email")) email = updateTo.getText().toString();
        if (toUpdateChosen.equals("phone")) phone = updateTo.getText().toString();

        UsersAPI usersAPI = APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<ResponseBody> updateCall = usersAPI.updateUser(username,password,email,phone,"",String.valueOf(Auth.loggedInUser.getId()), Auth.token);

        updateCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if ( response.isSuccessful() ){
                    ResponseBody responseBody = response.body();
                    if ( responseBody.getStatus().equals("success") ){
                        if( toUpdateChosen.equals("username") || toUpdateChosen.equals("password") ){
                            Global.alert(getApplicationContext(),"Update successful, please login again!");
                            Auth.logoout();
                            Global.openActivity(EditAccountActivity.this, LoginRegisterActivity.class,new ArrayList());
                        }else{
                            Global.showMessage(msgView,responseBody.getMessage()+" updated!");
                            updateTo.setText("");
                        }
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
}
