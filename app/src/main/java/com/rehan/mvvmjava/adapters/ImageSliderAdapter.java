package com.rehan.mvvmjava.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rehan.mvvmjava.R;
import com.rehan.mvvmjava.databinding.ItemContainerSilderImageBinding;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> {

    private String[] sliderImages;
    private LayoutInflater layoutInflater;

    public ImageSliderAdapter(String[] sliderImages) {
        this.sliderImages = sliderImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerSilderImageBinding itemContainerSilderImageBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_container_silder_image, parent, false);
        return new ViewHolder(itemContainerSilderImageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindSliderImage(sliderImages[position]);
    }

    @Override
    public int getItemCount() {
        return sliderImages.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemContainerSilderImageBinding itemContainerSilderImageBinding;

        public ViewHolder(ItemContainerSilderImageBinding itemContainerSilderImageBinding) {
            super(itemContainerSilderImageBinding.getRoot());
            this.itemContainerSilderImageBinding = itemContainerSilderImageBinding;
        }

        public void bindSliderImage(String imageURL){
            itemContainerSilderImageBinding.setImageURL(imageURL);
        }
    }
}
