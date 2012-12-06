package com.App42.TicTacToe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends Activity
{    
    
    private final int INTERNET_ALERT_DIALOG_ID = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);               
        setContentView(R.layout.splash);                     
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onStart()
    {
        super.onStart();         
                               
        if(!isInternetConnected())
        {
            showDialog(INTERNET_ALERT_DIALOG_ID);
            return;
        }
        
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //Finish the splash activity so it can't be returned to.
                SplashActivity.this.finish();
                // Create an Intent that will start the main activity.
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
            }
        }, Constants.SPLASH_DISPLAY_TIME);           
        
    }
    
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialog = null;
        switch(id) {
        case INTERNET_ALERT_DIALOG_ID:
            // do the work to define the pause Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connect to a network and try again.")
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                     SplashActivity.this.finish();
                }
            });
            dialog = builder.create();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    private boolean isInternetConnected() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}