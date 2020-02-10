package com.kushal.qrparking.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.activities.fragments.LoginFragment;
import com.kushal.qrparking.activities.fragments.RegisterFragment;
import com.kushal.qrparking.adapters.FragmentAdapter;
import com.kushal.qrparking.controllers.Auth;

import java.util.ArrayList;

public class LoginRegisterActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // if already logged in, go to dashboard
        if (Auth.isLoggedIn()){
            Global.openActivity(LoginRegisterActivity.this,DashboardActivity.class,new ArrayList());
        }

        viewPager = findViewById(R.id.theViewPager);
        tabLayout = findViewById(R.id.tabLayout);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());

        fragmentAdapter.addFragment(new LoginFragment(),"Login");
        fragmentAdapter.addFragment(new RegisterFragment(),"Register");

        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
