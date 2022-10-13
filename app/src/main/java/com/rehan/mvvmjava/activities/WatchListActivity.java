package com.rehan.mvvmjava.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rehan.mvvmjava.R;
import com.rehan.mvvmjava.adapters.WatchListAdapter;
import com.rehan.mvvmjava.common.Common;
import com.rehan.mvvmjava.databinding.ActivityWatchListBinding;
import com.rehan.mvvmjava.listeners.WatchListListener;
import com.rehan.mvvmjava.models.TvShow;
import com.rehan.mvvmjava.viewmodels.WatchListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends AppCompatActivity implements WatchListListener {

    private ActivityWatchListBinding binding;
    private WatchListViewModel watchListViewModel;
    private WatchListAdapter watchListAdapter;
    private List<TvShow> watchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_list);
        doInitialization();
    }

    private void doInitialization() {
        watchListViewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        binding.ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        watchList = new ArrayList<>();
        loadWatchList();
    }

    private void loadWatchList() {
        binding.setIsLoading(true);     // Before showing data, first it will load progressbar
        // Implementing RxJava
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(watchListViewModel.loadWatchList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    binding.setIsLoading(false);
                    if (watchList.size() > 0) {
                        watchList.clear();
                    }
                    watchList.addAll(tvShows);
                    watchListAdapter = new WatchListAdapter(watchList, this);
                    binding.rvWatchList.setAdapter(watchListAdapter);
                    binding.rvWatchList.setVisibility(View.VISIBLE);
                    compositeDisposable.dispose();
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.IS_WATCHLIST_UPDATED){
            loadWatchList();
            Common.IS_WATCHLIST_UPDATED = false;
        }
    }

    @Override
    public void onTvShowClicked(TvShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TvShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTvShowFromWatchList(TvShow tvShow, int position) {
        // Implementing RxJava
        CompositeDisposable compositeDisposableForDelete = new CompositeDisposable();
        compositeDisposableForDelete.add(watchListViewModel.removeTvShowFromWatchList(tvShow)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    watchList.remove(position);
                    watchListAdapter.notifyItemRemoved(position);
                    watchListAdapter.notifyItemRangeChanged(position, watchListAdapter.getItemCount());
                    compositeDisposableForDelete.dispose();
                }));
    }
}