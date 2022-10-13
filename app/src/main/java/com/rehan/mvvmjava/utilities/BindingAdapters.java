package com.rehan.mvvmjava.utilities;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BindingAdapters {

    // As we are fetching image from remote server, we are handling that image from remote and showing into imageview

    @BindingAdapter("android:imageURL") // We will use this android:imageURL attribute in our ImageView in XML
    public static void setImageURL(ImageView imageView, String URL){
        try {
            imageView.setAlpha(0f);
            Picasso.get().load(URL).noFade().into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageView.animate().setDuration(300).alpha(1f).start();
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
