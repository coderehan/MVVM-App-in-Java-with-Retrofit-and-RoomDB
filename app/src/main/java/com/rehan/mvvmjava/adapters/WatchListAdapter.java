package com.rehan.mvvmjava.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rehan.mvvmjava.R;
import com.rehan.mvvmjava.databinding.ItemLayoutTvShowsBinding;
import com.rehan.mvvmjava.listeners.WatchListListener;
import com.rehan.mvvmjava.models.TvShow;

import java.util.List;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {

    private final List<TvShow> tvShows;
    private WatchListListener watchListListener;
    private LayoutInflater layoutInflater;

    public WatchListAdapter(List<TvShow> tvShows, WatchListListener watchListListener) {
        this.tvShows = tvShows;
        this.watchListListener = watchListListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemLayoutTvShowsBinding itemLayoutTvShowsBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_layout_tv_shows, parent, false);
        return new ViewHolder(itemLayoutTvShowsBinding);
    }

    // No need of setting text from here. Data is already bind in XML views itself.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTvShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // This is how we generate DataBinding class for custom_layout in RecyclerView Adapter

        private final ItemLayoutTvShowsBinding itemLayoutTvShowsBinding;

        public ViewHolder(ItemLayoutTvShowsBinding itemLayoutTvShowsBinding) {
            super(itemLayoutTvShowsBinding.getRoot());
            this.itemLayoutTvShowsBinding = itemLayoutTvShowsBinding;
        }

        public void bindTvShow(TvShow tvShow) {
            itemLayoutTvShowsBinding.setTvShow(tvShow);     // This set method is used to access the data binding in XML.
            itemLayoutTvShowsBinding.executePendingBindings();
            itemLayoutTvShowsBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchListListener.onTvShowClicked(tvShow);
                }
            });
            itemLayoutTvShowsBinding.ivDelete.setVisibility(View.VISIBLE);
            itemLayoutTvShowsBinding.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchListListener.removeTvShowFromWatchList(tvShow, getAdapterPosition());
                }
            });
        }
    }
}
