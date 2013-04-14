package io.recom.howabout.category.adult.net;

import io.recom.howabout.category.adult.model.ImageList;

import android.net.Uri;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;


public class RandomImagesRequest extends SpringAndroidSpiceRequest<ImageList> {
	
	protected int limit = 30;

	public RandomImagesRequest(int limit) {
		this();
		this.limit = limit;
	}
	
	public RandomImagesRequest() {
		super( ImageList.class );
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public ImageList loadDataFromNetwork() throws Exception {
        Uri.Builder uriBuilder = Uri.parse( "http://hoogi.me/images/random" ).buildUpon();
        uriBuilder.appendQueryParameter( "limit", Integer.toString(this.limit) );

        String url = uriBuilder.build().toString();

        return getRestTemplate().getForObject( url, ImageList.class );
	}

}
