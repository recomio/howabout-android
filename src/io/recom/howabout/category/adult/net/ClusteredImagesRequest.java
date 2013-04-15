package io.recom.howabout.category.adult.net;

import io.recom.howabout.category.adult.model.ImageList;

import android.net.Uri;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;


public class ClusteredImagesRequest extends SpringAndroidSpiceRequest<ImageList> {
	
	protected String pHash = "";
	protected int limit = 0;

	public ClusteredImagesRequest(String pHash, int limit) {
		this(pHash);
		this.limit = limit;
	}
	
	public ClusteredImagesRequest(String pHash) {
		this();
		this.pHash = pHash;
	}
	
	public ClusteredImagesRequest() {
		super( ImageList.class );
	}

	public String getpHash() {
		return pHash;
	}
	
	public void setpHash(String pHash) {
		this.pHash = pHash;
	}
	
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public ImageList loadDataFromNetwork() throws Exception {
		if (pHash.length() == 0) {
			throw new Exception("no pHash.");
		}
        
		Uri.Builder uriBuilder = Uri.parse("http://hoogi.me/images/" + pHash + "/cluster").buildUpon();
        if (limit > 0) {
        	uriBuilder.appendQueryParameter( "limit", Integer.toString(this.limit) );
        }

        String url = uriBuilder.build().toString();

        return getRestTemplate().getForObject( url, ImageList.class );
	}

}
