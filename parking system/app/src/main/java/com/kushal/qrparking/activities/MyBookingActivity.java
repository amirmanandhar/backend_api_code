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
import com.kushal.qrparking.api.BookingAPI;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.controllers.BookingController;
import com.kushal.qrparking.retrofit.APIUrl;
import com.kushal.qrparking.retrofit.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingActivity extends AppCompatActivity {

    private ListView myBookingsListView;
    private Button btnBack;
    private ArrayList<String> myBookingList = new ArrayList();
    private TextView noBookingInfo;

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
        String selectedListItem = myBookingList.get(index);
        MyBookingResponse booking = bookingListMap.get(selectedListItem);
        currentContextMenuListViewItem = index;
        if ( item.getTitle().toString().toLowerCase().equals("delete") ){
            deleteBooking(booking.getBookingId());
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        myBookingsListView = findViewById(R.id.myBookingsListView);
        btnBack = findViewById(R.id.btnBackMyBooking);
        noBookingInfo = findViewById(R.id.noBookingInfo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.openActivity(MyBookingActivity.this,DashboardActivity.class,new ArrayList());
            }
        });

        myBookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clickedValue = (String) parent.getItemAtPosition(position);
                MyBookingResponse clickedBooking = bookingListMap.get(clickedValue);

                ArrayList<Integer> params = new ArrayList();
                params.add(clickedBooking.getBookingId());
                Global.openActivity(MyBookingActivity.this,DashboardActivity.class,params);
            }
        });

        getMyBooking();

        registerForContextMenu(myBookingsListView);
    }

    private Date convertDate(String theDateTime){
        TimeZone tz = TimeZone.getTimeZone("Asia/Kathmandu");
        Calendar cal = Calendar.getInstance(tz);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setCalendar(cal);
        try {
            cal.setTime(sdf.parse(theDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date = cal.getTime();
        return date;
    }

    ArrayAdapter<String> listViewAdapter;
    HashMap<String,MyBookingResponse> bookingListMap = new HashMap<>();
    private void getMyBooking(){
        BookingAPI bookingAPI = APIUrl.getRetrofitInstance().create(BookingAPI.class);
        Call<List<MyBookingActivity.MyBookingResponse>> myBookingsCall = bookingAPI.getMyBookings(Auth.loggedInUser.getId(),Auth.token);
        myBookingsCall.enqueue(new Callback<List<MyBookingResponse>>() {
            @Override
            public void onResponse(Call<List<MyBookingResponse>> call, Response<List<MyBookingResponse>> response) {
                List<MyBookingResponse> res = response.body();
                for (MyBookingResponse bookingRes : res) {
                    String bookingString = bookingRes.getTitle()+" > "+bookingRes.getVehicleName()+" > "+convertDate(bookingRes.getDatetime());
                    myBookingList.add(bookingString);
                    bookingListMap.put(bookingString,bookingRes);
                }
                listViewAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, myBookingList);
                myBookingsListView.setAdapter(listViewAdapter);
                if (res.size() == 0){
                    noBookingInfo.setVisibility(View.VISIBLE);
                }else{
                    noBookingInfo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<MyBookingResponse>> call, Throwable t) {

            }
        });
    }

    private void deleteBooking(int id){
        BookingAPI bookingAPI = APIUrl.getRetrofitInstance().create(BookingAPI.class);
        Call<ResponseBody> delCall = bookingAPI.delBooking(id,Auth.token);

        delCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody res = response.body();
                if (res.getStatus().equals("success")){
                    myBookingList.remove(currentContextMenuListViewItem);
                    listViewAdapter.notifyDataSetChanged();
                }
                Global.alert(getApplicationContext(),res.getMessage());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public static class MyBookingResponse{
        int id;
        String vehicleName,title,description,datetime,verificationId;

        public int getBookingId() {
            return id;
        }

        public void setBookingId(int bookingId) {
            this.id = bookingId;
        }

        public String getVehicleName() {
            return vehicleName;
        }

        public void setVehicleName(String vehicleName) {
            this.vehicleName = vehicleName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getVerificationId() {
            return verificationId;
        }

        public void setVerificationId(String verificationId) {
            this.verificationId = verificationId;
        }
    }
}
