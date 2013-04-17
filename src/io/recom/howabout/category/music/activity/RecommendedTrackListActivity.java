package io.recom.howabout.category.music.activity;

import io.recom.howabout.R;
import io.recom.howabout.category.music.fragment.RecommendedTrackListFragment;
import roboguice.inject.ContentView;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

@ContentView(R.layout.activity_track_list)
public class RecommendedTrackListActivity extends TrackListActivity {

	protected RecommendedTrackListFragment recommendedTrackListFragment;

	protected String trackId;
	protected String trackTitle;
	protected String artistName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		trackId = bundle.getString("trackId");
		trackTitle = bundle.getString("trackTitle");
		artistName = bundle.getString("artistName");

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(trackTitle);
		actionBar.setSubtitle(artistName);

		recommendedTrackListFragment = new RecommendedTrackListFragment();
		Bundle recommendedTrackListFragmentBundle = new Bundle();
		recommendedTrackListFragmentBundle.putString("trackId", trackId);
		recommendedTrackListFragment
				.setArguments(recommendedTrackListFragmentBundle);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.contentView, recommendedTrackListFragment,
						"music_recommend").commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.recommended_track_list, menu);

		return super.onCreateOptionsMenu(menu);
	}

}
