package appwarp.example.multiplayerdemo;

import java.util.HashMap;
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
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;


public class RoomListActivity extends Activity implements ZoneRequestListener {
	
	private WarpClient theClient;
	private RoomListAdapter roomlistAdapter;
	private ListView listView;
	private ProgressDialog progressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_list);
		listView = (ListView)findViewById(R.id.roomList);
		roomlistAdapter = new RoomListAdapter(this);
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
		theClient.getRoomInRange(1, 1);// trying to get room with at least one user
	}
	
	public void onStop(){
		super.onStop();
		theClient.removeZoneRequestListener(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		theClient.disconnect();
	}
	
	public void joinRoom(String roomId){
		if(roomId!=null && roomId.length()>0){
			goToGameScreen(roomId);
		}else{
			Log.d("joinRoom", "failed:"+roomId);
		}
	}
	
	public void onJoinNewRoomClicked(View view){
		progressDialog = ProgressDialog.show(this,"","Pleaes wait...");
		progressDialog.setCancelable(true);
		HashMap<String, Object> properties = new HashMap<String, Object>();
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
				if(progressDialog!=null){
					progressDialog.dismiss();
					progressDialog = null;
				}
				if(event.getResult()==WarpResponseResultCode.SUCCESS){// if room created successfully
					String roomId = event.getData().getId();
					joinRoom(roomId);
					Log.d("onCreateRoomDone", event.getResult()+" "+roomId);
				}else{
					Utils.showToastAlert(RoomListActivity.this, "Room creation failed...");
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
		
	}
	
	@Override
	public void onGetMatchedRoomsDone(final MatchedRoomsEvent event) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				RoomData[] roomDataList = event.getRoomsData();
				if(roomDataList!=null && roomDataList.length>0){
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
		
	}
	
	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
		
	}
	
	private void goToGameScreen(String roomId){
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("roomId", roomId);
		startActivity(intent);
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
