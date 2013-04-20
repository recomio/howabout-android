package io.recom.howabout.category.music.activity;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.player.MusicPlayer;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

@ContentView(R.layout.activity_music_playlist)
public class MusicPlaylistActivity extends RoboSherlockSpiceFragmentActivity {

	MusicPlaylistAdapter musicPlaylistAdapter;

	@InjectView(R.id.listView)
	protected ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		HowaboutApplication application = (HowaboutApplication) getApplication();
		final MusicPlayer musicPlayer = application.getMusicPlayer();
		musicPlaylistAdapter = musicPlayer.getMusicPlaylistAdapter();

		listView.setAdapter(musicPlaylistAdapter);
		musicPlaylistAdapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				musicPlayer.setLastActivity(MusicPlaylistActivity.this);
				musicPlayer.play(position);
				musicPlaylistAdapter.notifyDataSetChanged();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				musicPlayer.setLastActivity(MusicPlaylistActivity.this);
				musicPlayer.remove(position);
				musicPlaylistAdapter.notifyDataSetChanged();
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.music_playlist, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
