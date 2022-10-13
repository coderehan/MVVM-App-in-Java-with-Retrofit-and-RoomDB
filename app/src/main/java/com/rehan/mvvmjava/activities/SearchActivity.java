package com.rehan.mvvmjava.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.rehan.mvvmjava.R;
import com.rehan.mvvmjava.adapters.TvShowsAdapter;
import com.rehan.mvvmjava.databinding.ActivitySearchBinding;
import com.rehan.mvvmjava.listeners.TvShowsListener;
import com.rehan.mvvmjava.models.TvShow;
import com.rehan.mvvmjava.viewmodels.SearchTvShowViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TvShowsListener {

    private ActivitySearchBinding binding;
    private SearchTvShowViewModel searchTvShowViewModel;
    private List<TvShow> tvShows = new ArrayList<>();
    private TvShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        doInitialization();
    }

    private void doInitialization() {
        searchTvShowViewModel = new ViewModelProvider(this).get(SearchTvShowViewModel.class);
        binding.ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.rvSearch.setHasFixedSize(true);
        tvShowsAdapter = new TvShowsAdapter(tvShows, this);
        binding.rvSearch.setAdapter(tvShowsAdapter);
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    currentPage = 1;
                                    totalAvailablePages = 1;
                                    searchTvShow(editable.toString());
                                }
                            });
                        }
                    }, 800);
                } else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });
        binding.rvSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvSearch.canScrollVertically(1)) {
                    if (!binding.etSearch.getText().toString().isEmpty()) {
                        if (currentPage < totalAvailablePages) {
                            currentPage += 1;
                            searchTvShow(binding.etSearch.getText().toString());
                        }
                    }
                }
            }
        });
        binding.etSearch.requestFocus();
    }

    private void searchTvShow(String query) {
        toggleLoading();
        searchTvShowViewModel.searchTvShow(query, currentPage).observe(this, tvShowsResponse -> {
            toggleLoading();
            if (tvShowsResponse != null) {
                totalAvailablePages = tvShowsResponse.getTotalPages();
                if (tvShowsResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();
                    tvShows.addAll(tvShowsResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
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
        Intent intent = new Intent(SearchActivity.this, TvShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}