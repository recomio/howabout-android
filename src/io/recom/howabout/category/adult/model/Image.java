package io.recom.howabout.category.adult.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {

    protected String pHash;
    protected int width;
    protected int height;
    
    public Image() {
    	
    }
    
    public Image(String pHash) {
    	this.pHash = pHash;
    }
    
	@JsonProperty("pHash")
    public String getpHash() {
		return pHash;
	}
	
	@JsonProperty("pHash")
	public void setpHash(String pHash) {
		this.pHash = pHash;
	}
	
	@JsonProperty("width")
	public int getWidth() {
		return width;
	}
	
	public String getThumbmailUrl() {
		if (pHash.length() > 0) {
			return "http://images.cdn.realgirls.recom.io/image/" + pHash + "/thumbnail/150/150";
		} else {
			throw new RuntimeException("no pHash.");
		}
	}
	
	public String getBasicUrl() {
		if (pHash.length() > 0) {
			return "http://images.cdn.realgirls.recom.io/image/" + pHash + "/resize/800";
		} else {
			throw new RuntimeException("no pHash.");
		}
	}
	
	@JsonProperty("width")
	public void setWidth(int width) {
		this.width = width;
	}
	
	@JsonProperty("height")
	public int getHeight() {
		return height;
	}
	
	@JsonProperty("height")
	public void setHeight(int height) {
		this.height = height;
	}
	
}
