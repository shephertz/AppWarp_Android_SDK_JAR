package com.turnbased.appwarp.longestline;

import java.util.HashMap;
import java.util.Hashtable;

import android.util.Log;
import android.widget.Toast;

import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.TurnBasedRoomListener;

public class GameMessenger implements NotifyListener, TurnBasedRoomListener, ConnectionRequestListener {

    private GameActivity observer;

    public GameMessenger(GameActivity gameActivity) {
        observer = gameActivity;
    }

    @Override
    public void onConnectDone(ConnectEvent evt) {
        Log.d("AppWarpTrace", "onConnectDone "+evt.getResult());        
    }

    @Override
    public void onDisconnectDone(ConnectEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onSendMoveDone(byte result) {
        Log.d("AppWarpTrace", "onSendMoveDone "+result);        
    }
    
    
    @Override
    public void onChatReceived(ChatEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onPrivateChatReceived(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRoomCreated(RoomData arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRoomDestroyed(RoomData arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpdatePeersReceived(UpdateEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserJoinedLobby(LobbyData arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserJoinedRoom(RoomData arg0, String username) {        
        Log.d("AppWarpTrace", "onUserJoinedRoom "+username);
    }

    @Override
    public void onUserLeftLobby(LobbyData arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserLeftRoom(RoomData arg0, String username) {
        Log.d("AppWarpTrace", "onUserLeftRoom "+username);
        observer.handleRemoteLeft();
    }

    @Override
    public void onUserPaused(String arg0, boolean arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserResumed(String arg0, boolean arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onMoveCompleted(MoveEvent evt) {
        Log.d("AppWarpTrace", "onMoveCompleted turn is "+evt.getNextTurn());   
        observer.onMoveCompleted(evt);        
    }


    public void start() {
        Utilities.getWarpClient().addTurnBasedRoomListener(this);
        Utilities.getWarpClient().addNotificationListener(this);
        Utilities.getWarpClient().addConnectionRequestListener(this);        
    }

    public void stop() {
        Utilities.getWarpClient().disconnect();
        Utilities.getWarpClient().removeConnectionRequestListener(this);
        Utilities.getWarpClient().removeNotificationListener(this);
        Utilities.getWarpClient().removeTurnBasedRoomListener(this);        
    }

    public void sendMove(String boardState) {
        Utilities.getWarpClient().sendMove(boardState);        
    }

    @Override
    public void onInitUDPDone(byte arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetMoveHistoryDone(byte arg0, MoveEvent[] arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStartGameDone(byte res) {        
        Log.d("AppWarpTrace", "onStartGameDone "+res);        
    }

    @Override
    public void onStopGameDone(byte arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGameStarted(String started, String id, String nextTurn) {
        Log.d("AppWarpTrace", "onGameStarted nextTurn "+nextTurn); 
        observer.onGameStarted(nextTurn);        
    }

    @Override
    public void onGameStopped(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserChangeRoomProperty(RoomData arg0, String arg1,
            HashMap<String, Object> arg2, HashMap<String, String> arg3) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void onSetNextTurnDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNextTurnRequest(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrivateUpdateReceived(String arg0, byte[] arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}    
}
