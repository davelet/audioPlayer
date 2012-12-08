package com.dave.player;

import java.sql.Date;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends Activity {
	private TextView song_title;
	private TextView song_artist;
	private TextView song_composer;
	private TextView song_album;
	private TextView song_date;
	private TextView song_time;
	private TextView song_size;
	private TextView song_path;
	private TextView song_mime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		HashMap<String, Object> data = (HashMap<String, Object>) getIntent()
				.getSerializableExtra("info");
		song_title = (TextView) findViewById(R.id.song_name);
		song_time = (TextView) findViewById(R.id.time);
		song_artist = (TextView) findViewById(R.id.singer);
		song_composer = (TextView) findViewById(R.id.composer);
		song_album = (TextView) findViewById(R.id.album);
		song_size = (TextView) findViewById(R.id.size);
		song_date = (TextView) findViewById(R.id.date);
		song_path = (TextView) findViewById(R.id.path);
		song_mime = (TextView) findViewById(R.id.mime_type);

		song_title.setText((String) data.get("title"));
		song_time.setText((String) data.get("time"));
		song_artist.setText((String) data.get("singer"));
		song_composer.setText((String) data.get("composer"));
		song_album.setText((String) data.get("album"));
		song_size.setText((String) data.get("size"));
		song_date.setText((String) data.get("date"));
		song_path.setText((String) data.get("path"));
		song_mime.setText((String) data.get("mime"));
	}
}
