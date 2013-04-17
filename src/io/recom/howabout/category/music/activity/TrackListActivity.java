package io.recom.howabout.category.music.activity;

import roboguice.inject.ContentView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import android.os.Bundle;

@ContentView(R.layout.activity_track_list)
public abstract class TrackListActivity extends
		RoboSherlockSpiceFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
		return super.onOptionsItemSelected(item);
	}

}
