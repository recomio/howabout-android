package io.recom.howabout;


import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockFragmentActivity implements TabListener {
	
	@InjectResource(R.string.tab_random)
	String tabRandomString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//		actionBar.setDisplayShowTitleEnabled(false);

		Tab randomTab = actionBar.newTab().setText(tabRandomString).setTabListener(this);
		actionBar.addTab(randomTab);
		
		Tab imagesTab = actionBar.newTab().setText("Images").setTabListener(this);
		actionBar.addTab(imagesTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		switch (tab.getPosition()) {
		case 0:
			RandomFragment randomFragment = new RandomFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.ContentView, randomFragment).commit();
			break;
		case 1:
			ImageListFragment imageListFragment = new ImageListFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.ContentView, imageListFragment).commit();
			break;
		}
		
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
	
}
