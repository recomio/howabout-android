package io.recom.howabout.category.music.net;

import com.octo.android.robospice.request.SpiceRequest;

public class YoutubeMp4StreamUrlRequest extends SpiceRequest<String> {

	protected String youtubeMovieId;

	public YoutubeMp4StreamUrlRequest(String youtubeMovieId) {
		super(String.class);
		this.youtubeMovieId = youtubeMovieId;
	}

	@Override
	public String loadDataFromNetwork() throws Exception {
		YoutubeMp4StreamUrlGetter youtubeMp4StreamUrlGetter = new YoutubeMp4StreamUrlGetter();

		String youtubeMp4StreamUrl = youtubeMp4StreamUrlGetter
				.getYoutubeMp4StreamUrl("http://www.youtube.com/watch?v="
						+ youtubeMovieId);

		return youtubeMp4StreamUrl;
	}
}
