package com.kushal.qrparking.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.api.VehicleAPI;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.controllers.VehicleController;
import com.kushal.qrparking.models.Vehicle;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyVehiclesActivity extends AppCompatActivity implements VehicleController.OnVehiclesLoad {

    private ListView myVehicleListView;
    private Button btnBack;
    private ArrayList<String> myVehicleList = new ArrayList();
    private TextView noVehicleInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicles);

        myVehicleListView = findViewById(R.id.myVehicleListView);
        btnBack = findViewById(R.id.btnBackMyVehicle);
        noVehicleInfo = findViewById(R.id.noVehicleInfo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(MyVehiclesActivity.this,DashboardActivity.class,new ArrayList());
            }
        });

        getMyVehicles();
        registerForContextMenu(myVehicleListView);
    }

    ArrayAdapter<String> listViewAdapter;
    HashMap<String, Vehicle> myVehiclesHashMap = new HashMap<>();
    private void getMyVehicles() {
        VehicleController.getMyVehicles(this);
    }

    @Override
    public void onVehiclesLoad(List<Vehicle> vehicles) {
        for (Vehicle vehicle : vehicles) {
            String vehicleString = vehicle.getName()+" > "+vehicle.getBrand()+" > "+vehicle.getType()+" > "+vehicle.getVehicleNo();
            myVehicleList.add(vehicleString);
            myVehiclesHashMap.put(vehicleString,vehicle);
        }
        listViewAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, myVehicleList);
        myVehicleListView.setAdapter(listViewAdapter);
        if (vehicles.size() == 0){
            noVehicleInfo.setVisibility(View.VISIBLE);
        }else{
            noVehicleInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Delete");
    }

    int currentContextMenuListViewItem = 0;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        String selectedListItem = myVehicleList.get(index);
        Vehicle vehicle = myVehiclesHashMap.get(selectedListItem);
        currentContextMenuListViewItem = index;
        if ( item.getTitle().toString().toLowerCase().equals("delete") ){
            deleteVehicle(vehicle.getId());
        }

        return true;
    }

    private void deleteVehicle(int vehicleid){
        VehicleAPI vehicleAPI = APIUrl.getRetrofitInstance().create(VehicleAPI.class);
        Call<ResponseBody> deleteVehicleCall = vehicleAPI.deleteVehicle(vehicleid, Auth.token);

        deleteVehicleCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody res = response.body();
                if (res.getStatus().equals("success")){
                    myVehicleList.remove(currentContextMenuListViewItem);
                    listViewAdapter.notifyDataSetChanged();
                }
                Global.alert(getApplicationContext(),res.getMessage());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Global.alert(getApplicationContext(),t.getLocalizedMessage());
            }
        });

    }
}
