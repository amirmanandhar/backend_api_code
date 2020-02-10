package com.kushal.qrparking.activities.fragments;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.api.VehicleAPI;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;
import com.kushal.qrparking.sensors.Sensors;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleFragment extends Fragment implements SensorEventListener {

    private EditText vehicleNoTxt,vehicleNameTxt;
    private AutoCompleteTextView vehicleBrandView;
    private Spinner vehicleTypeSpinner;
    private ArrayList<String> brands = new ArrayList<>();
    private Button btnAdd;
    private View thisView;

    private Sensors sensors;

    public AddVehicleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_vehicle, container, false);
        thisView = view;

        vehicleNoTxt = view.findViewById(R.id.vehicleNo);
        vehicleNameTxt = view.findViewById(R.id.vehiclName);
        vehicleBrandView = view.findViewById(R.id.vehicleBrand);
        vehicleTypeSpinner = view.findViewById(R.id.vehicleType);
        btnAdd = view.findViewById(R.id.btnAddVehicle);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addVehicle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //gyroscopr sensor is used for adding vehicle
        sensors = new Sensors(getContext(), Sensor.TYPE_GYROSCOPE,this);

        setupAutoCompleteFields(view);

        return view;
    }

    private void setupAutoCompleteFields(View view){

        brands.add("Bentley");
        brands.add("Bugatti");
        brands.add("Volvo");
        brands.add("Suzuki");
        brands.add("Tesla");
        brands.add("Nissan");
        brands.add("Toyota");
        brands.add("Yamaha");
        brands.add("Ford");
        brands.add("Mitshubishi");
        brands.add("Mercedez-Benz");
        brands.add("Jeep");
        brands.add("Jaguar");
        brands.add("Rolls-Royce");
        brands.add("Porsche");
        brands.add("Honda");
        brands.add("Ferrari");
        brands.add("Audi");
        brands.add("BMW");
        brands.add("Volkswagen");
        brands.add("Ducati");
        brands.add("Harley-Davidson");
        brands.add("KTM");
        brands.add("Bajaj");
        brands.add("Royal Enfield");
        brands.add("Benelli");
        brands.add("Lamborghini");

        ArrayAdapter<CharSequence> autoTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.vehicleTypes, android.R.layout.simple_spinner_item);
        autoTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> autoBrandAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_dropdown_item_1line, brands);

        vehicleBrandView.setAdapter(autoBrandAdapter);
        vehicleTypeSpinner.setAdapter(autoTypesAdapter);
        vehicleTypeSpinner.setSelection(0);
    }

    private void addVehicle() throws IOException {

        VehicleAPI vehicleAPI = APIUrl.getRetrofitInstance().create(VehicleAPI.class);
        Call<ResponseBody> vehicleAddCall = vehicleAPI.addVehicle(
                vehicleNameTxt.getText().toString(),
                vehicleBrandView.getText().toString(),
                vehicleTypeSpinner.getSelectedItem().toString(),
                vehicleNoTxt.getText().toString(),
                Auth.loggedInUser.getId(),
                Auth.token
        );

        vehicleAddCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()){
                    ResponseBody addResponse = response.body();
                    if (addResponse.getStatus().equals("success")){
                       clearFields();
                    }
                    Snackbar.make(thisView, addResponse.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(getView(),t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                Global.showNotification(getContext(),"Vehicle addition error","An error was occured while adding your vehcle",true);
            }
        });
    }

    private void clearFields(){
        vehicleNameTxt.setText("");
        vehicleNoTxt.setText("");
        vehicleBrandView.setText("");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Auth.isLoggedIn()){
            if (event.values[1] < 0 ){
                //  tilted left
                try {
                    addVehicle();
//                        Global.showNotification(getContext(),"Tilted","Left",true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if( event.values[1] > 0 ){
                //tilted right
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
