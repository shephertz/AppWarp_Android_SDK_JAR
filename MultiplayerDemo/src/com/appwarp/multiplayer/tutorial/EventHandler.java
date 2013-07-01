package com.appwarp.multiplayer.tutorial;

import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONObject;

import android.util.Log;

import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

public class EventHandler implements RoomRequestListener, NotifyListener{

	private AndEngineTutorialActivity gameScreen;
	
	private Hashtable<String, Object> properties;
	
	public EventHandler(AndEngineTutorialActivity gameScreen) {
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
				gameScreen.updateMove(sender, xCord, yCord);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
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
	public void onUserChangeRoomProperty(RoomData roomData, String userName, Hashtable<String, Object> tableProperties, Hashtable<String, String> lockProperties) {
		if(userName.equals(Utils.userName)){
			// just update the local property table.
			// no need to update UI as we have already done so.
			properties = tableProperties;
			return;
		}
		
		// notification is from a remote user. We need to update UI accordingly.
		Enumeration<String> keyEnum = tableProperties.keys();
		while(keyEnum.hasMoreElements()){
			String key = keyEnum.nextElement();
			String value = tableProperties.get(key).toString();
			if(value.length()>0){
				if(!this.properties.get(key).toString().equals(value)){
					int fruitId = Integer.parseInt(value);
					gameScreen.placeObject(fruitId, key, userName, false);
					properties.put(key, value);
				}
			}
		}
	}

	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserJoinedRoom(RoomData roomData, String name) {
		gameScreen.addMorePlayer(true, name);
	}

	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
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
		Enumeration<String> keyEnum = event.getProperties().keys();
		while(keyEnum.hasMoreElements()){
			String key = keyEnum.nextElement();
			String value = event.getProperties().get(key).toString();
			if(value.length()>0){
				int fruitId = Integer.parseInt(value);
				gameScreen.placeObject(fruitId, key, null, false);
			}
		}
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent event) {
		
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

	@Override
	public void onMoveCompleted(MoveEvent arg0) {
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

	

}
