package com.appwarp.multiplayer.tutorial;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class MainActivity extends Activity implements ConnectionRequestListener {

	private WarpClient theClient;
	private EditText nameEditText;
    private ProgressDialog progressDialog;
    private boolean isConnected = false;
	private int selectedMonster;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		selectedMonster = -1;
		init();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(theClient!=null && isConnected){
			theClient.disconnect();
		}
	}
	
	public void onMonsterClicked(View view){
		switch (view.getId()) {
			case R.id.imageView1:
				selectedMonster = 1;
			break;
			case R.id.imageView2:
				selectedMonster = 2;
			break;
			case R.id.imageView3:
				selectedMonster = 3;
			break;
			case R.id.imageView4:
				selectedMonster = 4;
			break;
		}
	}
	
	public void onPlayGameClicked(View view){
		if(nameEditText.getText().length()==0){
			Utils.showToastAlert(this, getApplicationContext().getString(R.string.enterName));
			return;
		}
		if(selectedMonster!=-1){
			theClient.addConnectionRequestListener(this); 
			String userName = nameEditText.getText()+"_@"+selectedMonster;
			Utils.userName = userName;
			Log.d("Name to Join ", ""+userName);
			theClient.connectWithUserName(userName);
			progressDialog =  ProgressDialog.show(this, "", "connecting to appwarp");
		}else{
			Utils.showToastAlert(this, getApplicationContext().getString(R.string.alertSelect));
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
	
	@Override
	public void onConnectDone(final ConnectEvent event) {
		Log.d("onConnectDone", event.getResult()+"");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
			}
		});
		if(event.getResult() == WarpResponseResultCode.SUCCESS){// go to room  list 
			isConnected = true;
			Intent intent = new Intent(MainActivity.this, RoomlistActivity.class);
			startActivity(intent);
		}else{
			Utils.showToastOnUIThread(MainActivity.this, "connection failed");
		}
	}
	
	@Override
	public void onDisconnectDone(final ConnectEvent event) {
		Log.d("onDisconnectDone", event.getResult()+"");
	}

    @Override
	public void onInitUDPDone(byte arg0) {
		
	}
    
}
