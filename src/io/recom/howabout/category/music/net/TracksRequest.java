package io.recom.howabout.category.music.net;

import io.recom.howabout.category.music.model.TrackList;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public abstract class TracksRequest extends
		SpringAndroidSpiceRequest<TrackList> {

	protected int limit = 0;

	public TracksRequest(int limit) {
		this();
		this.limit = limit;
	}

	public TracksRequest() {
		super(TrackList.class);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
