package io.recom.howabout.category.music.net;

import io.recom.howabout.category.music.model.TrackList;

import android.net.Uri;
import android.util.Log;

public class SearchedTracksRequest extends TracksRequest {

	protected String searchKeyword = "";

	public SearchedTracksRequest(String searchKeyword) {
		super();
		this.searchKeyword = searchKeyword;
	}
	
	public SearchedTracksRequest(String searchKeyword, int limit) {
		super(limit);
		this.searchKeyword = searchKeyword;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	@Override
	public TrackList loadDataFromNetwork() throws Exception {
		if (searchKeyword.length() == 0) {
			throw new Exception("no q param.");
		}

		// 한글 인자는 정상적인 코드로 동작하지 않는다.
		// uriBuilder.appendQueryParameter("q", searchKeyword);
		// ?q= 으로 하드코딩 하니 정상 동작 한다.
		Uri.Builder uriBuilder = Uri.parse(
				"http://listena.recom.io/tracks/search?q=" + searchKeyword).buildUpon();
		
		if (limit > 0) {
			uriBuilder.appendQueryParameter("limit",
					Integer.toString(this.limit));
		}

		String url = uriBuilder.build().toString();
		Log.d("url", url);

		return getRestTemplate().getForObject(url, TrackList.class);
	}

}
