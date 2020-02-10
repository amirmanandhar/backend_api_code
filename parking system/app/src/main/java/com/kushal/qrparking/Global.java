package com.kushal.qrparking;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Global {

    public static void openActivity(Activity currentActivity, Class toOpenActivityClass, ArrayList intentExtras){
        Intent intent = new Intent(currentActivity.getApplicationContext(),toOpenActivityClass);
        intent.putExtra("sentPacket",intentExtras);
        currentActivity.getApplicationContext().startActivity(intent);
        currentActivity.finish();
    }

    //alert
    public static void alert(Context c,String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(final TextView textView, String message){
        textView.setTranslationY(textView.getHeight());
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
        textView.animate().translationY(0);
        setTimeOut(5000, new callbackRun() {
            @Override
            public void toRun() {
                textView.animate().translationY(textView.getHeight());
                setTimeOut(100, new callbackRun() {
                    @Override
                    public void toRun() {
                        hideMessage(textView);
                    }
                });
            }
        });
    }

    public static void hideMessage(TextView textView){
        textView.setVisibility(View.INVISIBLE);
    }

    public static void setTimeOut(long milliSeconds, final callbackRun toRun){
        Runnable runnable = null;
        new android.os.Handler().removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                toRun.toRun();
            }
        };
        new android.os.Handler().postDelayed(runnable,milliSeconds);
    }

    public static interface callbackRun{
        void toRun();
    }

    public static ProgressDialog showProgress(Context context){
        // Set up progress before call
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(context);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Loggin you in");
        progressDoalog.setTitle("Please wait...");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();
        return progressDoalog;
    }

    public static void showNotification(Context context,String title,String description,boolean cancelable){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,"QRParking Notifier")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setAutoCancel(cancelable)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(description));


        // Gets an instance of the NotificationManager service//
        NotificationManager mNotificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    public static void createFileAndAddContent(Context c,String filename,String fileContents){
        FileOutputStream outputStream;
        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String openFile(Context context,String filename){
        String content = "";
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content += line;
            }
        }catch (Exception e){}
        return content;
    }

}
