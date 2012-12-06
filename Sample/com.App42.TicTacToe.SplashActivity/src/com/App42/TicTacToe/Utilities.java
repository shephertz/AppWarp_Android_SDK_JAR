package com.App42.TicTacToe;

import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.shephertz.app42.paas.sdk.android.storage.Storage.JSONDocument;

public class Utilities {		 	
	
	public static JSONArray deepCopyJSONArray(JSONArray src)
	{
		JSONArray dst = new JSONArray();
		for(int i=0; i<src.length(); i++){
			try {
				dst.put(src.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return dst;
		
	}

	public static Bitmap loadBitmap(String url) {

        Bitmap bitmap = null;

        try {
            InputStream in = new java.net.URL(url).openStream();   
            bitmap = BitmapFactory.decodeStream(in);
        } 
        catch (Exception e) {

        } 

        return bitmap;
    }
    
	public static boolean areCharsEqual(char c1, char c2, char c3, char c4)
	{
		if(c1 == c2 && c2==c3 && c3==c4)
		{
			return true;
		}
		return false;
	}	
	
	public static JSONObject buildNewGameJSON(String username){
		// prepare the gameObj myself as I'm the one who joined first.
		JSONObject gameObject = new JSONObject();

		try {
			gameObject.put(Constants.GameFirstUserKey, username);
			gameObject.put(Constants.GameSecondUserKey, "");
			gameObject.put(Constants.GameStateKey, Constants.GameStateIdle);					
			gameObject.put(Constants.GameBoardKey, Constants.GameEmptyBoard);			
			gameObject.put(Constants.GameWinnerKey, "");
			gameObject.put(Constants.GameNextMoveKey, username);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gameObject;		
	}
	
	public static  ArrayList<JSONObject> getJSONObjectsFromJSONDocuments(ArrayList<JSONDocument> input)
	{
		ArrayList<JSONObject> retValue = new ArrayList<JSONObject>();
		for(int i=0; i<input.size(); i++)
		{
			JSONObject obj;
			try {
				obj = new JSONObject(input.get(i).jsonDoc);
				retValue.add(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		return retValue;
	}				
}