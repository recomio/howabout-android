package io.recom.howabout;

import com.flurry.android.FlurryAgent;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

public class RoboSherlockFlurryFragmentActivity extends
		RoboSherlockFragmentActivity {

	private final String FLURRY_API_KEY = "PXPN496ZGVC95C52Y2YM";

	@Override
	public void onStart() {
		super.onStart();

		FlurryAgent.onStartSession(this, FLURRY_API_KEY);
	}

	@Override
	public void onStop() {
		FlurryAgent.onEndSession(this);

		super.onStop();
	}
}
