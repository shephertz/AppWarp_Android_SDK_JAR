package com.appwarp.multiplayer.tutorial;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.util.Log;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class EventHandler implements RoomRequestListener, NotifyListener{

	private GameActivity gameScreen;
	
	private HashMap<String, Object> properties;
	
	public EventHandler(GameActivity gameScreen) {
		this.gameScreen = gameScreen;
	}
	
	@Override
	public void onChatReceived(ChatEvent event) {
		String sender = event.getSender();
		if(sender.equals(Utils.userName)==false){// if not same user
			String message = event.getMessage();
			try{
				JSONObject object = new JSONObject(message);
				float xCord = Float.parseFloat(object.get("X")+"");
				float yCord = Float.parseFloat(object.get("Y")+"");
				gameScreen.updateMove(true, sender, xCord, yCord);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onPrivateChatReceived(String arg0, String arg1) {
		
	}

	@Override
	public void onRoomCreated(RoomData arg0) {
		
	}

	@Override
	public void onRoomDestroyed(RoomData arg0) {
		
	}

	@Override
	public void onUpdatePeersReceived(UpdateEvent arg0) {
		
	}

	@Override
	public void onUserChangeRoomProperty(RoomData roomData, String userName, HashMap<String, Object> tableProperties, HashMap<String, String> lockProperties) {
		if(userName.equals(Utils.userName)){
			// just update the local property table.
			// no need to update UI as we have already done so.
			properties = tableProperties;
			return;
		}
		
		// notification is from a remote user. We need to update UI accordingly.
		
		for (Map.Entry<String, Object> entry : tableProperties.entrySet()) { 
            if(entry.getValue().toString().length()>0){
				if(!this.properties.get(entry.getKey()).toString().equals(entry.getValue())){
					int fruitId = Integer.parseInt(entry.getValue().toString());
					gameScreen.placeObject(fruitId, entry.getKey(), userName, false);
					properties.put(entry.getKey(), entry.getValue());
				}
			}
        }
	}

	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		
		
	}

	@Override
	public void onUserJoinedRoom(RoomData roomData, String name) {
		gameScreen.addMorePlayer(true, name);
	}

	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		
		
	}

	@Override
	public void onUserLeftRoom(RoomData roomData, String name) {
			gameScreen.handleLeave(name);
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		String[] joinedUser = event.getJoinedUsers();
		if(joinedUser!=null){
			for(int i=0;i<joinedUser.length;i++){
				if(joinedUser[i].equals(Utils.userName)){
					gameScreen.addMorePlayer(true, joinedUser[i]);
				}else{
					gameScreen.addMorePlayer(false, joinedUser[i]);
				}
			}
		}else{
			Log.d("hello app", "joined users are null");
		}
		properties = event.getProperties();
		for (Map.Entry<String, Object> entry : properties.entrySet()) { 
            if(entry.getValue().toString().length()>0){
				int fruitId = Integer.parseInt(entry.getValue().toString());
				gameScreen.placeObject(fruitId, entry.getKey(), null, false);
			}
        }
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent event) {
		
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
	public void onMoveCompleted(MoveEvent arg0) {
		
	}

	@Override
	public void onLockPropertiesDone(byte arg0) {
		
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		
	}

	@Override
	public void onGameStarted(String arg0, String arg1, String arg2) {
			
	}

	@Override
	public void onGameStopped(String arg0, String arg1) {
		
	}

	@Override
	public void onUserPaused(String arg0, boolean arg1, String arg2) {
		
	}

	@Override
	public void onUserResumed(String arg0, boolean arg1, String arg2) {
		
	}
	
}
