package app.appwarp.multiplayer;

import java.util.HashMap;
import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import app.appwarp.multiplayer.handler.EventHandler;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;



public class RoomlistActivity extends Activity {
	
	private WarpClient theClient;
	private RoomlistAdapter roomlistAdapter;
	private ListView listView;
	private ProgressDialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_list);
		listView = (ListView)findViewById(R.id.roomList);
		roomlistAdapter = new RoomlistAdapter(this);
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
		EventHandler.getInstance().setResultActivity(this);
		progressDialog = ProgressDialog.show(this,"","Pleaes wait...");
		theClient.addZoneRequestListener(EventHandler.getInstance());
		theClient.addRoomRequestListener(EventHandler.getInstance());
		theClient.addNotificationListener(EventHandler.getInstance());
		theClient.getRoomInRange(1,1);// trying to get room with at least one user
	}
	public void onStop(){
		super.onStop();
		theClient.removeZoneRequestListener(EventHandler.getInstance());
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(theClient!=null){
			theClient.disconnect();
		}
	}
	
	public void onJoinNewRoomClicked(View view){
		progressDialog = ProgressDialog.show(this,"","Pleaes wait...");
		progressDialog.setCancelable(true);
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("red", "");
		properties.put("green", "");
		properties.put("blue", "");
		properties.put("yellow", "");
		theClient.createRoom(""+System.currentTimeMillis(), "Saurav", 4, properties);
	}
	
	public void onRoomCreated(String roomId) {
		progressDialog.dismiss();
		if( roomId!=null && roomId.length()>0 ){// if room created successfully
			goToSelectionScreen(roomId);
		}
	}
	
	
	
	public void onGetMatchedRooms(RoomData[] roomDataList) {
		progressDialog.dismiss();
		if(roomDataList!=null && roomDataList.length>0){
			roomlistAdapter.setData(roomDataList);
			listView.setAdapter(roomlistAdapter);
		}else{
			roomlistAdapter.clear();
		}
	}
	
	public void goToSelectionScreen(String roomId){
		Intent intent = new Intent(RoomlistActivity.this, MonsterSelectionActivity.class);
		intent.putExtra("roomId", roomId);
		startActivity(intent);
	}
}
