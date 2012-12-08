package com.dave.player;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionActivity extends Activity {
	HashMap<String, Object> data;
	Button playButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = (HashMap<String, Object>) getIntent().getSerializableExtra(
				"info");
		setContentView(R.layout.option_layout);
		playButton = (Button) findViewById(R.id.play_button);
		playButton.setTag(data);
	}

	public void getDetail(View view) {
		// (HashMap<String, Object>) view.getTag();
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("info", data);
		startActivity(intent);
		finish();
	}

	public void closeApplication(View view) {
		System.exit(0);
	}

	public void playMP3(View v) {
		Intent i = new Intent(this, PlayerActivity.class);
		i.putExtra("info", (HashMap<String, Object>) (v.getTag()));
		startActivity(i);
		finish();
	}
}
