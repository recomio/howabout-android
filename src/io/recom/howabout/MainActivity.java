package io.recom.howabout;

import roboguice.inject.ContentView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import android.os.Bundle;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

}
