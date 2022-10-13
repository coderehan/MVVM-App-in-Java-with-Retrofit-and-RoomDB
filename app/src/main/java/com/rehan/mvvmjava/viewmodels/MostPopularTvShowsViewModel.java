package com.rehan.mvvmjava.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rehan.mvvmjava.repositories.MostPopularTvShowsRepository;
import com.rehan.mvvmjava.responses.TvShowsResponse;

public class MostPopularTvShowsViewModel extends ViewModel {

    private MostPopularTvShowsRepository mostPopularTvShowsRepository;

    public MostPopularTvShowsViewModel(){
        mostPopularTvShowsRepository = new MostPopularTvShowsRepository();
    }

    public LiveData<TvShowsResponse> getMostPopularTvShows(int page){
        return mostPopularTvShowsRepository.getMostPopularTvShowsResponse(page);
    }
}
