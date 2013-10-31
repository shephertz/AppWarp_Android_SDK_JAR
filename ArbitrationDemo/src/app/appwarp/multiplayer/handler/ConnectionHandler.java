package app.appwarp.multiplayer.handler;

import android.app.Activity;
import android.util.Log;
import app.appwarp.multiplayer.MainActivity;
import app.appwarp.multiplayer.Utils;

import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class ConnectionHandler implements ConnectionRequestListener{

	private Activity resultActivity;
	private static ConnectionHandler connectionHandler;
	
	private ConnectionHandler(){
		
	}
	
	public static ConnectionHandler getInstance(){
		if(connectionHandler==null){
			connectionHandler = new ConnectionHandler();
		}
		return connectionHandler;
	}
	
	public void setResultActivity(Activity activity){
		this.resultActivity =  activity;
	}
	
	@Override
	public void onConnectDone(final ConnectEvent event) {
		Log.d("onConnectDone", event.getResult()+"");
		if (resultActivity instanceof MainActivity) {
			final MainActivity mainScreen = (MainActivity) resultActivity;
			mainScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(event.getResult()==0){// go to room  list 
						mainScreen.goToRoomSelectionScreen(true);
					}else{
						Utils.showToastAlert(mainScreen, "connection failed ");
						mainScreen.goToRoomSelectionScreen(false);
					}
				}
			});
		}
		
	}

	@Override
	public void onDisconnectDone(ConnectEvent event) {
		Log.d("onDisconnectDone", event.getResult()+"");
		
	}

	@Override
	public void onInitUDPDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
