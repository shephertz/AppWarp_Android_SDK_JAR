package com.example.matchmaking;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

public class ResultActivity extends Activity implements ZoneRequestListener, RoomRequestListener{
	
	private long time;
	private CheckBox cb1;
	private CheckBox cb2;
	private CheckBox cb3;
	private TextView resultTextView;
	private Button chatButton;
	private WarpClient theClient;
	Hashtable<String, Object> propertiesToMatch ;
	private String roomIdJoined = "";
	private String roomNameJoined = "";
	private Timer timer;
	private long timeCounter = 0;
	private boolean withoutStatus = false;
	private boolean isOnJoinRoom = false;
	private String[] roomIds;
	private int roomIdCounter=0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d("on create", "Result Activity");
		setContentView(R.layout.activity_result);
		cb1 = (CheckBox)findViewById(R.id.checkBox1);
		cb2 = (CheckBox)findViewById(R.id.checkBox2);
		cb3 = (CheckBox)findViewById(R.id.checkBox3);
		chatButton = (Button)findViewById(R.id.chatBtn);
		chatButton.setEnabled(false);
		resultTextView = (TextView)findViewById(R.id.resultTextView);
		withoutStatus = getIntent().getBooleanExtra("isWithout", false);
		String topic = getIntent().getStringExtra("topic").toString();
		if(propertiesToMatch==null){
			propertiesToMatch = new Hashtable<String, Object>();
		}else{
			propertiesToMatch.clear();
		}
		propertiesToMatch.put("topic", topic);
		timeCounter = 0;
		roomIdCounter = 0;
		isOnJoinRoom  = false;
		roomIds = null;
		load(withoutStatus);
		startTimer();
		roomIdJoined = "";
		roomNameJoined = "";
	}
	public void onStart(){
		super.onStart();
		theClient.addZoneRequestListener(this);
		theClient.addRoomRequestListener(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		stopTimer();
		if(roomIdJoined!=null && roomIdJoined.length()>0 && isOnJoinRoom){
			theClient.leaveRoom(roomIdJoined);
		}
	}
	public void onStop(){
		super.onStop();
		theClient.removeZoneRequestListener(this);
		theClient.removeRoomRequestListener(this);
	}
	public void onChatClicked(View view){
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("roomId", roomIdJoined);
		startActivity(intent);
	}
	 
	private void load(boolean isWithout){
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(isWithout){
			theClient.getAllRooms();
		}else{
			findViewById(R.id.beforeView).setVisibility(View.GONE);
			theClient.joinRoomWithProperties(propertiesToMatch);
		}
		cb1.setChecked(false);
		cb2.setChecked(false);
		cb3.setChecked(false);
		
	}
	
	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) {
		Hashtable roomProperties = event.getProperties();
		Log.d("roomProperties"+roomProperties, "propertiesToMatch"+propertiesToMatch);
		boolean status = hasMatchingProperties(roomProperties, propertiesToMatch);
        if(status){
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					cb2.setChecked(true);
				}
			});
        	theClient.joinRoom(""+event.getData().getId());
        }else{
        	if(roomIdCounter<roomIds.length){
        		theClient.getLiveRoomInfo(roomIds[roomIdCounter]);
        		roomIdCounter++;
        	}else{
        		runOnUiThread(new Runnable() {
    				@Override
    				public void run() {
    					resultTextView.setText("\nNo Such Room Found \nTime Taken: "+timeCounter +"\nResult code: "+event.getResult());
    				}
    			});
        		
        	}
        }
	}
	public boolean hasMatchingProperties(Hashtable totalProperties, Hashtable propertiesToMatch) {
        if(propertiesToMatch == null || totalProperties == null ){
            return false;    
        }
        Enumeration enumKeys = propertiesToMatch.keys();
        while(enumKeys.hasMoreElements()){
            String key_join = enumKeys.nextElement().toString();
            if(totalProperties.get(key_join) == null){
                return false;
            }
            if(totalProperties.get(key_join).equals(propertiesToMatch.get(key_join))){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }
	
	@Override
	public void onJoinRoomDone(final RoomEvent event) {
		stopTimer();
		isOnJoinRoom = true;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(withoutStatus){
					cb3.setChecked(true);
				}
				if(event.getResult()==0){// success case
					chatButton.setEnabled(true);
					roomIdJoined = event.getData().getId();
					roomNameJoined = event.getData().getName();
					resultTextView.setText("\nTime Taken: "+timeCounter +"\nResult code: "+event.getResult()+" Success \nRoomID: "+roomIdJoined );
				}else{
					resultTextView.setText("\nRoom join failed \nTime Taken: "+timeCounter +"\nResult code: "+event.getResult());
				}
			}
		});
	}
	
	@Override
	public void onLeaveRoomDone(RoomEvent event ) {
		
	}
	
	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		
	}
	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		
	}
	@Override
	public void onCreateRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onDeleteRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onGetAllRoomsDone(AllRoomsEvent event) {
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cb1.setChecked(true);
			}
		});
		
		roomIds = event.getRoomIds();
		Log.d("onGetAllRoomsDone"+roomIds, "onGetAllRoomsDone"+roomIds.length);
		if(roomIds!=null && roomIds.length>0){
			theClient.getLiveRoomInfo(roomIds[0]);
			roomIdCounter++;
		}
	}
	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent event) {
		
	}
	@Override
	public void onGetMatchedRoomsDone(MatchedRoomsEvent arg0) {
		
	}
	@Override
	public void onGetOnlineUsersDone(AllUsersEvent arg0) {
		
	}
	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
		
	}
	public void update(){
		timeCounter++;
	}
	private void startTimer(){
		if(timer==null){
			timer  = new Timer();
			timer.schedule(new CountTimerTask(this), 1 , 1 );
		}
	}
	private void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
	}
}
class CountTimerTask extends TimerTask{
	ResultActivity gameActivity;
	CountTimerTask(ResultActivity gameActivity){
		this.gameActivity = gameActivity;
	}
	public void run(){
		gameActivity.update();
	}
}
