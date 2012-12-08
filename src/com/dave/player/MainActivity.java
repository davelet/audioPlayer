package com.dave.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	ListView musicListView;
	public static MediaPlayer mediaPlayer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		musicListView = (ListView) findViewById(R.id.list);
		getMusics();
	}

	private void getMusics() {
		final ProgressDialog dialog = new ProgressDialog(this);
		new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setTitle(getString(R.string.wait));
				dialog.setMessage(getString(R.string.looking));
				dialog.setIcon(android.R.drawable.ic_dialog_map);
				dialog.setIndeterminate(false);
				dialog.setCancelable(false);
				dialog.show();
			}

			@Override
			protected List<HashMap<String, Object>> doInBackground(
					Void... params) {
				List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
				ContentResolver contentResolver = MainActivity.this
						.getContentResolver();
				Cursor cursor = contentResolver.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
						null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
				cursor.moveToFirst();
				int counter = cursor.getCount();

				for (int j = 0; j < counter; j++) {
					HashMap<String, Object> hMap = new HashMap<String, Object>();
					String title = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					hMap.put("title", title);
					String time = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
					hMap.put("time", getString(R.string.duration)
							+ getNormalTima(time));
					String singer = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					hMap.put("singer", getString(R.string.artist) + singer);
					String album = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
					hMap.put("album", getString(R.string.album) + album);
					data.add(hMap);
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DATA));
					hMap.put("path", path);
					hMap.put(
							"composer",
							getString(R.string.composer)
									+ cursor.getString(cursor
											.getColumnIndex(MediaStore.Audio.Media.COMPOSER)));
					hMap.put(
							"mime",
							getString(R.string.mime)
									+ cursor.getString(cursor
											.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)));
					hMap.put(
							"size",
							getString(R.string.size)
									+ cursor.getString(cursor
											.getColumnIndex(MediaStore.Audio.Media.SIZE))
									+ " b");
					hMap.put(
							"date",
							getString(R.string.date)
									+ cursor.getString(cursor
											.getColumnIndex(MediaStore.Audio.Media.YEAR)));
					cursor.moveToNext();
				}
				cursor.close();
				return data;
			}

			@Override
			protected void onPostExecute(List<HashMap<String, Object>> data) {
				super.onPostExecute(data);
				dialog.dismiss();
				SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
						data, R.layout.music_item, new String[] { "title",
								"time", "singer", "album" }, new int[] {
								R.id.music_title, R.id.music_time,
								R.id.music_author, R.id.music_album });
				musicListView.setAdapter(adapter);
				musicListView.setOnItemClickListener(new musicClickListener());
				musicListView
						.setOnItemLongClickListener(new musicLongClickListener());
			}
		}.execute();
	}

	private String getNormalTima(String time) {
		int duration = Integer.parseInt(time) / 1000;
		if (duration < 60) {
			return "" + duration + "s";
		} else {
			return "" + duration / 60 + ":" + duration % 60;
		}
	}

	class musicClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			ListView listView = (ListView) parent;
			@SuppressWarnings("unchecked")
			HashMap<String, Object> data = (HashMap<String, Object>) listView
					.getItemAtPosition(position);
			Intent i = new Intent(MainActivity.this, OptionActivity.class);
			i.putExtra("info", data);
			startActivity(i);
		}

	}

	class musicLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			ListView listView = (ListView) parent;
			@SuppressWarnings("unchecked")
			HashMap<String, Object> data = (HashMap<String, Object>) listView
					.getItemAtPosition(position);
			Intent i = new Intent(MainActivity.this, PlayerActivity.class);
			i.putExtra("info", data);
			startActivity(i);
			return true;
		}

	}
}
