package com.rehan.mvvmjava.listeners;

import com.rehan.mvvmjava.models.TvShow;

public interface WatchListListener {

    void onTvShowClicked(TvShow tvShow);

    void removeTvShowFromWatchList(TvShow tvShow, int position);
}
