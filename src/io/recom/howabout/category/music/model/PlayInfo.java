package io.recom.howabout.category.music.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayInfo {

	protected String id;
	protected String trackTitle;
	protected String artistName;
	protected int bugsTrackId;
	protected int bugsArtistId;
	protected int bugsAlbumId;
	protected String bugsAlbumTitle;
	protected int refCount;
	protected String groovesharkSongID;
	protected String groovesharkSongName;
	protected String groovesharkArtistID;
	protected String groovesharkArtistName;
	protected String groovesharkAlbumID;
	protected String groovesharkAlbumName;
	protected String youtubeMovieId;
	protected String youtubeMovieUrl;
	protected String thumbnailImageUrl;
	protected String hqThumbnailImageUrl;
	protected String duration;
	protected String youtubeMp4StreamUrl;

	public String getTrackId() {
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

	public String getId() {
		return id;
	}

	public String getGroovesharkSongID() {
		return groovesharkSongID;
	}

	public void setGroovesharkSongID(String groovesharkSongID) {
		this.groovesharkSongID = groovesharkSongID;
	}

	public String getGroovesharkSongName() {
		return groovesharkSongName;
	}

	public void setGroovesharkSongName(String groovesharkSongName) {
		this.groovesharkSongName = groovesharkSongName;
	}

	public String getGroovesharkArtistID() {
		return groovesharkArtistID;
	}

	public void setGroovesharkArtistID(String groovesharkArtistID) {
		this.groovesharkArtistID = groovesharkArtistID;
	}

	public String getGroovesharkArtistName() {
		return groovesharkArtistName;
	}

	public void setGroovesharkArtistName(String groovesharkArtistName) {
		this.groovesharkArtistName = groovesharkArtistName;
	}

	public String getGroovesharkAlbumID() {
		return groovesharkAlbumID;
	}

	public void setGroovesharkAlbumID(String groovesharkAlbumID) {
		this.groovesharkAlbumID = groovesharkAlbumID;
	}

	public String getGroovesharkAlbumName() {
		return groovesharkAlbumName;
	}

	public void setGroovesharkAlbumName(String groovesharkAlbumName) {
		this.groovesharkAlbumName = groovesharkAlbumName;
	}

	public String getYoutubeMovieId() {
		return youtubeMovieId;
	}

	public void setYoutubeMovieId(String youtubeMovieId) {
		this.youtubeMovieId = youtubeMovieId;
	}

	public String getYoutubeMovieUrl() {
		return youtubeMovieUrl;
	}

	public void setYoutubeMovieUrl(String youtubeMovieUrl) {
		this.youtubeMovieUrl = youtubeMovieUrl;
	}

	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}

	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}

	public String getHqThumbnailImageUrl() {
		return hqThumbnailImageUrl;
	}

	public void setHqThumbnailImageUrl(String hqThumbnailImageUrl) {
		this.hqThumbnailImageUrl = hqThumbnailImageUrl;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getYoutubeMp4StreamUrl() {
		return youtubeMp4StreamUrl;
	}

	public void setYoutubeMp4StreamUrl(String youtubeMp4StreamUrl) {
		this.youtubeMp4StreamUrl = youtubeMp4StreamUrl;
	}

	public String getThumbmailUrl() {
		return "http://image.bugsm.co.kr/album/images/224/"
				+ (bugsAlbumId / 100) + "/" + bugsAlbumId + ".jpg";
	}

	public boolean isGrooveshark() {
		if (groovesharkSongID == null) {
			return false;
		}
		return true;
	}

}
