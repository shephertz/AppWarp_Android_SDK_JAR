package com.example.matchmaking;


import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class MainActivity extends Activity implements ConnectionRequestListener{

	
	private Button withoutBtn;
	private Button withBtn;
	private Button connectToAppwarp;
	private EditText nameEditText;
	private TextView descText;
	private Spinner spinnerTopic;
	private WarpClient theClient;
	private ProgressDialog progressDialog;
    Handler handler = new Handler();
    int i=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		withoutBtn = (Button)findViewById(R.id.withoutBtn);
		withBtn = (Button)findViewById(R.id.withBtn);
		connectToAppwarp = (Button)findViewById(R.id.connect);
		descText = (TextView)findViewById(R.id.descText);
		spinnerTopic = (Spinner)findViewById(R.id.spinnerLevel);
		nameEditText = (EditText)findViewById(R.id.editTextName);
		
		withoutBtn.setVisibility(View.GONE);
		withBtn.setVisibility(View.GONE);
		spinnerTopic.setVisibility(View.GONE);
		init();
		i=0;
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(theClient!=null){
			theClient.removeConnectionRequestListener(this);
			theClient.disconnect();
		}
	}
	
	public void onConnectClicked(View view){
		
		String userName = nameEditText.getText().toString();
		if(userName.length()>0){
			Utils.USER_NAME  = userName;
			progressDialog = ProgressDialog.show(this, "", "Please wait...");
			progressDialog.setCancelable(true);
			theClient.addConnectionRequestListener(this);  
			theClient.connectWithUserName(userName);
		}else{
			Utils.showToast(this, "Please enter name");
		}
	}
	
	public void onWithoutClicked(View view){
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra("isWithout", true);
		intent.putExtra("topic", spinnerTopic.getSelectedItem().toString());
		startActivity(intent);
	}
	public void onWithClicked(View view){
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra("isWithout", false);
		intent.putExtra("topic", spinnerTopic.getSelectedItem().toString());
		startActivity(intent);
	}
	private void init(){
		WarpClient.initialize(Constants.apiKey, Constants.secretKey);
        try {
            theClient = WarpClient.getInstance();
            WarpClient.enableTrace(true);
            
        } catch (Exception ex) {
            Toast.makeText(this, "Exception in Initilization", Toast.LENGTH_LONG).show();
        }
	}
	@Override
	public void onConnectDone(final ConnectEvent event) {
		progressDialog.dismiss();
		if(event.getResult() == 0){
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					Log.d("OnConnectDone", "Success: "+event.getResult()+new Date());
					Toast.makeText(MainActivity.this, "connection success", Toast.LENGTH_SHORT).show();
					nameEditText.setVisibility(View.GONE);
					withoutBtn.setVisibility(View.VISIBLE);
					withBtn.setVisibility(View.VISIBLE);
					spinnerTopic.setVisibility(View.VISIBLE);
					connectToAppwarp.setVisibility(View.GONE);
					descText.setVisibility(View.GONE);
					
				}
			});
		}else{
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "connection failed "+event.getResult()+" state "+theClient.getConnectionState()+" "+i, Toast.LENGTH_SHORT).show(); 
					Log.d("OnConnectDone", "Failed: "+event.getResult()+new Date());
				}
			}, 5000);
		}
		
	}
	@Override
	public void onDisconnectDone(final ConnectEvent event) {
		
	}

	@Override
	public void onInitUDPDone(byte arg0) {
		
		
	}
	
}
