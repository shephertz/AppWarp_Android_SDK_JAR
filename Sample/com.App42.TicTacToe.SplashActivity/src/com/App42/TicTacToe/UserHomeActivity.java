package com.App42.TicTacToe;

import java.util.ArrayList;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.app.ProgressDialog;

public class UserHomeActivity extends Activity implements ZoneRequestListener, RoomRequestListener{

	private String userName;
	private EditText newGameName;
		
	private ProgressDialog progressDialog;	
	private ArrayList<String> roomsList = new ArrayList<String>();
	private String roomIdToJoin;	
	Handler UIThreadHandler = new Handler();
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);              
    }  
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home);            
        
        newGameName = (EditText) findViewById(R.id.game_name);               
                        
        // don't automatically show the soft keyboard. wait till user actually clicks on 
        // the edit box.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        userName = getIntent().getStringExtra(Constants.IntentUserName);   
        AsyncApp42ServiceApi.getMyWarpClient().addRoomRequestListener(this);
    }    
    
	public void onStartGameClicked(View view) {
		if(newGameName.getText().toString().isEmpty())
		{
			return;
		}	
						
		progressDialog = ProgressDialog.show(this, "", "creating new room");
		AsyncApp42ServiceApi.getMyWarpClient().addZoneRequestListener(this);
		AsyncApp42ServiceApi.getMyWarpClient().createRoom(newGameName.getText().toString(), userName, 2, null);

	}
	
	public void onJoinRandomGameClicked(View view){
		AsyncApp42ServiceApi.getMyWarpClient().addZoneRequestListener(this);
		AsyncApp42ServiceApi.getMyWarpClient().getAllRooms();
		progressDialog = ProgressDialog.show(this, "", "joining a random game..");
	}
	
	public void onSignOutClicked(View view) {
		SharedPreferences mPrefs = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove(Constants.SharedPrefUname);
		editor.apply();		
		
		AsyncApp42ServiceApi.getMyWarpClient().disconnect();
		
    	Intent myIntent = new Intent(this, MainActivity.class);	    	
    	this.startActivity(myIntent);		
	}	
	
	@Override
    public void onStart()
    {
    	super.onStart();
        Intent intent = getIntent();
        userName = intent.getStringExtra(Constants.IntentUserName);    	   	
    }
    
	@Override
	protected void onStop()
	{
		super.onStop();
		AsyncApp42ServiceApi.getMyWarpClient().removeRoomRequestListener(this);
	}
	
    public void onNewIntent(Intent newIntent)
    {
    	super.onNewIntent(newIntent);            	       	
    	this.setIntent(newIntent);
    }

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
		boolean done = false;
		if(arg0.getResult() == WarpResponseResultCode.SUCCESS){
			String[] users = arg0.getJoinedUsers();
			if(users!=null && users.length == 1){				
				// yay found a room I can join!
				roomIdToJoin = arg0.getData().getId();				
				AsyncApp42ServiceApi.getMyWarpClient().joinRoom(roomIdToJoin);
				done = true;
			}
		}		
		if(!done){
			// continue finding first available room to join.
			if(roomsList.size() > 0){			
				AsyncApp42ServiceApi.getMyWarpClient().getLiveRoomInfo(roomsList.remove(0));				
			}			
			else{
				
				// callback is not the main UI thread. So post message to UI thread
				// through its handler. Android UI elements can't be accessed from
				// non-UI threads.		
				UIThreadHandler.post(new Runnable() {
		            @Override
		            public void run() {    
		            	progressDialog.dismiss();
		            }
		        });			
			}
		}
		
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {

		if(arg0.getResult() == WarpResponseResultCode.SUCCESS){
			// callback is not the main UI thread. So post message to UI thread
			// through its handler. Android UI elements can't be accessed from
			// non-UI threads.		
			UIThreadHandler.post(new Runnable() {
	            @Override
	            public void run() {    
	            	progressDialog.dismiss();
	    			Intent myIntent = new Intent(UserHomeActivity.this, GameActivity.class);
	    			myIntent.putExtra(Constants.IntentGameRoomId, roomIdToJoin);
	    			myIntent.putExtra(Constants.IntentUserName, userName);
	    			UserHomeActivity.this.startActivity(myIntent);
	            }
	        });	
			
		
		}		
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
	public void onCreateRoomDone(RoomEvent arg0) {
		if(arg0.getResult() == WarpResponseResultCode.SUCCESS){
			roomIdToJoin = arg0.getData().getId();
			AsyncApp42ServiceApi.getMyWarpClient().joinRoom(roomIdToJoin);
		}		
	}

	@Override
	public void onDeleteRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetAllRoomsDone(AllRoomsEvent arg0) {
		if((arg0.getResult() == WarpResponseResultCode.SUCCESS) && (arg0.getRoomIds()!=null)){
			for(int i=0; i<arg0.getRoomIds().length; i++ ){
				roomsList.add(arg0.getRoomIds()[i]);
			}
			
			// start finding first available room to join.	
			if(roomsList.size() > 0){						
				AsyncApp42ServiceApi.getMyWarpClient().getLiveRoomInfo(roomsList.remove(0));				
			}
		}
		else{
			progressDialog.dismiss();
		}
		
	}

	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent arg0) {
		// TODO Auto-generated method stub
		
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
	public void onLockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetMatchedRoomsDone(MatchedRoomsEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener#onGetRoomsCountDone(com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent)
	 */
	@Override
	public void onGetRoomsCountDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener#onGetUsersCountDone(com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent)
	 */
	@Override
	public void onGetUsersCountDone(AllUsersEvent arg0) {
		// TODO Auto-generated method stub
		
	}	
}