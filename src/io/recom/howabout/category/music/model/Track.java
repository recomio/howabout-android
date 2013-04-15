package io.recom.howabout.category.music.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

	protected String id;
	protected String trackTitle;
	protected String artistName;
	protected int bugsTrackId;
	protected int bugsArtistId;
	protected int bugsAlbumId;
	protected String bugsAlbumTitle;
	protected int refCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getBugsTrackId() {
		return bugsTrackId;
	}

	public void setBugsTrackId(int bugsTrackId) {
		this.bugsTrackId = bugsTrackId;
	}

	public int getBugsArtistId() {
		return bugsArtistId;
	}

	public void setBugsArtistId(int bugsArtistId) {
		this.bugsArtistId = bugsArtistId;
	}

	public int getBugsAlbumId() {
		return bugsAlbumId;
	}

	public void setBugsAlbumId(int bugsAlbumId) {
		this.bugsAlbumId = bugsAlbumId;
	}

	public String getBugsAlbumTitle() {
		return bugsAlbumTitle;
	}

	public void setBugsAlbumTitle(String bugsAlbumTitle) {
		this.bugsAlbumTitle = bugsAlbumTitle;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public String getThumbmailUrl() {
		return "http://image.bugsm.co.kr/album/images/224/"
				+ (bugsAlbumId / 100) + "/" + bugsAlbumId + ".jpg";
	}
	
}
