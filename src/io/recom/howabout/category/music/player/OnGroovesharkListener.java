package io.recom.howabout.category.music.player;

public interface OnGroovesharkListener {

	void onLoadStart(String src);

	void onPlay();

	void onEnded();

}
