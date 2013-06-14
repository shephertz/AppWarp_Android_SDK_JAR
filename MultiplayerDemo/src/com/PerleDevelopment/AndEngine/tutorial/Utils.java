package com.PerleDevelopment.AndEngine.tutorial;

import android.content.Context;
import android.widget.Toast;


public class Utils {

	public static String userName = "";
	
	public static void showToastAlert(Context ctx, String message){
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}
}
