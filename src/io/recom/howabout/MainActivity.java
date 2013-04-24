package io.recom.howabout;

import io.recom.howabout.category.adult.fragment.AdultCategoryWrapFragment;
import io.recom.howabout.category.music.activity.MusicPlayerActivity;
import io.recom.howabout.category.music.activity.MusicPlaylistActivity;
import io.recom.howabout.category.music.activity.SearchedTrackListActivity;
import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.fragment.MusicCategoryWrapFragment;
import io.recom.howabout.category.music.player.GroovesharkWebView;
import io.recom.howabout.category.music.player.MusicPlayer;
import io.recom.howabout.category.music.service.MusicPlayerService;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

//@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockSpiceGroovesharkFragmentActivity {

	// @InjectResource(R.array.category_list)
	private String[] categoryStrings;

	// @InjectResource(R.string.title_activity_music_player)
	private String musicPlayerTitle;
	private String musicPlaylistTitle;

	AdultCategoryWrapFragment adultCategoryWrapFragment;
	MusicCategoryWrapFragment musicCategoryWrapFragment;

	private MenuItem searchMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		groovesharkWebView = (GroovesharkWebView) findViewById(R.id.groovesharkWebView);
		groovesharkWebView.init();

		categoryStrings = getResources().getStringArray(R.array.category_list);
		musicPlayerTitle = getResources().getString(
				R.string.title_activity_music_player);
		musicPlaylistTitle = getResources().getString(
				R.string.title_activity_music_playlist);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(false);

		// dropdown menu in actionBar.
		final SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(
				this, R.array.category_list,
				android.R.layout.simple_list_item_1);
		actionBar.setListNavigationCallbacks(spinnerAdapter,
				new HowaboutDropdownNavigationListener());

		// set framgents.
		adultCategoryWrapFragment = new AdultCategoryWrapFragment();
		Bundle adultCategoryWrapFragmentBundle = new Bundle();
		adultCategoryWrapFragmentBundle.putString("category", "adult");
		adultCategoryWrapFragment.setArguments(adultCategoryWrapFragmentBundle);

		musicCategoryWrapFragment = new MusicCategoryWrapFragment();
		Bundle musicCategoryWrapFragmentBundle = new Bundle();
		musicCategoryWrapFragmentBundle.putString("category", "music");
		musicCategoryWrapFragment.setArguments(musicCategoryWrapFragmentBundle);

		// set music player webView.
		HowaboutApplication application = (HowaboutApplication) getApplication();
		MusicPlayer musicPlayer = new MusicPlayer(this, groovesharkWebView);
		application.setMusicPlayer(musicPlayer);
		musicPlayer.setMusicPlaylistAdapter(new MusicPlaylistAdapter(
				musicPlayer));
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		HowaboutApplication application = (HowaboutApplication) getApplication();
		MusicPlayer musicPlayer = application.getMusicPlayer();
		musicPlayer.pause();

		stopService(new Intent(this, MusicPlayerService.class));

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// Place an action bar item for searching.
		final SearchView searchView = new SearchView(getSupportActionBar()
				.getThemedContext());
		searchView.setQueryHint("Search");
		searchView.setIconified(true);

		searchMenu = menu.getItem(0);

		searchMenu.setActionView(searchView).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		searchView
				.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View view,
							boolean queryTextFocused) {
						if (!queryTextFocused) {
							Log.d("searchMenu.collapseActionView();", Boolean
									.toString(searchMenu.collapseActionView()));
							searchView.setQuery("", false);
						}
					}
				});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String searchKeyword) {
				Intent intent = new Intent(MainActivity.this,
						SearchedTrackListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				Bundle bundle = new Bundle();
				bundle.putString("category", "music");
				bundle.putString("method", "search");
				bundle.putString("searchKeyword", searchKeyword);
				intent.putExtras(bundle);

				startActivity(intent);

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(musicPlayerTitle)) {
			Intent intent = new Intent(this, MusicPlayerActivity.class);

			Bundle bundle = new Bundle();
			bundle.putString("method", "");
			intent.putExtras(bundle);

			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			startActivity(intent);
			return true;

		} else if (item.getTitle().equals(musicPlaylistTitle)) {
			Intent intent = new Intent(this, MusicPlaylistActivity.class);

			Bundle bundle = new Bundle();
			intent.putExtras(bundle);

			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public class HowaboutDropdownNavigationListener implements
			OnNavigationListener {

		@Override
		public boolean onNavigationItemSelected(int position, long itemId) {
			searchMenu.setVisible(false);

			Fragment categoryWrapFragment;

			if (position == 0) {
				searchMenu.setVisible(true);
				categoryWrapFragment = MainActivity.this.musicCategoryWrapFragment;
			} else {
				categoryWrapFragment = MainActivity.this.adultCategoryWrapFragment;
			}

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.contentView, categoryWrapFragment,
							categoryStrings[position]).commit();

			return true;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
