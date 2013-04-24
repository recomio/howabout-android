package io.recom.howabout;

import io.recom.howabout.category.music.player.GroovesharkWebView;

// RoboSherlockSpiceActivity + GroovesharkWebView.
public abstract class RoboSherlockSpiceGroovesharkFragmentActivity extends
		RoboSherlockSpiceFragmentActivity {

	protected GroovesharkWebView groovesharkWebView;

	public GroovesharkWebView getGroovesharkWebView() {
		return groovesharkWebView;
	}

}
