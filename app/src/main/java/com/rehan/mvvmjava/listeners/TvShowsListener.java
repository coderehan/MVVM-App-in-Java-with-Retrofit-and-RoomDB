package com.rehan.mvvmjava.listeners;

import com.rehan.mvvmjava.models.TvShow;

public interface TvShowsListener {
    // Interface is mainly used for moving from 1 activity to another activity
    void onTvShowClicked(TvShow tvShow);
}
