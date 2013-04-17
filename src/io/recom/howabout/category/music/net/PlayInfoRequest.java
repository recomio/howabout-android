package io.recom.howabout.category.music.net;

import io.recom.howabout.category.music.model.PlayInfo;
import android.net.Uri;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class PlayInfoRequest extends SpringAndroidSpiceRequest<PlayInfo> {

	protected String trackTitle;
	protected String artistName;

	public PlayInfoRequest(String trackTitle, String artistName) {
		this();

		this.trackTitle = trackTitle;
		this.artistName = artistName;
	}

	public PlayInfoRequest() {
		super(PlayInfo.class);
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	@Override
	public PlayInfo loadDataFromNetwork() throws Exception {
		Uri.Builder uriBuilder = Uri.parse(
				"http://listena.recom.io/playInfo/" + trackTitle + "/"
						+ artistName).buildUpon();

		String url = uriBuilder.build().toString();

		return getRestTemplate().getForObject(url, PlayInfo.class);
	}

}
