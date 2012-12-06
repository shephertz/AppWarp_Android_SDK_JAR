package com.App42.TicTacToe;

import android.os.Handler;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;

public class AsyncApp42ServiceApi {
		
	private UserService userService;
	private static WarpClient warpClient = null;	
		
	private static AsyncApp42ServiceApi mInstance = null;
	
	private AsyncApp42ServiceApi(){
		// initialize the singletons.
    	ServiceAPI sp = new ServiceAPI(Constants.App42ApiKey, Constants.App42ApiSecret);
    	WarpClient.initialize(Constants.App42ApiKey, Constants.App42ApiSecret);
    	this.userService = sp.buildUserService();    	
	}	
	
	public static WarpClient getMyWarpClient(){
		if(warpClient == null){
			try {
				warpClient = WarpClient.getInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return warpClient;
	}
	
	public static AsyncApp42ServiceApi instance()
	{
		if(mInstance == null)
		{
			mInstance = new AsyncApp42ServiceApi();
		}
		
		return mInstance;
	}
	
	// Used to send out a new user registration request
	public void createUser(final String name, final String pswd, final String email, final App42ServiceListener callBack){
		final Handler callerThreadHandler = new Handler();
        new Thread(){
            @Override public void run() {
                try {                      	
            		final User user = userService.createUser(name, pswd, email);      
            		// callback is not the main UI thread. So post message to UI thread
            		// through its handler. Android UI elements can't be accessed from
            		// non-UI threads.
            		callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {      
                        	callBack.onUserCreated(user);
                        }
                    });                                 		
                }  
            	catch (App42Exception ex) {
            		System.out.println(ex.toString());    	
        			callBack.onUserCreated(null);
            	}
            }
        }.start();
	}

	// used to authenticate the user
	public void authenticateUser(final String name, final String pswd, final App42ServiceListener callBack){
		final Handler callerThreadHandler = new Handler();
        new Thread(){
            @Override public void run() {
                try {                        
            		final App42Response response = userService.authenticate(name, pswd);
            		// callback is not the main UI thread. So post message to UI thread
            		// through its handler. Android UI elements can't be accessed from
            		// non-UI threads.
            		callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {      
                        	callBack.onUserAuthenticated(response);
                        }            			
            		});            		
                }  
            	catch (App42Exception ex) {
            		System.out.println(ex.toString());    
            		callBack.onUserAuthenticated(null);
            	}
            }
        }.start();
	}

	// callback interface.
	public static interface App42ServiceListener {
		public void onUserCreated(User response);		
		public void onUserAuthenticated(App42Response response);
	}
}