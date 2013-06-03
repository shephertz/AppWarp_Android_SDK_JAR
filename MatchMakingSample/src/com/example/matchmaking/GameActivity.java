package com.example.matchmaking;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

public class GameActivity extends Activity implements ZoneRequestListener, RoomRequestListener{
	
	private long time;
	private CheckBox cb1;
	private CheckBox cb2;
	private CheckBox cb3;
	
	private TextView resultTextView;
	
	private ProgressDialog progressDialog;
	private WarpClient theClient;
	
	Hashtable<String, Object> propertiesToMatch ;
	private String roomIdJoined = "";
	private Timer timer;
	private long timeCounter = 0;
	private boolean withoutStatus = false;
	private boolean isOnJoinRoom = false;
	private String[] roomIds;
	private int roomIdCounter=0;
	private int i=0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		cb1 = (CheckBox)findViewById(R.id.checkBox1);
		cb2 = (CheckBox)findViewById(R.id.checkBox2);
		cb3 = (CheckBox)findViewById(R.id.checkBox2);
		resultTextView = (TextView)findViewById(R.id.resultTextView);
		withoutStatus = getIntent().getBooleanExtra("isWithout", false);
		String property = getIntent().getStringExtra("level").toLowerCase();
		Log.d("property", property+"");
		if(propertiesToMatch==null){
			propertiesToMatch = new Hashtable<String, Object>();
		}else{
			propertiesToMatch.clear();
		}
		propertiesToMatch.put("level", property);
		timeCounter = 0;
		roomIdCounter = 0;
		i=0;
		isOnJoinRoom  = false;
		roomIds = null;
		load(withoutStatus);
		
	}
	public void onStart(){
		super.onStart();
		startTimer();
		roomIdJoined = "";
		
	}
	public void onStop(){
		super.onStop();
		stopTimer();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(roomIdJoined!=null && roomIdJoined.length()>0 && isOnJoinRoom){
			theClient.removeZoneRequestListener(this);
			theClient.removeRoomRequestListener(this);
			theClient.leaveRoom(roomIdJoined);
		}
	}
	 
	private void load(boolean isWithout){
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		theClient.addZoneRequestListener(this);
		theClient.addRoomRequestListener(this);
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
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		Hashtable roomProperties = event.getProperties();
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
        	}
        }
	}
	public boolean hasMatchingProperties(Hashtable totalProperties, Hashtable propertiesToMatch) {
        if(propertiesToMatch == null){
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
		i++;
		isOnJoinRoom = true;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(withoutStatus){
					cb3.setChecked(true);
				}
				if(event.getResult()==0){// success case
					roomIdJoined = event.getData().getId();
					resultTextView.setText("\nTime Taken: "+timeCounter +"\nResult code: "+event.getResult()+"\n RoomID: "+roomIdJoined +" i "+i);
				}else{
					resultTextView.setText("\nTime Taken: "+timeCounter +"\nResult code: "+event.getResult()+" i "+i);
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
		Log.d("onGetAllRoomsDone", "onGetAllRoomsDone");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cb1.setChecked(true);
			}
		});
		roomIds = event.getRoomIds();
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
	GameActivity gameActivity;
	CountTimerTask(GameActivity gameActivity){
		this.gameActivity = gameActivity;
	}
	public void run(){
		gameActivity.update();
	}
}
