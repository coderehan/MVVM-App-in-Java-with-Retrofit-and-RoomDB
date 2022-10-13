package com.rehan.mvvmjava.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rehan.mvvmjava.database.TvShowDatabase;
import com.rehan.mvvmjava.models.TvShow;
import com.rehan.mvvmjava.repositories.TvShowDetailRepository;
import com.rehan.mvvmjava.responses.TvShowDetailsResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TvShowDetailsViewModel extends AndroidViewModel {

    private TvShowDetailRepository tvShowDetailRepository;
    private TvShowDatabase tvShowDatabase;

    public TvShowDetailsViewModel(@NonNull Application application){
        super(application);
        tvShowDetailRepository = new TvShowDetailRepository();
        tvShowDatabase = TvShowDatabase.getTvShowDatabase(application);
    }

    public LiveData<TvShowDetailsResponse> getTvShowDetails(String tvShowId){
        return tvShowDetailRepository.getTvShowDetails(tvShowId);
    }

    public Completable addToWatchList(TvShow tvShow){
        return tvShowDatabase.tvShowDao().addToWatchList(tvShow);
    }

    public Flowable<TvShow> getTvShowFromWatchList(String tvShowId){
        return tvShowDatabase.tvShowDao().getTvShowFromWatchList(tvShowId);
    }

    public Completable removeTvShowFromWatchList(TvShow tvShow){
        return tvShowDatabase.tvShowDao().removeFromWatchList(tvShow);
    }
}
