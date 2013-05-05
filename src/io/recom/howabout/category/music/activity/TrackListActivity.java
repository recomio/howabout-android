package io.recom.howabout.category.music.activity;

import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockFlurryAdlibSpiceFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

@ContentView(R.layout.activity_track_list)
public abstract class TrackListActivity extends
		RoboSherlockFlurryAdlibSpiceFragmentActivity {

	@InjectResource(R.string.title_activity_music_playlist)
	protected String musicPlaylistTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		setAdsContainer(R.id.adView);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}

		if (item.getTitle().equals(musicPlaylistTitle)) {
			Intent intent = new Intent(this, MusicPlaylistActivity.class);

			Bundle bundle = new Bundle();
			intent.putExtras(bundle);

			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
