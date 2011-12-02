package com.pyrobot.droid;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ModeSelect extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.mode_select);
		setUpButtons();
	}
	private void setUpButtons() {
		Button client_button = (Button) findViewById(R.id.RobotClientBtn);
		client_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent client_intent = new Intent(ModeSelect.this, RobotClient.class);
				startActivity(client_intent);
			}
		});
		Button server_button = (Button) findViewById(R.id.RobotServerBtn);
		server_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent server_intent = new Intent(ModeSelect.this, RobotServer.class);
				startActivity(server_intent);
			}
		});
	}
	
}
