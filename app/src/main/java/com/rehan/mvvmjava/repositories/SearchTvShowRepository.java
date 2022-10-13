package com.rehan.mvvmjava.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rehan.mvvmjava.network.Api;
import com.rehan.mvvmjava.network.ApiService;
import com.rehan.mvvmjava.responses.TvShowsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTvShowRepository {

    private ApiService apiService;

    public SearchTvShowRepository(){
        apiService = Api.getRetrofit().create(ApiService.class);
    }

    public LiveData<TvShowsResponse> searchTvShow(String query, int page){
        MutableLiveData<TvShowsResponse> data = new MutableLiveData<>();
        apiService.searchTvShow(query, page).enqueue(new Callback<TvShowsResponse>() {
            @Override
            public void onResponse(Call<TvShowsResponse> call, Response<TvShowsResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TvShowsResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
