package appwarp.example.chatdemo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
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
		showMainView();
	}
	
	public void onWithClicked(View view){
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra("isWithout", false);
		intent.putExtra("topic", spinnerTopic.getSelectedItem().toString());
		startActivity(intent);
		showMainView();
	}
	
	private void showMatchMakingOption(){
		descText.setVisibility(View.GONE);
		nameEditText.setVisibility(View.GONE);
		connectToAppwarp.setVisibility(View.GONE);
		withoutBtn.setVisibility(View.VISIBLE);
		withBtn.setVisibility(View.VISIBLE);
		spinnerTopic.setVisibility(View.VISIBLE);
	}
	
	private void showMainView(){
		descText.setVisibility(View.VISIBLE);
		nameEditText.setVisibility(View.VISIBLE);
		connectToAppwarp.setVisibility(View.VISIBLE);
		withoutBtn.setVisibility(View.GONE);
		withBtn.setVisibility(View.GONE);
		spinnerTopic.setVisibility(View.GONE);
	}
	
	private void init(){
		WarpClient.initialize(Constants.apiKey, Constants.secretKey);
        try {
            theClient = WarpClient.getInstance();
            WarpClient.enableTrace(true);
        } catch (Exception ex) {
            Toast.makeText(this, "Exception in Initilization", Toast.LENGTH_LONG).show();
        }
        theClient.addConnectionRequestListener(this);
	}
	@Override
	public void onConnectDone(final ConnectEvent event) {
		Log.d("OnConnectDone", ""+event.getResult());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(progressDialog!=null){
					progressDialog.dismiss();
					progressDialog=null;
				}
				if(event.getResult()==WarpResponseResultCode.SUCCESS){
					Toast.makeText(MainActivity.this, "Connection Success", Toast.LENGTH_SHORT).show();
					showMatchMakingOption();
				}else{
					Toast.makeText(MainActivity.this, "Connection Failed"+event.getResult(), Toast.LENGTH_SHORT).show(); 
				}
			}
		});
	}
	
	@Override
	public void onDisconnectDone(ConnectEvent event) {
		
	}

	@Override
	public void onInitUDPDone(byte arg0) {
		
		
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		if(theClient!=null){
			theClient.removeConnectionRequestListener(this);
		}
	}
	
	
}
