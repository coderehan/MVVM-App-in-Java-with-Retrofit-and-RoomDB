package com.rehan.mvvmjava.responses;

import com.google.gson.annotations.SerializedName;
import com.rehan.mvvmjava.models.TvShow;

import java.util.List;

public class TvShowsResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("pages")
    private int totalPages;

    @SerializedName("tv_shows")
    private List<TvShow> tvShows;

    public TvShowsResponse(int page, int totalPages, List<TvShow> tvShows) {
        this.page = page;
        this.totalPages = totalPages;
        this.tvShows = tvShows;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<TvShow> getTvShows() {
        return tvShows;
    }
}
