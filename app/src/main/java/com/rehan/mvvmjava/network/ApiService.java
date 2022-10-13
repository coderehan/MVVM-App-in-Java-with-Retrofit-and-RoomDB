package com.rehan.mvvmjava.network;

import com.rehan.mvvmjava.responses.TvShowDetailsResponse;
import com.rehan.mvvmjava.responses.TvShowsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("most-popular")
    Call<TvShowsResponse> getMostPopularTvShows(@Query("page") int page);

    @GET("show-details")
    Call<TvShowDetailsResponse> getTvShowDetails(@Query("q") String tvShowId);

    @GET("search")
    Call<TvShowsResponse> searchTvShow(@Query("q") String query, @Query("page") int page);
}
