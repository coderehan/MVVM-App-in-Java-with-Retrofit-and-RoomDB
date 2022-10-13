package com.rehan.mvvmjava.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rehan.mvvmjava.repositories.SearchTvShowRepository;
import com.rehan.mvvmjava.responses.TvShowsResponse;

public class SearchTvShowViewModel extends ViewModel {

    private SearchTvShowRepository searchTvShowRepository;

    public SearchTvShowViewModel(){
        searchTvShowRepository = new SearchTvShowRepository();
    }

    public LiveData<TvShowsResponse> searchTvShow(String query, int page){
        return searchTvShowRepository.searchTvShow(query, page);
    }
}
