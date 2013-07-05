package com.example.andengineappwarp.multiplayer.handler;

import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.example.andengineappwarp.multiplayer.AndEngineTutorialActivity;
import com.example.andengineappwarp.multiplayer.RoomlistActivity;
import com.example.andengineappwarp.multiplayer.SelectionActivity;
import com.example.andengineappwarp.multiplayer.Utils;
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

public class ResponseHandler implements ZoneRequestListener, RoomRequestListener, NotifyListener{

	private Activity resultActivity;
	private static ResponseHandler responseHandler;
	
	private ResponseHandler(){
		
	}
	
	public static ResponseHandler getInstance(){
		if(responseHandler==null){
			responseHandler = new ResponseHandler();
		}
		return responseHandler;
	}
	
	public void setResultActivity(Activity activity){
		this.resultActivity = activity;
	}
	
	@Override
	public void onChatReceived(ChatEvent event) {
		
		
		if (resultActivity instanceof AndEngineTutorialActivity) {
			AndEngineTutorialActivity gameScreen = (AndEngineTutorialActivity) resultActivity;
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
	public void onUserChangeRoomProperty(final RoomData roomData, final String userName, final Hashtable<String, Object> tableProperties, final Hashtable<String, String> lockProperties) {
		
		if (resultActivity instanceof AndEngineTutorialActivity) {
			final AndEngineTutorialActivity gameScreen = (AndEngineTutorialActivity) resultActivity;
			Enumeration<String> keyEnum = tableProperties.keys();
			while(keyEnum.hasMoreElements()){
				final String key = keyEnum.nextElement();
				final String owner = lockProperties.get(key);
				if(owner!=null && owner.length()>0){
					gameScreen.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(owner.equals(Utils.userName)){
								gameScreen.addMorePlayer(true, key, owner);
							}else{
								gameScreen.addMorePlayer(false, key, owner);
							}
						}
					});
				}
			}
		}else if (resultActivity instanceof SelectionActivity) {
			final SelectionActivity selectionScreen = (SelectionActivity) resultActivity;
			selectionScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					selectionScreen.handleLockInfo(tableProperties, lockProperties);
				}
			});
		}
	}
	
	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserJoinedRoom(RoomData roomData, String name) {
		
	}

	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLeftRoom(RoomData roomData, String name) {
		if (resultActivity instanceof AndEngineTutorialActivity) {
			AndEngineTutorialActivity gameScreen = (AndEngineTutorialActivity) resultActivity;
			gameScreen.handleLeave(name);
		}
	}

	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) {

		if (resultActivity instanceof AndEngineTutorialActivity) {
			final AndEngineTutorialActivity gameScreen = (AndEngineTutorialActivity) resultActivity;
			Enumeration<String> keyEnum = event.getProperties().keys();
			while(keyEnum.hasMoreElements()){
				final String key = keyEnum.nextElement();
				final String owner = event.getLockProperties().get(key);
				if(owner!=null && owner.length()>0){
					gameScreen.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(owner.equals(Utils.userName)){
								gameScreen.addMorePlayer(true, key, owner);
							}else{
								gameScreen.addMorePlayer(false, key, owner);
							}
						}
					});
				}
			}
		}else if(resultActivity instanceof SelectionActivity){
			final SelectionActivity selectionScreen = (SelectionActivity) resultActivity;
			selectionScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(event.getResult()==0){
						selectionScreen.handleLockInfo(event.getProperties(), event.getLockProperties());
					}else{
						selectionScreen.handleLockInfo(null, null);
					}
				}
			});
		}
	}

	@Override
	public void onJoinRoomDone(final RoomEvent event) {
		
		if (resultActivity instanceof SelectionActivity) {
			final SelectionActivity selectionScreeen = (SelectionActivity) resultActivity;
			selectionScreeen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(event.getResult()==0){
						selectionScreeen.onRoomJoined(true);
					}else{
						Utils.showToastAlert(selectionScreeen, event.getData().getId()+"failed to join room"+event.getResult());
						selectionScreeen.onRoomJoined(false);
					}
				}
			});
		}
	}

	@Override
	public void onLeaveRoomDone(final RoomEvent event) {
		
		if (resultActivity instanceof AndEngineTutorialActivity) {
			final AndEngineTutorialActivity gameScreen = (AndEngineTutorialActivity) resultActivity;
			gameScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(event.getResult()==0){
						gameScreen.onUserLeftRoom(true);
					}else{
//						Log.d("onLeaveRoomDone", event.getResult()+"");
						gameScreen.onUserLeftRoom(false);
					}
				}
			});
		}
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent event) {
//		Log.d("onSubscribeRoomDone", event.getResult()+"");
		if(event.getResult()==0){
			
		}
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
	public void onLockPropertiesDone(final byte result) {
		
		if (resultActivity instanceof SelectionActivity) {
			final SelectionActivity selectionScreen = (SelectionActivity) resultActivity;
			selectionScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(result==0){
						selectionScreen.onPropertyLocked(true);
					}else{
						Utils.showToastAlert(selectionScreen, "error in lock property");
						selectionScreen.onPropertyLocked(false);
					}
				}
			});
		}
		
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	// zone request listener
	@Override
	public void onCreateRoomDone(final RoomEvent event) {
		
		if (resultActivity instanceof RoomlistActivity) {
			final RoomlistActivity roomListActivity = (RoomlistActivity) resultActivity;
			roomListActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(event.getResult()==0){// if room created successfully
						String roomId = event.getData().getId();
						roomListActivity.onRoomCreated(roomId);
					}else{
						roomListActivity.onRoomCreated(null);
						Utils.showToastAlert(roomListActivity, "Room creation failed...");
					}
				}
			});
		}
		
	}
	
	@Override
	public void onDeleteRoomDone(RoomEvent arg0) {
		
		
	}
	
	@Override
	public void onGetAllRoomsDone(AllRoomsEvent arg0) {
		
		
	}
	
	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onGetMatchedRoomsDone(final MatchedRoomsEvent event) {
		
		if (resultActivity instanceof RoomlistActivity) {
			final RoomlistActivity roomListScreen = (RoomlistActivity) resultActivity;
			roomListScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(event.getResult()==0){// success case
						roomListScreen.onGetMatchedRooms(event.getRoomsData());
					}else{
						roomListScreen.onGetMatchedRooms(null);
					}
					
				}
			});
		}
	}
	
	@Override
	public void onGetOnlineUsersDone(AllUsersEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
		
		
	}

	@Override
	public void onMoveCompleted(MoveEvent event) {
		// TODO Auto-generated method stub
		
	}

}
