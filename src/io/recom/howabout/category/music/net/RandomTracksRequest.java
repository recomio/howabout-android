package io.recom.howabout.category.music.net;

import io.recom.howabout.category.music.model.TrackList;

import android.net.Uri;

public class RandomTracksRequest extends TracksRequest {

	@Override
	public TrackList loadDataFromNetwork() throws Exception {
		Uri.Builder uriBuilder = Uri.parse(
				"http://listena.recom.io/tracks/random").buildUpon();
		if (limit > 0) {
			uriBuilder.appendQueryParameter("limit",
					Integer.toString(this.limit));
		}

		String url = uriBuilder.build().toString();

		return getRestTemplate().getForObject(url, TrackList.class);
	}

}
