package io.recom.howabout.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {

    protected String pHash;
    protected int width;
    protected int height;
    
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
