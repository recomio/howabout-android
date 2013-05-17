package io.recom.howabout;

import android.os.Bundle;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

// RoboSherlockActivity + RoboSpice.
public abstract class RoboSherlockFlurryAdlibSpiceFragmentActivity extends
		RoboSherlockFlurryAdlibFragmentActivity {

	protected SpiceManager contentManager = new SpiceManager(
			JacksonSpringAndroidSpiceService.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!contentManager.isStarted()) {
			contentManager.start(this);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
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
