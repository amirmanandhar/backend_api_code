package com.kushal.qrparking.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.fragments.AddVehicleFragment;
import com.kushal.qrparking.activities.fragments.BookParkingFragment;
import com.kushal.qrparking.activities.fragments.DashboardFragment;
import com.kushal.qrparking.activities.fragments.ViewQrFragment;
import com.kushal.qrparking.activities.fragments.SearchParkingSpotsFragment;
import com.kushal.qrparking.adapters.FragmentAdapter;
import com.kushal.qrparking.api.UsersAPI;
import com.kushal.qrparking.controllers.Auth;
import com.kushal.qrparking.controllers.BookingController;
import com.kushal.qrparking.models.User;
import com.kushal.qrparking.retrofit.APIUrl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static ViewPager chosenItemsPage;
    public static NavigationView navigationView;
    public static MenuItem selectedItem;
    private User loggedInUser;
    TextView headerUsername,headerEmail,headerPhone;
    public static TextView messageViewDashboard;
    public static Activity myActivity;
    private SharedPreferences sharedPreferences;
    public static boolean loggedInMessageShown = false;

    public DashboardActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Bundle gottenPacket = getIntent().getExtras();
        if (gottenPacket != null){
            ArrayList params = gottenPacket.getIntegerArrayList("sentPacket");
            if( params.size() > 0 ){
                int toOpenQrId = (int) params.get(0);
                BookingController.viewQrBookingId = toOpenQrId;
                Global.setTimeOut(1000, new Global.callbackRun() {
                    @Override
                    public void toRun() {
                        DashboardActivity.openFragment("Qr Code",R.id.nav_view_qr,3);
                    }
                });
            }
        }

        myActivity = this;
        sharedPreferences = this.getSharedPreferences("loggedInUser", MODE_PRIVATE );

        // if not logged in, go to login,register activity
        if (!Auth.isLoggedIn()){
            Global.openActivity(DashboardActivity.this,LoginRegisterActivity.class,new ArrayList());
        }else {
            if (!loggedInMessageShown){
                Global.alert(this, "Logged in");
                loggedInMessageShown = true;
            }
            getAndSetLoggedInUserToSharedPreference();

            messageViewDashboard = findViewById(R.id.messageViewDashboard);

            chosenItemsPage = findViewById(R.id.chosenItemsPage);
            chosenItemsPage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    private void initOther(){
        addFragmentsToDashboardContentPager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        don't need floating action button
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(0);
    }

    private void inflateHeader(){
        View header = navigationView.getHeaderView(0);
        headerUsername  = header.findViewById(R.id.loggedInUserName);
        headerEmail  = header.findViewById(R.id.loggedInUserEmail);
        headerPhone = header.findViewById(R.id.loggedInUserPhone);

        headerUsername.setText(loggedInUser.getUsername()+" ("+loggedInUser.getType().toUpperCase()+")");
        headerEmail.setText(loggedInUser.getEmail());
        headerPhone.setText(loggedInUser.getPhone());
    }

    int backCount = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backCount >= 2) super.onBackPressed();
            else Global.alert(getApplication(),"Press back again to exit");
        }
        backCount++;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addFragmentsToDashboardContentPager(){
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        fragmentAdapter.addFragment(new DashboardFragment(),"Dashboard");
        fragmentAdapter.addFragment(new AddVehicleFragment(),"Add Vehicle");
        fragmentAdapter.addFragment(new BookParkingFragment(),"Book Parking");
        fragmentAdapter.addFragment(new ViewQrFragment(),"View QR");
        fragmentAdapter.addFragment(new SearchParkingSpotsFragment(),"Search Parking Spots");
        chosenItemsPage.setAdapter(fragmentAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedItem = item;
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int currentItem = 0;

        if (id == R.id.nav_dashboard){
            currentItem = 0;
        }
        if (id == R.id.nav_vehicle) {
            currentItem = 1;
        }
        if (id == R.id.nav_book_parking) {
            currentItem = 2;
        }
        if (id == R.id.nav_view_qr) {
            currentItem = 3;
        }
        if (id == R.id.nav_search_parking_spots) {
            currentItem = 4;
        }
        if (id == R.id.nav_setting) {
            currentItem = 5;
        }
        if(id == R.id.nav_logout){
            Auth.logoout();
            Global.openActivity(DashboardActivity.this,LoginRegisterActivity.class,new ArrayList());
        }

        chosenItemsPage.setCurrentItem(currentItem);
        setTitle(item.getTitle());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getLoggedInUserDetailFromSharedPreference(){
        loggedInUser = new User();
        if ( sharedPreferences.contains("id") ){

            loggedInUser.setId(sharedPreferences.getInt("id",0));
            loggedInUser.setUsername(sharedPreferences.getString("username",null));
            loggedInUser.setEmail(sharedPreferences.getString("email",null));
            loggedInUser.setPhone(sharedPreferences.getString("phone",null));
            loggedInUser.setType(sharedPreferences.getString("type",null));


            Auth.loggedInUser = loggedInUser;
        }else{
            Global.alert(this,"Nothing here");
        }
    }

    private void getAndSetLoggedInUserToSharedPreference(){
        UsersAPI usersAPI= APIUrl.getRetrofitInstance().create(UsersAPI.class);
        Call<User> loggedUserCall = usersAPI.getLoggedInUser(Auth.token);

        loggedUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()){
                    User user = response.body();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("id", user.getId());
                    editor.putString("username", user.getUsername());
                    editor.putString("email", user.getEmail());
                    editor.putString("phone", user.getPhone());
                    editor.putString("type", user.getType());
                    editor.apply();

                    getLoggedInUserDetailFromSharedPreference();
                    initOther();
                    inflateHeader();
                }else {
                    Global.alert(getApplicationContext(),"Logged in users detail's response error.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Global.alert(getApplicationContext(),t.getLocalizedMessage());
            }
        });

    }

    public static void openFragment(String title,int checkedItem,int toSetCurrentItem){
        DashboardActivity.myActivity.setTitle(title);
        DashboardActivity.navigationView.setCheckedItem(checkedItem);
        DashboardActivity.chosenItemsPage.setCurrentItem(toSetCurrentItem);
        DashboardActivity.chosenItemsPage.getAdapter().notifyDataSetChanged();
    }
}
