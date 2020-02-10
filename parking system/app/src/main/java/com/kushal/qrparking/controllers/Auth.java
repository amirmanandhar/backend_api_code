package com.kushal.qrparking.controllers;

import android.app.Activity;
import com.kushal.qrparking.Global;
import com.kushal.qrparking.activities.DashboardActivity;
import com.kushal.qrparking.models.User;
import java.util.ArrayList;

public class Auth {

    public static String token = null;
    public static boolean loggedIn = false;
    public static User loggedInUser = null;

    //token is available if user is authenticated
    //else token is null
    public static boolean isAuthenticated(){
        if ( token.equals(null) ) return false;
        return true;
    }

    //logout
    public static void logoout(){
        token = null;
        loggedIn = false;
        loggedInUser = null;
        DashboardActivity.loggedInMessageShown = false;
    }

    //called on user is authenticated from server
    public static void login(String token){
        Auth.token = token;
        loggedIn = true;
    }

    public static boolean isLoggedIn(){
        return loggedIn;
    }

    //to check users type on logged in
    public interface CheckUser{
        void checkUser();
    }
    //for allowing certain user types to access certain activities
    public static void unAllow(String allowedUser,Activity currentActivity,Class toOpenIfNotAllowed){
        if ( !Auth.loggedInUser.getType().equals(allowedUser) ){
            Global.openActivity(currentActivity,toOpenIfNotAllowed,new ArrayList());
        }
    }

}
