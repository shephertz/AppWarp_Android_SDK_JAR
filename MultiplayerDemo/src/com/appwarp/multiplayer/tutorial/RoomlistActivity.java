package com.appwarp.multiplayer.tutorial;

import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;


public class RoomlistActivity extends Activity implements ZoneRequestListener, RoomRequestListener{
	
	private WarpClient theClient;
	private RoomlistAdapter roomlistAdapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_list);
		listView = (ListView)findViewById(R.id.roomList);
		roomlistAdapter = new RoomlistAdapter(this);
		init();
	}
	private void init(){
        try {
            theClient = WarpClient.getInstance();
        } catch (Exception ex) {
        	Utils.showToastAlert(this, "Exception in Initilization");
        }
    }
	public void onStart(){
		super.onStart();
		theClient.addZoneRequestListener(this);
		theClient.getRoomWithNUser(1);// trying to get room with at least one user
	}
	public void onStop(){
		super.onStop();
		theClient.removeZoneRequestListener(this);
		theClient.removeRoomRequestListener(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		theClient.disconnect();
	}
	
	public void joinRoom(String roomId){
		if(roomId!=null && roomId.length()>0){
			theClient.joinRoom(roomId);
			theClient.addRoomRequestListener(this);
			if(progressDialog!=null){
				progressDialog.setMessage("joining room...");
			}else{
				progressDialog = ProgressDialog.show(this, "", "joining room...");
			}
		}else{
			Log.d("joinRoom", "failed:"+roomId);
		}
	}
	
	public void onJoinNewRoomClicked(View view){
		progressDialog = ProgressDialog.show(this,"","Pleaes wait...");
		progressDialog.setCancelable(true);
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put("topLeft", "");
		properties.put("topRight", "");
		properties.put("bottomLeft", "");
		properties.put("bottomRight", "");
		theClient.createRoom(""+System.currentTimeMillis(), "Saurav", 4, properties);
	}
	
	@Override
	public void onCreateRoomDone(final RoomEvent event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(event.getResult()==WarpResponseResultCode.SUCCESS){// if room created successfully
					String roomId = event.getData().getId();
					Log.d("roomId", event.getResult()+"onCreateRoomDone"+roomId);
					joinRoom(roomId);
				}else{
					progressDialog.dismiss();
					Utils.showToastAlert(RoomlistActivity.this, "Room creation failed...");
				}
			}
		});
	}
	
	@Override
	public void onDeleteRoomDone(RoomEvent event) {
		
	}
	@Override
	public void onGetAllRoomsDone(AllRoomsEvent event) {
		
	}
	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetMatchedRoomsDone(final MatchedRoomsEvent event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				RoomData[] roomDataList = event.getRoomsData();
				if(roomDataList.length>0){
					roomlistAdapter.setData(roomDataList);
					listView.setAdapter(roomlistAdapter);
				}else{
					roomlistAdapter.clear();
				}
			}
		});
		
	}
	@Override
	public void onGetOnlineUsersDone(AllUsersEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onJoinRoomDone(final RoomEvent event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				Log.d("onJoinRoomDone", ""+event.getResult());
				if(event.getResult()==0){// Room created successfully
					goToGameScreen(event.getData().getId());
				}else{
					Utils.showToastAlert(RoomlistActivity.this, "Room joining failed");
				}
			}
		});
	}
	private void goToGameScreen(String roomId){
		Intent intent = new Intent(RoomlistActivity.this, AndEngineTutorialActivity.class);
		intent.putExtra("roomId", roomId);
		startActivity(intent);
	}
	
	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
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
