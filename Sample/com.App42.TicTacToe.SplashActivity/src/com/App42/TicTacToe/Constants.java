package com.App42.TicTacToe;

public class Constants {
	
	// Update with your API and Secret key pair received while registering
	public static final String App42ApiKey = "";	
	public static final String App42ApiSecret = "";		
	///////////////////////////////////////////////////////
	
	public static final String SharedPrefUname = "logged_in_username";
	public static final String IntentUserName = "intentUserName";
	public static final String IntentGameRoomId = "intentGameRoomId";	
	
	public static final String GameFirstUserKey = "user_one";
	public static final String GameSecondUserKey = "user_two";
	public static final String GameStateKey = "state";
	public static final String GameBoardKey = "board";	
	public static final String GameWinnerKey = "winner";
	public static final String GameNextMoveKey = "next";
	public static final String GameEmptyBoard = "eeeeeeeee"; 
 
	public static final String GameStateIdle = "idle";
	public static final String GameStateActive = "active";
	public static final String GameStateFinished = "finished";
	
	public static final char BoardTileEmpty = 'e';
	public static final char BoardTileCross = 'x';
	public static final char BoardTileCircle = 'o';
	
	public static final int INVALID_SELECTION = -1;
	public static final int SPLASH_DISPLAY_TIME = 5000;
	
	public static final byte UpdateTrigger[] = {0};

}