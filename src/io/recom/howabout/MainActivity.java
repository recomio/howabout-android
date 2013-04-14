package io.recom.howabout;

import io.recom.howabout.category.adult.fragment.AdultCategoryWrapFragment;
import io.recom.howabout.category.music.fragment.MusicCategoryWrapFragment;
import roboguice.inject.ContentView;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockFragmentActivity {

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
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public class HowaboutDropdownNavigationListener implements
			OnNavigationListener {
		String[] categoryStrings = getResources().getStringArray(
				R.array.category_list);

		@Override
		public boolean onNavigationItemSelected(int position, long itemId) {
			Fragment categoryWrapFragment;

			if (position == 0) {
				categoryWrapFragment = new AdultCategoryWrapFragment();
			} else if (position == 1) {
				categoryWrapFragment = new MusicCategoryWrapFragment();
			} else {
				categoryWrapFragment = new MusicCategoryWrapFragment();
			}

			Bundle bundle = new Bundle();
			bundle.putString("category", categoryStrings[position]);
			categoryWrapFragment.setArguments(bundle);

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.contentView, categoryWrapFragment,
							categoryStrings[position]).commit();

			return true;
		}
	}

}
