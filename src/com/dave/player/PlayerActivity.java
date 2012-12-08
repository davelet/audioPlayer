package com.dave.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends Activity {

	HashMap<String, Object> data;
	TextView name;
	static SeekBar playSeekBar;
	boolean playOrPause = false;// 正在播放，需要暂停
	Button playOrPauseButton;
	// private MediaPlayer mediaPlayer = null;
	static TextView currentTime;
	TextView totalTime;
	private static String path;
	private static TextView lrcview1;
	private static TextView lrcview2;
	private static TextView lrcview3;
	static BufferedReader br = null;
	static boolean hasLRC = false;
	static String lrcString;
	static boolean tag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = (HashMap<String, Object>) getIntent().getSerializableExtra(
				"info");
		setContentView(R.layout.player_ui);
		name = (TextView) findViewById(R.id.song_title);
		currentTime = (TextView) findViewById(R.id.current_time);
		totalTime = (TextView) findViewById(R.id.total_time);
		playOrPauseButton = (Button) findViewById(R.id.play_or_pause);
		lrcview1 = (TextView) findViewById(R.id.fommer_lrc);
		lrcview2 = (TextView) findViewById(R.id.this_lrc);
		lrcview3 = (TextView) findViewById(R.id.latter_lrc);
		path = (String) data.get("path");
		if (MainActivity.mediaPlayer == null) {
			MainActivity.mediaPlayer = new MediaPlayer();
		}
		playOrPauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				songAction(path);
			}
		});
		name.setText((String) data.get("title"));
		playSeekBar = (SeekBar) findViewById(R.id.play_progress);
		playSeekBar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						MainActivity.mediaPlayer.start();
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						MainActivity.mediaPlayer.pause();
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						MainActivity.mediaPlayer.seekTo(seekBar.getProgress()
								* MainActivity.mediaPlayer.getDuration()
								/ seekBar.getMax());
					}
				});
		totalTime.setText(((String) data.get("time")).substring(3));
		if (getLrc()) {
			hasLRC = true;
		}
		playMusic(path);

	}

	private static Runnable updateRunnable = new Runnable() {

		@Override
		public void run() {
			SetSongTime(MainActivity.mediaPlayer.getCurrentPosition());
//			SetSeekBar(MainActivity.mediaPlayer.getCurrentPosition());
			if (hasLRC) {
				SetLrc();
			}
			playSeekBar.postDelayed(updateRunnable, 200);
		}
	};

	private void playMusic(String path) {
		try {
			/* 重置MediaPlayer */
			MainActivity.mediaPlayer.reset();
			/* 设置要播放的文件的路径 */
			MainActivity.mediaPlayer.setDataSource(path);
			/* 准备播放 */
			MainActivity.mediaPlayer.prepare();
			/* 开始播放 */
			MainActivity.mediaPlayer.start();
			playSeekBar.postDelayed(updateRunnable, 0);
			MainActivity.mediaPlayer
					.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer arg0) {
							// 播放完成一首之后
							PlayerActivity.this.finish();
						}
					});
		} catch (IOException e) {
		}
	}

	protected static void SetLrc() {
		try {
			br = new BufferedReader(new StringReader(lrcString));
			String temp = null;
			if (tag) {
				temp = br.readLine();
				lrcString = lrcString
						.replace(lrcString.substring(0,
								lrcString.indexOf("\n") + 1), "");
				tag = false;
			}
			if ((temp) != null) {
				if (temp.indexOf("[ar") >= 0) {
					lrcview1.setText(temp.substring(temp.indexOf(":") + 1,
							temp.length() - 1));
					tag = true;
				} else if (temp.indexOf("[ti") >= 0) {
					lrcview2.setText(temp.substring(temp.indexOf(":") + 1,
							temp.length() - 1));
					tag = true;
				} else if (temp.contains("[by")) {
					lrcview3.setText(temp.substring(temp.indexOf(":") + 1,
							temp.length() - 1));
					tag = true;
				} else if (temp.contains(":") && temp.contains(".")) {
					String tString = temp.substring(1, temp.indexOf("."))
							.replace(":", "-");
					String[] tArray = tString.split("-");
					int ct = Integer.parseInt(tArray[0]) * 60
							+ Integer.parseInt(tArray[1]);
					if (ct * 1000 + 500 < MainActivity.mediaPlayer
							.getCurrentPosition()) {
						lrcview2.setText(temp.substring(10, temp.length()));
						tag = true;
						return;
					} else {
						lrcview3.setText(temp.substring(10, temp.length()));
					}
					if (tArray.length < 2) {
						tag = true;
					}
				} else {
					lrcview2.setText(temp);
					tag = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean getLrc() {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {
				lrcview2.setText("正在搜索歌词");
			};

			@Override
			protected String doInBackground(Void... params) {
				String lrcpath = PlayerActivity.path;
				lrcpath = lrcpath.replace("wma", "lrc").replace("mp3", "lrc");
				File file = new File(lrcpath);
				StringBuilder sb = new StringBuilder("");
				try {
					br = new BufferedReader(new InputStreamReader(
							new FileInputStream(file), "GBK"));
					String temp = null;
					while ((temp = br.readLine()) != null) {
						sb.append(temp).append("\n");
					}
				} catch (Exception e) {
				} finally {
					try {
						br.close();
					} catch (Exception e) {
					}
				}
				return sb.toString();
			}

			protected void onPostExecute(String result) {
				if (result.length() > 0) {
					hasLRC = true;
					lrcString = result;
					lrcview2.setText("正在加载");
				} else {
					lrcview2.setText("暂无歌词");
				}
			};

		}.execute();

		return true;
	}

	protected static void SetSeekBar(int i) {
		playSeekBar.setIndeterminate(false);
		playSeekBar
				.setProgress((int) (i / MainActivity.mediaPlayer.getDuration() * playSeekBar
						.getMax()));
	}

	protected static void SetSongTime(int i) {
		i /= 1000;
		currentTime.setText(i < 60 ? "" + i : i / 60 + ":" + i % 60);
	}

	private void songAction(String path) {
		if (playOrPause) {
			playOrPause = false;
			playOrPauseButton.setText(getString(R.string.pause));
			MainActivity.mediaPlayer.start();
		} else {
			playOrPause = true;
			playOrPauseButton.setText(getString(R.string.play));
			MainActivity.mediaPlayer.pause();
		}
	}

	public void closeApplication(View view) {
		finish();
		System.exit(0);
	}

	public void getDetail(View view) {
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("info", data);
		startActivity(intent);
	}
}
