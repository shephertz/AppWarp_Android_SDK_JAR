package com.turnbased.appwarp.longestline;

import com.sample.turnbasedtictactoe.R;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.turnbased.appwarp.longestline.Utilities.GameState;
import com.turnbased.appwarp.longestline.Utilities.Sequence;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements RoomRequestListener {

    private TextView turnText;
    private Handler UIThreadHandler = new Handler();
    private String boardState = Utilities.EMPTY_BOARD_STATE;
    private boolean isLocalTurn = false;
    private GameMessenger messenger;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        turnText = (TextView) findViewById(R.id.turn_player_name);
        messenger = new GameMessenger(this);        
    }
    
    @Override
    public void onStart(){
        super.onStart();
        messenger.start();
        Utilities.getWarpClient().addRoomRequestListener(this); 
        Utilities.getWarpClient().getLiveRoomInfo(Utilities.game_room_id);
        if(Utilities.isLocalPlayerX){
            isLocalTurn = true;
            turnText.setText("Next Turn "+Utilities.localUsername);
        }
    }
    
    @Override
    public void onStop(){
        super.onStop();
        this.finish();
        messenger.stop();
        Utilities.getWarpClient().removeRoomRequestListener(this);
    }
    
    public void onCellClicked(View view) {
        
        if(!isLocalTurn){
            return;
        }
        char piece = Utilities.isLocalPlayerX ? 'x' : 'o';
        int selectedCell = Utilities.getCellIndexFromViewId(view.getId());
        if(boardState.charAt(selectedCell) != 'e'){
            return;
        }
        if(Utilities.isLocalPlayerX){
            ((ImageButton) view).setImageResource(R.drawable.cross_cell);
        }
        else{
            ((ImageButton) view).setImageResource(R.drawable.circle_cell);
        }
                
        boardState = Utilities.getNewBoardState(boardState, selectedCell, piece);
        messenger.sendMove(boardState);
    }   
    
    // refresh the UI from the boardState
    private void updateUI()
    {
        
        uptadeCell(R.id.cell_00);
        uptadeCell(R.id.cell_01);
        uptadeCell(R.id.cell_02);
        uptadeCell(R.id.cell_03);
        uptadeCell(R.id.cell_04);
        uptadeCell(R.id.cell_05);
        
        uptadeCell(R.id.cell_10);
        uptadeCell(R.id.cell_11);
        uptadeCell(R.id.cell_12);
        uptadeCell(R.id.cell_13);
        uptadeCell(R.id.cell_14);
        uptadeCell(R.id.cell_15);
        
        uptadeCell(R.id.cell_20);
        uptadeCell(R.id.cell_21);
        uptadeCell(R.id.cell_22);
        uptadeCell(R.id.cell_23);
        uptadeCell(R.id.cell_24);
        uptadeCell(R.id.cell_25);
        
        uptadeCell(R.id.cell_30);
        uptadeCell(R.id.cell_31);
        uptadeCell(R.id.cell_32);
        uptadeCell(R.id.cell_33);
        uptadeCell(R.id.cell_34);
        uptadeCell(R.id.cell_35);
        
        uptadeCell(R.id.cell_40);
        uptadeCell(R.id.cell_41);
        uptadeCell(R.id.cell_42);
        uptadeCell(R.id.cell_43);
        uptadeCell(R.id.cell_44);
        uptadeCell(R.id.cell_45);
        
        uptadeCell(R.id.cell_50);
        uptadeCell(R.id.cell_51);
        uptadeCell(R.id.cell_52);
        uptadeCell(R.id.cell_53);
        uptadeCell(R.id.cell_54);
        uptadeCell(R.id.cell_55);        
    }   

    private void uptadeCell(int id){
        ((ImageButton)findViewById(id)).setImageResource(Utilities.getDrawableId(boardState, Utilities.getCellIndexFromViewId(id)));
    }
    
    private void highlightIndex(int index)
    {
        ImageButton v = (ImageButton) findViewById(Utilities.getViewIdFromIndext(index));
        if(boardState.charAt(index) == 'x')
            v.setImageResource(R.drawable.cross_cell_hl);
        else
            v.setImageResource(R.drawable.circle_cell_hl);
    }
    
    public void onMoveCompleted(final MoveEvent evt) {
        if(evt.getNextTurn().equals(Utilities.localUsername)){                    
            isLocalTurn = true;                    
        }
        else{
            isLocalTurn = false;
        }
        
        UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {               
                if(evt.getMoveData().length() > 0){
                    boardState = evt.getMoveData();
                    Result longestCrossRes = new Result(0,0,0,Sequence.COLUMN);
                    Result longestCircleRes = new Result(0,0,0,Sequence.COLUMN); 
                    GameState state = Utilities.getState(boardState, longestCrossRes, longestCircleRes);
                    Log.d("AppWarpTrace", "state is "+state+" isLocalPlayerX"+Utilities.isLocalPlayerX);
                    updateUI();
                    if(state == GameState.ACTIVE){
                        turnText.setText("Next Turn "+evt.getNextTurn());
                    }
                    else if(state == GameState.DRAWN){
                        handleDraw();
                    }
                    else if(state == GameState.LOCAL_LOST){
                        handleLocalLoss();
                        highlightWinner(longestCrossRes.length > longestCircleRes.length ? longestCrossRes : longestCircleRes);
                    }
                    else{
                        handleLocalWin();
                        highlightWinner(longestCrossRes.length > longestCircleRes.length ? longestCrossRes : longestCircleRes);                        
                    }
                }
                else{
                    turnText.setText("Next Turn "+evt.getNextTurn());                    
                    handleTurnExpired(evt.getSender());
                }                
            }
        });        
    }
    
    public void onGameStarted(final String nextTurn){
        if(nextTurn.equals(Utilities.localUsername)){                    
            isLocalTurn = true;                    
        }
        else{
            isLocalTurn = false;
        }       
        UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {   
                turnText.setText("Next Turn "+nextTurn);
            }
        });        
    }

    private void handleTurnExpired(String expiredUser) {
        Toast.makeText(this, expiredUser+" turn expired", Toast.LENGTH_SHORT).show();
    }
    
    private void highlightWinner(Result winner)
    {        
        int index = winner.begin;
        if(winner.order == Sequence.COLUMN){
            while(index <= winner.end){
                highlightIndex(index);
                index += 6;
            }
        }
        else if(winner.order == Sequence.ROW){
            while(index <= winner.end){
                highlightIndex(index);
                index += 1;
            }           
        }
        else if(winner.order == Sequence.L2R){
            while(index <= winner.end){
                highlightIndex(index);
                index += 7;
            }
        }
        else{
            while(index <= winner.end){
                highlightIndex(index);
                index += 5;
            }            
        }
    }
    
    private void handleLocalWin(){
        Utilities.getWarpClient().deleteRoom(Utilities.game_room_id);
        Toast.makeText(this, "you won!", Toast.LENGTH_SHORT).show();
        Utilities.isLocalPlayerX = isLocalTurn;
        GameActivity.this.finish();
    }
    
    private void handleLocalLoss(){
        Toast.makeText(this, "you lost!", Toast.LENGTH_LONG).show();
        Utilities.isLocalPlayerX = isLocalTurn;        
        GameActivity.this.finish();
    }
    
    private void handleDraw(){
        Toast.makeText(this, "draw!", Toast.LENGTH_LONG).show();
        Utilities.isLocalPlayerX = isLocalTurn;
        GameActivity.this.finish();
    }
    
    void handleRemoteLeft(){        
        Utilities.getWarpClient().deleteRoom(Utilities.game_room_id);        
        UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {   
                Toast.makeText(GameActivity.this, "your opponent left. Try again", Toast.LENGTH_SHORT).show();                 
                GameActivity.this.finish();
            }
        });
    }

    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent evt) {        
        int numUsers = evt.getJoinedUsers().length;
        Log.d("AppWarpTrace", "onGetLiveRoomInfoDone numUsers "+numUsers);
        if(numUsers > 1){
            Utilities.getWarpClient().startGame();
        }
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
    public void onLockPropertiesDone(byte arg0) {
        // TODO Auto-generated method stub
        
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
    public void onUnlockPropertiesDone(byte arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
        // TODO Auto-generated method stub
        
    }

}
