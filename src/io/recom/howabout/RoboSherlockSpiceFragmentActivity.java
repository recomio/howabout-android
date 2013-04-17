package io.recom.howabout;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

// RoboSherlockFragmentActivity + RoboSpice.
public abstract class RoboSherlockSpiceFragmentActivity extends
		RoboSherlockFragmentActivity {

	protected SpiceManager contentManager = new SpiceManager(
			JacksonSpringAndroidSpiceService.class);

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

}
