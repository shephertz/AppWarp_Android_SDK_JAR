package com.turnbased.appwarp.longestline;

import android.util.Log;
import android.view.View;

import com.sample.turnbasedtictactoe.R;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

public class Utilities {

    private static WarpClient theClient = null;
    
    public static String game_room_id = "";
    public static boolean isLocalPlayerX = true;

    public static String localUsername = "";
    
    public static final String API_KEY = "Your AppWarp API Key";
    public static final String SECRET_KEY = "Your AppWarp Secret Key";
    
    public static final String EMPTY_BOARD_STATE = "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    
    enum GameState
    {
        ACTIVE,
        DRAWN,
        LOCAL_WON,
        LOCAL_LOST
    }
    
    enum Sequence
    {
        ROW,
        COLUMN,
        L2R,
        R2L
    }
    
    public static WarpClient getWarpClient(){
        if(theClient == null){
            try {             
                WarpClient.initialize(API_KEY, SECRET_KEY, "50.112.253.86");
                WarpClient.enableTrace(true);
                theClient = WarpClient.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            };
        }
        return theClient;
    }
    
    public static int getViewIdFromIndext(int index)
    {
        switch(index)
        {
        case 0:
            return R.id.cell_00;
        case 1:
            return R.id.cell_01;
        case 2:
            return R.id.cell_02;
        case 3:
            return R.id.cell_03;
        case 4:
            return R.id.cell_04;
        case 5:
            return R.id.cell_05;
        case 6:
            return R.id.cell_10;
        case 7:
            return R.id.cell_11;
        case 8:
            return R.id.cell_12;
        case 9:
            return R.id.cell_13;
        case 10:
            return R.id.cell_14;
        case 11:
            return R.id.cell_15;
        case 12:
            return R.id.cell_20;
        case 13:
            return R.id.cell_21;
        case 14:
            return R.id.cell_22;
        case 15:
            return R.id.cell_23;
        case 16:
            return R.id.cell_24;
        case 17:
            return R.id.cell_25;
        case 18:
            return R.id.cell_30;
        case 19:
            return R.id.cell_31;
        case 20:
            return R.id.cell_32;
        case 21:
            return R.id.cell_33;
        case 22:
            return R.id.cell_34;
        case 23:
            return R.id.cell_35;
        case 24:
            return R.id.cell_40;
        case 25:
            return R.id.cell_41;            
        case 26:
            return R.id.cell_42;
        case 27:
            return R.id.cell_43;          
        case 28:
            return R.id.cell_44;
        case 29:
            return R.id.cell_45;
        case 30:
            return R.id.cell_50;
        case 31:
            return R.id.cell_51;
        case 32:
            return R.id.cell_52;
        case 33:
            return R.id.cell_53;            
        case 34:
            return R.id.cell_54;
        case 35:
            return R.id.cell_55;            
        }
        return 0;
    }
    
    public static int getCellIndexFromViewId(int viewId)
    {
        switch (viewId)
        {
        case R.id.cell_00 :
            return 0;
        case R.id.cell_01 :
            return 1;
        case R.id.cell_02 :
            return 2;
        case R.id.cell_03 :
            return 3;
        case R.id.cell_04 :
            return 4;
        case R.id.cell_05 :
            return 5;     
            
        case R.id.cell_10 :
            return 6;
        case R.id.cell_11 :
            return 7;
        case R.id.cell_12 :
            return 8;
        case R.id.cell_13 :
            return 9;
        case R.id.cell_14 :
            return 10;
        case R.id.cell_15 :
            return 11; 
            
        case R.id.cell_20 :
            return 12;
        case R.id.cell_21 :
            return 13;
        case R.id.cell_22 :
            return 14;
        case R.id.cell_23 :
            return 15;
        case R.id.cell_24 :
            return 16;
        case R.id.cell_25 :
            return 17;    
            
        case R.id.cell_30 :
            return 18;
        case R.id.cell_31 :
            return 19;
        case R.id.cell_32 :
            return 20;
        case R.id.cell_33 :
            return 21;
        case R.id.cell_34 :
            return 22;
        case R.id.cell_35 :
            return 23;  
            
        case R.id.cell_40 :
            return 24;
        case R.id.cell_41 :
            return 25;
        case R.id.cell_42 :
            return 26;
        case R.id.cell_43 :
            return 27;
        case R.id.cell_44 :
            return 28;
        case R.id.cell_45 :
            return 29;   
            
        case R.id.cell_50 :
            return 30;
        case R.id.cell_51 :
            return 31;
        case R.id.cell_52 :
            return 32;
        case R.id.cell_53 :
            return 33;
        case R.id.cell_54 :
            return 34;
        case R.id.cell_55 :
            return 35;                           
        }
        return 0;
    }    
    
    private static boolean areCharsEqual(char c1, char c2, char c3)
    {
        if(c1 == c2 && c2==c3 && c1!='e')
        {
            return true;
        }
        return false;
    }
    
    public static GameState getState(String boardState, Result retCrossRes, Result retCircleRes)
    {
        if(!isGameOver(boardState)){
            return GameState.ACTIVE;
        }
        
        Result longestCrossRes = new Result(0,0,0,Sequence.COLUMN);
        Result longestCircleRes = new Result(0,0,0,Sequence.COLUMN);   
       
        Result val;
        for(int i=0; i<=5; i++){            
            
            // check rows
            val = getLongestRowSequence(i, 'x', boardState);
            longestCrossRes = (longestCrossRes.length < val.length) ? val : longestCrossRes;
            
            val = getLongestRowSequence(i, 'o', boardState);
            longestCircleRes = (longestCircleRes.length < val.length) ? val : longestCircleRes;
            
            // check columns
            val = getLongestColumnSequence(i, 'x', boardState);
            longestCrossRes = (longestCrossRes.length < val.length) ? val : longestCrossRes;
            
            val = getLongestColumnSequence(i, 'o', boardState);
            longestCircleRes = (longestCircleRes.length < val.length) ? val : longestCircleRes;            
        }
        
        // check diagonals
        val = getLongestDiagonal('x', boardState);
        longestCrossRes = (longestCrossRes.length < val.length) ? val : longestCrossRes;
        
        val = getLongestDiagonal('o', boardState);
        longestCircleRes = (longestCircleRes.length < val.length) ? val : longestCircleRes;
        
        Log.d("AppWarpTrace", "longestCircleRes "+longestCircleRes.length+" " + longestCircleRes.begin+" "+ longestCircleRes.end + " "+longestCircleRes.order);
        Log.d("AppWarpTrace", "longestCrossRes "+longestCrossRes.length+" " + longestCrossRes.begin+" "+longestCrossRes.end+" "+longestCrossRes.order);
        
        retCrossRes.begin = longestCrossRes.begin;
        retCrossRes.end = longestCrossRes.end;
        retCrossRes.length = longestCrossRes.length;
        retCrossRes.order = longestCrossRes.order;
        retCircleRes.begin = longestCircleRes.begin;
        retCircleRes.end = longestCircleRes.end;
        retCircleRes.length = longestCircleRes.length;
        retCircleRes.order = longestCircleRes.order;        
        
        if(longestCircleRes.length == longestCrossRes.length){
            return GameState.DRAWN;
        }
        if(((longestCircleRes.length > longestCrossRes.length) && !isLocalPlayerX) || ((longestCircleRes.length < longestCrossRes.length) && isLocalPlayerX)){
            return GameState.LOCAL_WON;
        }
        else{
            return GameState.LOCAL_LOST;
        }
    }
    
    private static Result getLongestDiagonal(char c, String boardState){
        int longest = 0;
        int longestBegin = 0;
        int longestEnd = 0;
        Sequence best = Sequence.COLUMN;
        int val;
        val = getLongestL2RDiagonal(24, 31, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 24;
            longestEnd = 31;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(18, 32, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 18;
            longestEnd = 32;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(12, 33, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 12;
            longestEnd = 33;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(6, 34, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 6;
            longestEnd = 34;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(0, 35, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 0;
            longestEnd = 35;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(1, 29, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 1;
            longestEnd = 29;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(2, 23, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 2;
            longestEnd = 23;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(3, 17, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 3;
            longestEnd = 17;
            best = Sequence.L2R;
        }
        
        val = getLongestL2RDiagonal(4, 11, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 4;
            longestEnd = 11;
            best = Sequence.L2R;
        }
        
        // 
        // R2L
        //
        val = getLongestR2LDiagonal(1, 6, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 1;
            longestEnd = 6;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(2, 12, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 2;
            longestEnd = 12;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(3, 18, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 3;
            longestEnd = 18;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(4, 24, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 4;
            longestEnd = 24;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(5, 30, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 5;
            longestEnd = 30;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(11, 31, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 11;
            longestEnd = 31;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(17, 32, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 17;
            longestEnd = 32;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(23, 33, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 23;
            longestEnd = 33;
            best = Sequence.R2L;
        }
        
        val = getLongestR2LDiagonal(29, 34, c, boardState);
        if(longest < val){
            longest = val;
            longestBegin = 29;
            longestEnd = 34;
            best = Sequence.R2L;
        }

        return new Result(longest, longestBegin, longestEnd, best);
    }    
    
    private static int getLongestR2LDiagonal(int begin, int end, char c, String boardState)
    {
        int longest = 0;
        int current = 0;
        for(int i=begin; i<=end; i+=5){
            if(boardState.charAt(i) == c){
                current++;
            }
            else{
                if(current > longest){
                    longest = current;
                }
                current = 0;
            }            
        }
        if(current > longest){
            longest = current;
        }
        return longest;
    }
    
    private static int getLongestL2RDiagonal(int begin, int end, char c, String boardState)
    {
        int longest = 0;
        int current = 0;
        for(int i=begin; i<=end; i+=7){
            if(boardState.charAt(i) == c){
                current++;
            }
            else{
                if(current > longest){
                    longest = current;
                }
                current = 0;
            }            
        }
        if(current > longest){
            longest = current;
        }
        return longest;
    }
    
    /*
     * row will be 0 to 5
     * c is either 'x' or 'o'
     */
    private static Result getLongestRowSequence(int row, char c, String boardState)
    {
        int start = row*6;
        int longest = 0;
        int current = 0;
        for(int i=start; i<start+6; i++){
            if(boardState.charAt(i) == c){
                current++;
            }
            else{
                if(current > longest){
                    longest = current;
                }
                current = 0;
            }
        }
        if(current > longest){
            longest = current;
        }        
        return new Result(longest, start, start+5, Sequence.ROW);
    }

    /*
     * row will be 0 to 5
     * c is either 'x' or 'o'
     */
    private static Result getLongestColumnSequence(int column, char c, String boardState)
    {
        int start = column;
        int longestLen = 0;
        int current = 0;
        for(int i=start; i<start+36; i+=6){
            if(boardState.charAt(i) == c){
                current++;
            }
            else{
                if(current > longestLen){
                    longestLen = current;
                }
                current = 0;
            }
        }
        if(current > longestLen){
            longestLen = current;
        }         
        return new Result(longestLen, start, start+30, Sequence.COLUMN);
    }
    
    public static int getDrawableId(String state, int index){
        char piece = state.charAt(index);
        if(piece == 'e'){
            return R.drawable.empty_cell;
        }
        else if(piece == 'o'){
            return R.drawable.circle_cell;
        }
        else{
            return R.drawable.cross_cell;
        }        
    }    
    
    public static boolean isGameOver(String boardState){
        return (boardState.indexOf('e') < 0);
    }

    public static String getNewBoardState(String currentState, int selectedCell, char piece) {
        return currentState.substring(0, selectedCell) + piece + currentState.substring(selectedCell+1);
    }
}
