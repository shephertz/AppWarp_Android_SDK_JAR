package com.App42.TicTacToe;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
 
public class GameActivity extends Activity implements RoomRequestListener, NotifyListener{
 	
	private JSONObject gameObject;	// represents the custom Room data which contains the game info.
	private int selectedCell = Constants.INVALID_SELECTION;
	private String localUserName;
	private String remoteUserName;
	private int localUserCellImageId;
	private char localUserTile;
	private String currentState;
	private String nextTurn;
	private ProgressDialog progressDialog;
	private ImageButton selectedButton = null;
	private String gameRoomId;
	Handler UIThreadHandler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);						
		
        Intent intent = getIntent();
        localUserName = intent.getStringExtra(Constants.IntentUserName);
                        
		gameRoomId = intent.getStringExtra(Constants.IntentGameRoomId);
		
		AsyncApp42ServiceApi.getMyWarpClient().addRoomRequestListener(this);
		AsyncApp42ServiceApi.getMyWarpClient().addNotificationListener(this);	
				
		AsyncApp42ServiceApi.getMyWarpClient().subscribeRoom(gameRoomId);
	}
	
	private void updateUI()
	{        
		try {
			String u1Name = gameObject.getString(Constants.GameFirstUserKey);
	        String u2Name = gameObject.getString(Constants.GameSecondUserKey);
	        String winner = gameObject.getString(Constants.GameWinnerKey);
	        currentState = gameObject.getString(Constants.GameStateKey);
	        nextTurn = gameObject.getString(Constants.GameNextMoveKey);
	        if(u1Name.equals(localUserName) && u2Name.isEmpty()){
	        	progressDialog = ProgressDialog.show(this, "", "waiting for opponent");
	        }
	        if(u1Name.equalsIgnoreCase(localUserName))
	        {
	        	localUserCellImageId = R.drawable.cross_cell;
	        	localUserTile = Constants.BoardTileCross;
	        	remoteUserName = u2Name;
	        }
	        else
	        {
	        	localUserCellImageId = R.drawable.circle_cell;
	        	localUserTile = Constants.BoardTileCircle;
	        	remoteUserName = u1Name;
	        }
	        
			//((Button) this.findViewById(R.id.submit)).setClickable(false);
			
			this.drawImagesForBoard(gameObject.getString(Constants.GameBoardKey));	
			if(currentState.equals(Constants.GameStateFinished))
			{				
				if(u2Name.equalsIgnoreCase(localUserName)){
					((Button) this.findViewById(R.id.rematch)).setVisibility(View.VISIBLE);
				}
				//((Button) this.findViewById(R.id.submit)).setVisibility(View.INVISIBLE);
				if(winner.isEmpty())
				{
					((TextView) this.findViewById(R.id.status)).setText("Match Drawn!");
				}
				else if(winner.equalsIgnoreCase(localUserName))
				{
					((TextView) this.findViewById(R.id.status)).setText("You Won!");
				}
				else
				{
					((TextView) this.findViewById(R.id.status)).setText("You Lost :(");
				}
			}
			else
			{
				((Button) this.findViewById(R.id.rematch)).setVisibility(View.INVISIBLE);
				((TextView) this.findViewById(R.id.status)).setText("");
				if(nextTurn.equalsIgnoreCase(localUserName))
				{
					((TextView) this.findViewById(R.id.status)).setText("Your turn against " + remoteUserName);
					//((Button) this.findViewById(R.id.submit)).setVisibility(View.VISIBLE);
				}
				else
				{
					((TextView) this.findViewById(R.id.status)).setText("Waiting for "+remoteUserName+" to move");
					//((Button) this.findViewById(R.id.submit)).setVisibility(View.INVISIBLE);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 		
	}		
	
	@Override
	protected void onPause(){
		super.onPause();
		// tell opponent that I am leaving the room
		AsyncApp42ServiceApi.getMyWarpClient().leaveRoom(gameRoomId);
	}
	
	public void onRematchClicked(View view) {
		gameObject = Utilities.buildNewGameJSON(localUserName);
		try {
			gameObject.put(Constants.GameSecondUserKey, remoteUserName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AsyncApp42ServiceApi.getMyWarpClient().setCustomRoomData(gameRoomId, gameObject.toString());
		AsyncApp42ServiceApi.getMyWarpClient().sendUpdatePeers(Constants.UpdateTrigger);
	}
	
	public void onSubmitClicked(View view) {
		try {
			String boardState = gameObject.getString(Constants.GameBoardKey);
			String prefix = "";
			if(selectedCell > 0)
			{
				prefix = boardState.substring(0, selectedCell);
			}

			String suffix = "";
			if(selectedCell < 8)
			{
				suffix = boardState.substring(selectedCell+1);
			}
			
			String newBoardState = prefix+localUserTile+suffix;
			gameObject.put(Constants.GameBoardKey, newBoardState);
			gameObject.put(Constants.GameNextMoveKey, remoteUserName);
			if(isGameOver(newBoardState))
			{
				gameObject.put(Constants.GameStateKey, Constants.GameStateFinished);
				gameObject.put(Constants.GameWinnerKey, localUserName);
			}
			else if(isBoardFull(newBoardState))
			{
				gameObject.put(Constants.GameStateKey, Constants.GameStateFinished);
				gameObject.put(Constants.GameWinnerKey, "");
			}
			else
			{
				gameObject.put(Constants.GameStateKey, Constants.GameStateActive);
			}
			this.selectedCell = Constants.INVALID_SELECTION;
			this.selectedButton = null;
			AsyncApp42ServiceApi.getMyWarpClient().setCustomRoomData(gameRoomId, gameObject.toString());
			AsyncApp42ServiceApi.getMyWarpClient().sendUpdatePeers(Constants.UpdateTrigger);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isGameOver(String boardState)
	{
		// Check rows
		if(Utilities.areCharsEqual(boardState.charAt(0), boardState.charAt(1), boardState.charAt(2), localUserTile) ||
		   Utilities.areCharsEqual(boardState.charAt(3), boardState.charAt(4), boardState.charAt(5), localUserTile) ||
		   Utilities.areCharsEqual(boardState.charAt(6), boardState.charAt(7), boardState.charAt(8), localUserTile)
		   )
		{
			return true;
		}
		
		// Check columns
		if(Utilities.areCharsEqual(boardState.charAt(0), boardState.charAt(3), boardState.charAt(6), localUserTile) ||
		   Utilities.areCharsEqual(boardState.charAt(1), boardState.charAt(4), boardState.charAt(7), localUserTile) ||
		   Utilities.areCharsEqual(boardState.charAt(2), boardState.charAt(5), boardState.charAt(8), localUserTile)
		   )
		{
			return true;
		}		
		
		// Check diagonals
		if(Utilities.areCharsEqual(boardState.charAt(0), boardState.charAt(4), boardState.charAt(8), localUserTile) ||		   
		   Utilities.areCharsEqual(boardState.charAt(2), boardState.charAt(4), boardState.charAt(6), localUserTile)
		   )
		{
			return true;
		}		
		
		return false;
	}
	
	private boolean isBoardFull(String boardState)
	{
		for(int i=0; i<9; i++)
		{
			if(boardState.charAt(i) == Constants.BoardTileEmpty)
			{
				return false;
			}
		}
		
		return true;
	}		

	public void onCellClicked(View view) {
			
		//((Button) this.findViewById(R.id.submit)).setClickable(true);
		if(this.selectedButton != null)
		{
			this.selectedButton.setImageResource(R.drawable.empty_cell);
		}
		((ImageButton) view).setImageResource(localUserCellImageId);
		this.selectedButton = (ImageButton)view;
		this.selectedCell = getCellIndexFromView(view);		
		this.onSubmitClicked(null);
	}
	
	private int getCellIndexFromView(View view)
	{
		int viewId = view.getId();
		switch (viewId)
		{
		case R.id.cell_00 :
			return 0;
		case R.id.cell_01 :
			return 1;
		case R.id.cell_02 :
			return 2;
		case R.id.cell_10 :
			return 3;
		case R.id.cell_11 :
			return 4;
		case R.id.cell_12 :
			return 5;
		case R.id.cell_20 :
			return 6;
		case R.id.cell_21 :
			return 7;
		case R.id.cell_22 :
			return 8;			
		}
		return 0;
	}
	
	private void drawImagesForBoard(String boardState)
	{
		setupButton(boardState.charAt(0), (ImageButton) this.findViewById(R.id.cell_00));
		setupButton(boardState.charAt(1), (ImageButton) this.findViewById(R.id.cell_01));
		setupButton(boardState.charAt(2), (ImageButton) this.findViewById(R.id.cell_02));
		setupButton(boardState.charAt(3), (ImageButton) this.findViewById(R.id.cell_10));
		setupButton(boardState.charAt(4), (ImageButton) this.findViewById(R.id.cell_11));
		setupButton(boardState.charAt(5), (ImageButton) this.findViewById(R.id.cell_12));
		setupButton(boardState.charAt(6), (ImageButton) this.findViewById(R.id.cell_20));
		setupButton(boardState.charAt(7), (ImageButton) this.findViewById(R.id.cell_21));
		setupButton(boardState.charAt(8), (ImageButton) this.findViewById(R.id.cell_22));
		
	}
	
	private void setupButton(char boardTile, ImageButton button)
	{
		if(boardTile == Constants.BoardTileEmpty)
		{
			if(!currentState.equals(Constants.GameStateFinished) && nextTurn.equals(localUserName))
			{
				button.setClickable(true);
			}
			else
			{
				button.setClickable(false);
			}
			button.setImageResource(R.drawable.empty_cell);
		}
		else if(boardTile == Constants.BoardTileCircle)
		{
			button.setClickable(false);
			button.setImageResource(R.drawable.circle_cell);
		}
		else
		{
			button.setClickable(false);
			button.setImageResource(R.drawable.cross_cell);
		}
	}


	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent arg0) {
		
		if(arg0.getResult() != WarpResponseResultCode.SUCCESS){
			return;
		}
		
		// callback is not the main UI thread. So post message to UI thread
		// through its handler. Android UI elements can't be accessed from
		// non-UI threads.
		UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {      
        		try {			
        			String customData = arg0.getCustomData();
        			String roomName = arg0.getData().getName();
        			((TextView) GameActivity.this.findViewById(R.id.room_info)).setText("Room: " + roomName);
        			
        			if(customData == null || customData.length() == 0){
        				gameObject = Utilities.buildNewGameJSON(localUserName);
        				AsyncApp42ServiceApi.getMyWarpClient().setCustomRoomData(gameRoomId, gameObject.toString());
        			}
        			else{
        				gameObject = new JSONObject(arg0.getCustomData());
        			}
        			updateUI();
        		}
        		catch (JSONException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}            	
            }
        });				
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
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
		if(arg0.getResult() == WarpResponseResultCode.SUCCESS){
			AsyncApp42ServiceApi.getMyWarpClient().getLiveRoomInfo(gameRoomId);
		}
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChatReceived(ChatEvent arg0) {
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
		AsyncApp42ServiceApi.getMyWarpClient().getLiveRoomInfo(gameRoomId);		
	}

	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserJoinedRoom(final RoomData arg0, final String arg1) {
		
		// callback is not the main UI thread. So post message to UI thread
		// through its handler. Android UI elements can't be accessed from
		// non-UI threads.		
		UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {    
            	if(progressDialog != null ){
            		progressDialog.dismiss(); 
            	}
            	remoteUserName = arg1;
            	((TextView) GameActivity.this.findViewById(R.id.status)).setText("Your turn against " + remoteUserName);
            	try {
					gameObject.put(Constants.GameSecondUserKey, remoteUserName);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });						
		
		
	}

	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLeftRoom(final RoomData arg0, final String arg1) {
		// oh looks like my opponent has left :-(
		
		// callback is not the main UI thread. So post message to UI thread
		// through its handler. Android UI elements can't be accessed from
		// non-UI threads.
		UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {      
        		if(arg1.equals(remoteUserName)){					
					gameObject = Utilities.buildNewGameJSON(localUserName);
					AsyncApp42ServiceApi.getMyWarpClient().setCustomRoomData(gameRoomId, gameObject.toString());
					GameActivity.this.updateUI();								
				}            	
            }
        });
		
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
	public void onMoveCompleted(MoveEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNextTurnRequest(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrivateChatReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrivateUpdateReceived(String arg0, byte[] arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			HashMap<String, Object> arg2, HashMap<String, String> arg3) {
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
 
}