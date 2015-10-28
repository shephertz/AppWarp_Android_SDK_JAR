package com.turnbased.appwarp.longestline;


import com.sample.turnbasedtictactoe.R;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity implements ConnectionRequestListener {

    private EditText nameEditText;
    private ProgressDialog progressDialog;
    private Handler UIThreadHandler = new Handler();
    private RoomFinder roomFinder = new RoomFinder();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = (EditText) findViewById(R.id.editTextName);
    }

    @Override
    public void onStart(){
        super.onStart();
        Utilities.getWarpClient().addConnectionRequestListener(this); 
    }
    
    @Override
    public void onStop(){
        super.onStop();
        Utilities.getWarpClient().removeConnectionRequestListener(this); 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onConnectClicked(View view){
        String userName = nameEditText.getText().toString();
        if(userName.length()>0){
            progressDialog = ProgressDialog.show(this, "", "Please wait...");
            progressDialog.setCancelable(true);             
            Utilities.getWarpClient().connectWithUserName(userName);
            Utilities.localUsername = userName;
        }else{
            
        }
    }

    private void onRoomFound(final boolean success){
        // yay go to game scene        
        
        UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {    
                progressDialog.dismiss();
                if(success){
                    Intent myIntent = new Intent(MainActivity.this, GameActivity.class);         
                    startActivity(myIntent);
                }
            }
        });        
    }
    
    @Override
    public void onConnectDone(ConnectEvent evt) {
        Log.d("AppWarpTrace", "onConnectDone "+evt.getResult());
        if(evt.getResult() == WarpResponseResultCode.SUCCESS){
            roomFinder.findRoom();
        }
        else{
            onRoomFound(false);
        }
        
    }

    @Override
    public void onDisconnectDone(ConnectEvent arg0) {
        // TODO Auto-generated method stub  
        
    }
    
    private class RoomFinder implements RoomRequestListener, ZoneRequestListener{

        public RoomFinder(){
            
        }
        
        public void findRoom() {            
            Utilities.getWarpClient().addRoomRequestListener(this);
            Utilities.getWarpClient().addZoneRequestListener(this);
            Utilities.getWarpClient().joinRoomInRange(1, 1, true);
            Utilities.isLocalPlayerX = true;
        }
        
        @Override
        public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onJoinRoomDone(RoomEvent evt) {
            Log.d("AppWarpTrace", "onJoinRoomDone "+evt.getResult());
            if(evt.getResult() == WarpResponseResultCode.SUCCESS){
                Utilities.getWarpClient().subscribeRoom(evt.getData().getId());
            }
            else{
                Utilities.getWarpClient().createTurnRoom("dynamic", "dev", 2, null, 10);
            }
        }

        @Override
        public void onLeaveRoomDone(RoomEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onLockPropertiesDone(byte arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onSubscribeRoomDone(RoomEvent evt) {
            Log.d("AppWarpTrace", "onSubscribeRoomDone "+evt.getResult());
            if(evt.getResult() == WarpResponseResultCode.SUCCESS){
                Utilities.game_room_id = evt.getData().getId();
                onRoomFound(true);
            }
            
        }

        @Override
        public void onUnSubscribeRoomDone(RoomEvent arg0) {
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
        public void onCreateRoomDone(RoomEvent evt) {
            Log.d("AppWarpTrace", "onCreateRoomDone "+evt.getResult());
            if(evt.getResult() == WarpResponseResultCode.SUCCESS){
                Utilities.isLocalPlayerX = false;
                Utilities.getWarpClient().joinRoom(evt.getData().getId());
            }            
        }

        @Override
        public void onDeleteRoomDone(RoomEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onGetAllRoomsDone(AllRoomsEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onGetLiveUserInfoDone(LiveUserInfoEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onGetMatchedRoomsDone(MatchedRoomsEvent arg0) {
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

    @Override
    public void onInitUDPDone(byte arg0) {
        // TODO Auto-generated method stub
        
    }
}
