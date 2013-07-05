package com.example.andengineappwarp.multiplayer;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.example.andengineappwarp.multiplayer.handler.ConnectionHandler;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class MainActivity extends Activity {

	private EditText nameEditText;
	private WarpClient theClient;
    private ProgressDialog progressDialog;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		init();
		ConnectionHandler.getInstance().setResultActivity(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
	}
	
	public void connectToAppwarp(View view){
		if(nameEditText.getText().toString().trim().length()==0){
			Utils.showToastAlert(this, getApplicationContext().getString(R.string.enterName));
		}else{
			progressDialog = ProgressDialog.show(this,"","connecting to appwarp");
			progressDialog.setCancelable(true);
			theClient.addConnectionRequestListener(ConnectionHandler.getInstance());
			theClient.connectWithUserName(nameEditText.getText().toString().trim());
		}
	}
	
	public void goToRoomSelectionScreen(boolean success){
		progressDialog.dismiss(); 
		if(success){
			Utils.userName = nameEditText.getText().toString().trim();
			Intent intent = new Intent(MainActivity.this, RoomlistActivity.class);
			startActivity(intent);
		}
	}
	
	private void init(){
		WarpClient.initialize(Constants.apiKey, Constants.secretKey);
        try {
            theClient = WarpClient.getInstance();
        } catch (Exception ex) {
        	Utils.showToastAlert(this, "Exception in Initilization");
        }
    }
	
}
