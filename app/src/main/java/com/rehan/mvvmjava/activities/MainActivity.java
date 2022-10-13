package com.rehan.mvvmjava.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rehan.mvvmjava.R;
import com.rehan.mvvmjava.adapters.TvShowsAdapter;
import com.rehan.mvvmjava.common.Common;
import com.rehan.mvvmjava.databinding.ActivityMainBinding;
import com.rehan.mvvmjava.listeners.TvShowsListener;
import com.rehan.mvvmjava.models.TvShow;
import com.rehan.mvvmjava.viewmodels.MostPopularTvShowsViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TvShowsListener {

    private ActivityMainBinding binding;
    private MostPopularTvShowsViewModel viewModel;
    private List<TvShow> tvShowsList;
    private TvShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(MostPopularTvShowsViewModel.class);
        binding.rvTvShows.setHasFixedSize(true);
        tvShowsAdapter = new TvShowsAdapter(tvShowsList, this);
        binding.rvTvShows.setAdapter(tvShowsAdapter);
        binding.rvTvShows.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvTvShows.canScrollVertically(1)) {
                    if (currentPage <= totalAvailablePages) {
                        currentPage += 1;
                        getMostPopularTvShowsResponse();
                    }
                }
            }
        });
        binding.ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), WatchListActivity.class));
            }
        });
        binding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });
        getMostPopularTvShowsResponse();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getMostPopularTvShowsResponse() {
        toggleLoading();
        viewModel.getMostPopularTvShows(0).observe(this, tvShowsResponse -> {
            toggleLoading();
            if (tvShowsResponse != null) {
                totalAvailablePages = tvShowsResponse.getTotalPages();
                if (tvShowsResponse.getTvShows() != null) {
                    int oldCount = tvShowsList.size();
                    tvShowsList.addAll(tvShowsResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShowsList.size());
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (binding.getIsLoading() != null && binding.getIsLoading()) {
                binding.setIsLoading(false);    // If the data is showing in 1st page, top progress bar will be hidden
            } else {
                binding.setIsLoading(true);     // If the data is not showing in 1st page, top progress bar will be shown
            }
        } else {
            if (binding.getIsLoadingMore() != null && binding.getIsLoadingMore()) {
                binding.setIsLoadingMore(false); // If the data is showing after 1st page also, bottom progress bar will be hidden
            } else {
                binding.setIsLoadingMore(true); // If the data is not showing after 1st page, bottom progress bar will be shown
            }
        }
    }

    @Override
    public void onTvShowClicked(TvShow tvShow) {
        // Passing data from this source activity to destination activity
        Intent intent = new Intent(MainActivity.this, TvShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}