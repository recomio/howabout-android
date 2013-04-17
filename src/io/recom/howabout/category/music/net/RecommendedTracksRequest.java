package io.recom.howabout.category.music.net;

import io.recom.howabout.category.music.model.TrackList;

import android.net.Uri;
import android.util.Log;

public class RecommendedTracksRequest extends TracksRequest {

	protected String trackId = "";
	protected int limit = 100;

	public RecommendedTracksRequest(String trackId) {
		super();
		this.trackId = trackId;
	}
	
	public RecommendedTracksRequest(String trackId, int limit) {
		super(limit);
		this.trackId = trackId;
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	@Override
	public TrackList loadDataFromNetwork() throws Exception {
		if (trackId.length() == 0) {
			throw new Exception("no trackId param.");
		}

		Uri.Builder uriBuilder = Uri.parse(
				"http://listena.recom.io/tracks/recommend/" + trackId + "/track").buildUpon();
		
		if (limit > 0) {
			uriBuilder.appendQueryParameter("limit",
					Integer.toString(this.limit));
		}

		String url = uriBuilder.build().toString();
		Log.d("url", url);

		return getRestTemplate().getForObject(url, TrackList.class);
	}

}
