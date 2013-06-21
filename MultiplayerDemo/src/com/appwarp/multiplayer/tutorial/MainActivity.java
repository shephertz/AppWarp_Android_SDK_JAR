package com.appwarp.multiplayer.tutorial;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appwarp.multiplayer.tutorial.R;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class MainActivity extends Activity implements ConnectionRequestListener, RoomRequestListener{

	
	private EditText nameEditText;
	private WarpClient theClient;
    private ProgressDialog progressDialog;
	private int selectedMonster = -1;
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
		if(theClient!=null){
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
	private void goToGameScreen(String roomId){
		if(theClient!=null){
			theClient.removeConnectionRequestListener(this);
			theClient.removeRoomRequestListener(this);
		}
		Intent intent = new Intent(MainActivity.this, AndEngineTutorialActivity.class);
		intent.putExtra("roomId", roomId);
		startActivity(intent);
		selectedMonster = -1;
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
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				if(event.getResult()==WarpResponseResultCode.SUCCESS){// go to room  list 
					Intent intent = new Intent(MainActivity.this, RoomlistActivity.class);
					startActivity(intent);
				}else{
					Utils.showToastAlert(MainActivity.this, "connection failed ");
				}
			}
		});
	}
	
	@Override
	public void onDisconnectDone(final ConnectEvent event) {
		
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJoinRoomDone(final RoomEvent event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				if(event.getResult()==0){// success case
					if(theClient!=null){
						theClient.removeConnectionRequestListener(MainActivity.this);
						theClient.removeRoomRequestListener(MainActivity.this);
					}
					goToGameScreen(event.getData().getId());
				}else{
					Utils.showToastAlert(MainActivity.this, "Room join failed");
				}
			}
		});
	}

	@Override
	public void onLeaveRoomDone(RoomEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
