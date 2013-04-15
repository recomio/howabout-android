package io.recom.howabout;

import io.recom.howabout.category.adult.fragment.AdultCategoryWrapFragment;
import io.recom.howabout.category.music.fragment.MusicCategoryWrapFragment;
import io.recom.howabout.category.music.fragment.SearchedTrackListFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectResource;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockFragmentActivity {

	@InjectResource(R.array.category_list)
	private String[] categoryStrings;

	@InjectFragment(R.id.contentView)
	private Fragment contentFragment;

	AdultCategoryWrapFragment adultCategoryWrapFragment;
	MusicCategoryWrapFragment musicCategoryWrapFragment;

	private MenuItem searchMenu;

	private SpiceManager contentManager = new SpiceManager(
			JacksonSpringAndroidSpiceService.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
				MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

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
				SearchedTrackListFragment searchedTrackListFragment = new SearchedTrackListFragment();

				Bundle bundle = new Bundle();
				bundle.putString("category", "music");
				bundle.putString("method", "search");
				bundle.putString("searchKeyword", searchKeyword);
				searchedTrackListFragment.setArguments(bundle);

				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.contentView, searchedTrackListFragment,
								"music_search").commit();

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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();

		contentManager.start(this);
	}

	@Override
	public void onStop() {
		contentManager.shouldStop();

		super.onStop();
	}

	public SpiceManager getContentManager() {
		return contentManager;
	}

	public class HowaboutDropdownNavigationListener implements
			OnNavigationListener {
		String[] categoryStrings = getResources().getStringArray(
				R.array.category_list);

		@Override
		public boolean onNavigationItemSelected(int position, long itemId) {
			searchMenu.setVisible(false);

			Fragment categoryWrapFragment;

			if (position == 0) {
				categoryWrapFragment = MainActivity.this.adultCategoryWrapFragment;
			} else {
				searchMenu.setVisible(true);
				categoryWrapFragment = MainActivity.this.musicCategoryWrapFragment;
			}

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.contentView, categoryWrapFragment,
							categoryStrings[position]).commit();

			return true;
		}
	}

}
