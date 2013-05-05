package io.recom.howabout;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

// RoboSherlockActivity + RoboSpice.
public abstract class RoboSherlockFlurryAdlibSpiceFragmentActivity extends
		RoboSherlockFlurryAdlibFragmentActivity {

	protected SpiceManager contentManager = new SpiceManager(
			JacksonSpringAndroidSpiceService.class);

	@Override
	public void onStart() {
		super.onStart();

		if (!contentManager.isStarted()) {
			contentManager.start(this);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		contentManager.shouldStop();

		super.onDestroy();
	}

	public SpiceManager getContentManager() {
		return contentManager;
	}

}
