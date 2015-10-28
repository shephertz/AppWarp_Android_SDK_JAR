package app.appwarp.multiplayer.handler;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import app.appwarp.multiplayer.GameActivity;
import app.appwarp.multiplayer.MonsterSelectionActivity;
import app.appwarp.multiplayer.RoomlistActivity;
import app.appwarp.multiplayer.Utils;

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

public class EventHandler implements ZoneRequestListener, RoomRequestListener, NotifyListener{

	private Activity resultActivity;
	private static EventHandler responseHandler;
	
	private EventHandler(){
		
	}
	
	public static EventHandler getInstance(){
		if(responseHandler==null){
			responseHandler = new EventHandler();
		}
		return responseHandler;
	}
	
	public void setResultActivity(Activity activity){
		this.resultActivity = activity;
	}
	
	@Override
	public void onChatReceived(ChatEvent event) {
		if (resultActivity instanceof GameActivity) {
			GameActivity gameScreen = (GameActivity) resultActivity;
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
	public void onUserChangeRoomProperty(final RoomData roomData, final String userName, final HashMap<String, Object> tableProperties, final HashMap<String, String> lockProperties) {
		
		if (resultActivity instanceof GameActivity) {
			final GameActivity gameScreen = (GameActivity) resultActivity;
			for (Map.Entry<String, Object> entry : tableProperties.entrySet()) { 
				final String key = entry.getKey();
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
		}else if (resultActivity instanceof MonsterSelectionActivity) {
			final MonsterSelectionActivity selectionScreen = (MonsterSelectionActivity) resultActivity;
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
		if (resultActivity instanceof GameActivity) {
			GameActivity gameScreen = (GameActivity) resultActivity;
			gameScreen.handleLeave(name);
		}
	}

	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) {
		if (resultActivity instanceof GameActivity) {
			final GameActivity gameScreen = (GameActivity) resultActivity;
			for (Map.Entry<String, Object> entry : event.getProperties().entrySet()) { 
				final String key = entry.getKey();
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
		}else if(resultActivity instanceof MonsterSelectionActivity){
			final MonsterSelectionActivity selectionScreen = (MonsterSelectionActivity) resultActivity;
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
		
		if (resultActivity instanceof MonsterSelectionActivity) {
			final MonsterSelectionActivity selectionScreeen = (MonsterSelectionActivity) resultActivity;
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
		
		if (resultActivity instanceof GameActivity) {
			final GameActivity gameScreen = (GameActivity) resultActivity;
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
		
		if (resultActivity instanceof MonsterSelectionActivity) {
			final MonsterSelectionActivity selectionScreen = (MonsterSelectionActivity) resultActivity;
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

	@Override
	public void onGameStarted(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameStopped(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
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
	public void onNextTurnRequest(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrivateUpdateReceived(String arg0, byte[] arg1, boolean arg2) {
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
