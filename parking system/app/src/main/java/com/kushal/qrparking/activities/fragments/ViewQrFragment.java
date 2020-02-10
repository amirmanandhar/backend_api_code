package com.kushal.qrparking.activities.fragments;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kushal.qrparking.Global;
import com.kushal.qrparking.R;
import com.kushal.qrparking.controllers.BookingController;
import com.kushal.qrparking.retrofit.APIUrl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ViewQrFragment extends Fragment implements BookingController.qrCallBack {

    private TextView viewQrMessage;
    private WebView qrView;
    private Button btnDownloadQr;
    private ProgressDialog mProgressDialog;

    public ViewQrFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_qr, container, false);

        viewQrMessage = view.findViewById(R.id.noIdMessageQr);
        btnDownloadQr = view.findViewById(R.id.btnDownloadQr);
        qrView = view.findViewById(R.id.qrView);

        if ( BookingController.viewQrBookingId == 0){
            qrView.setVisibility(View.GONE);
            viewQrMessage.setVisibility(View.VISIBLE);
            btnDownloadQr.setVisibility(View.GONE);
        }
        if( BookingController.viewQrBookingId > 0){
            getQrImageTag();
            viewQrMessage.setVisibility(View.GONE);
            qrView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            qrView.getSettings().setJavaScriptEnabled(true);
            qrView.setWebViewClient(new WebViewClient());
            registerForContextMenu(qrView);

            // Capture button click
            btnDownloadQr.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent browserIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(APIUrl.BASE_URl+"static/qr/qr.png"));
                    startActivity(browserIntent);
                }
            });
        }

        return view;
    }

    private void getQrImageTag(){
        BookingController.getQrForBooking(BookingController.viewQrBookingId,this);
    }

    @Override
    public void cb(String imageTag) {
        qrView.loadDataWithBaseURL(null,imageTag,"text/html","UTF-8","about:blank");
    }
}
