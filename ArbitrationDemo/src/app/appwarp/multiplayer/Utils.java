package app.appwarp.multiplayer;

import android.content.Context;
import android.widget.Toast;


public class Utils {

	public static String userName = "";
	
	public static void showToastAlert(Context ctx, String message){
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}
	
	public static float getPercentFromValue(float number, float amount){
		float percent = (number/amount)*100;
		return percent;
	}
	
	public static float getValueFromPercent(float percent, float amount){
		float value = (percent/100)*amount;
		return value;
	}
}
