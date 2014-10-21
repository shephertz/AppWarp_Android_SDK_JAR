package com.App42.TicTacToe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.user.User;

public class MainActivity extends Activity implements AsyncApp42ServiceApi.App42ServiceListener, ConnectionRequestListener{
		
	private AsyncApp42ServiceApi asyncService; 
	private EditText userName;
	private EditText password;
	private EditText emailid;
	private SharedPreferences mPrefs;
	private ProgressDialog progressDialog;		
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);            
        userName = (EditText) this.findViewById(R.id.uname);
        password = (EditText) this.findViewById(R.id.pswd);
        emailid = (EditText) this.findViewById(R.id.email);       
        
        // Check if we can use saved creds
        mPrefs = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
        asyncService = AsyncApp42ServiceApi.instance();
        String loggedInName = mPrefs.getString(Constants.SharedPrefUname, null);        
        if(loggedInName != null && !loggedInName.isEmpty())
        {
    		// now connect and join Warp Server 
        	userName.setText(loggedInName);
    		AsyncApp42ServiceApi.getMyWarpClient().addConnectionRequestListener(this);
    		AsyncApp42ServiceApi.getMyWarpClient().connectWithUserName(userName.getText().toString());    	
        }        
    }    
    
    public void onSaveInstanceState(Bundle outState)
    {
    	super.onSaveInstanceState(outState);
    }
    
    public void onStart()
    {
    	super.onStart();    	    							
    }
    
	 public void onSigninClicked(View view) {
		 progressDialog = ProgressDialog.show(this, "", "signing in..");
		 asyncService.authenticateUser(userName.getText().toString(), password.getText().toString(), this);
	 }
	 
	 public void onRegisterClicked(View view) {
		 progressDialog = ProgressDialog.show(this, "", "registering..");
		 asyncService.createUser(userName.getText().toString(), password.getText().toString(), emailid.getText().toString(), this);
	 }
	 
	private void saveCreds()
	{
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(Constants.SharedPrefUname, userName.getText().toString());
		editor.commit();		
	}
	
    private void gotoHomeActivity(String signedInUserName)
    {		
        //Finish the activity so it can't be returned to.
        this.finish();
        // Create an Intent that will start the home activity.
        Intent mainIntent = new Intent(this, UserHomeActivity.class);
        mainIntent.putExtra(Constants.IntentUserName, signedInUserName);
        this.startActivity(mainIntent);    	    
    }
        
	@Override
	public void onUserCreated(final User user) {    	
    	if(user != null)
    	{
    		progressDialog.dismiss();
    		System.out.println(user.getUserName());
    		System.out.println(user.getEmail());
    		saveCreds();
    		// now connect and join Warp Server    		
    		AsyncApp42ServiceApi.getMyWarpClient().addConnectionRequestListener(this);
    		AsyncApp42ServiceApi.getMyWarpClient().connectWithUserName(userName.getText().toString());
    	} 
    	else{
    		progressDialog.dismiss();
    	}
	}
    
	@Override
	public void onUserAuthenticated(final App42Response response) {
  
    	if(response != null)
    	{
    		progressDialog.dismiss();
    		System.out.println(response.toString());
    		saveCreds();
    		// now connect and join Warp Server    		
    		AsyncApp42ServiceApi.getMyWarpClient().addConnectionRequestListener(this);
    		AsyncApp42ServiceApi.getMyWarpClient().connectWithUserName(userName.getText().toString());
    	}
    	else{
    		progressDialog.dismiss();
    	}
		
	}

	/*
	 * Warp connection listener callback
	 * 
	 */
	@Override
	public void onConnectDone(ConnectEvent arg0) {
		if(arg0.getResult() == WarpResponseResultCode.SUCCESS){
			// yay! we are connected to Warp server
			gotoHomeActivity(userName.getText().toString());
		}		
	}

	@Override
	public void onDisconnectDone(ConnectEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitUDPDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
}

