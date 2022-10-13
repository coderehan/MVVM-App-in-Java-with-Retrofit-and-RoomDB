package com.rehan.mvvmjava.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rehan.mvvmjava.R;
import com.rehan.mvvmjava.adapters.EpisodesAdapter;
import com.rehan.mvvmjava.adapters.ImageSliderAdapter;
import com.rehan.mvvmjava.common.Common;
import com.rehan.mvvmjava.databinding.ActivityTvShowDetailsBinding;
import com.rehan.mvvmjava.databinding.LayoutEpisodesBottomSheetBinding;
import com.rehan.mvvmjava.models.TvShow;
import com.rehan.mvvmjava.viewmodels.TvShowDetailsViewModel;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TvShowDetailsActivity extends AppCompatActivity {

    private ActivityTvShowDetailsBinding binding;
    private TvShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog bottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding bottomSheetBinding;
    private TvShow tvShow;
    private Boolean isTvShowAvailableInWatchList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tv_show_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TvShowDetailsViewModel.class);
        binding.ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvShow = (TvShow) getIntent().getSerializableExtra(Common.NAME);
        checkTvShowInWatchList();
        getTvShowDetails();
    }

    private void checkTvShowInWatchList() {
        // Implementing RxJva
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTvShowFromWatchList(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow -> {
                    isTvShowAvailableInWatchList = true;
                    binding.ivWatchList.setImageResource(R.drawable.icon_check);
                    compositeDisposable.dispose();
                }));
    }

    private void getTvShowDetails() {
        binding.setIsLoading(true);     // First it will show progress bar before showing data
        String tvShowId = String.valueOf(tvShow.getId());

        tvShowDetailsViewModel.getTvShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            binding.setIsLoading(false);    // Once data is loaded and shown, progress bar will be hidden
            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }
                binding.setTvShowImageURL(tvShowDetailsResponse.getTvShowDetails().getImagePath());
                binding.ivTvShow.setVisibility(View.VISIBLE);
                binding.setDescription(String.valueOf(HtmlCompat.fromHtml(tvShowDetailsResponse.getTvShowDetails().getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                binding.tvDescription.setVisibility(View.VISIBLE);
                binding.tvReadMore.setVisibility(View.VISIBLE);
                binding.tvReadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding.tvReadMore.getText().toString().equals("Read More")) {
                            binding.tvDescription.setMaxLines(Integer.MAX_VALUE);
                            binding.tvDescription.setEllipsize(null);
                            binding.tvReadMore.setText(R.string.read_less);
                        } else {
                            binding.tvDescription.setMaxLines(4);
                            binding.tvDescription.setEllipsize(TextUtils.TruncateAt.END);
                            binding.tvReadMore.setText(R.string.read_more);
                        }
                    }
                });
                binding.setRating(String.format(Locale.getDefault(), "%.2f", Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())));
                if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                    binding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    binding.setGenre("N/A");
                }
                binding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                binding.viewDivider1.setVisibility(View.VISIBLE);
                binding.llMisc.setVisibility(View.VISIBLE);
                binding.viewDivider2.setVisibility(View.VISIBLE);
                binding.btnWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                        startActivity(intent);
                    }
                });
                binding.btnWebsite.setVisibility(View.VISIBLE);
                binding.btnEpisodes.setVisibility(View.VISIBLE);
                binding.btnEpisodes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bottomSheetDialog == null) {
                            bottomSheetDialog = new BottomSheetDialog(TvShowDetailsActivity.this);
                            bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(TvShowDetailsActivity.this), R.layout.layout_episodes_bottom_sheet, findViewById(R.id.llHeader), false);
                            bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
                            bottomSheetBinding.rvEpisodes.setAdapter(new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes()));
                            bottomSheetBinding.tvTitle.setText(String.format("Episodes | %s", tvShow.getName()));
                            bottomSheetBinding.ivClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bottomSheetDialog.dismiss();
                                }
                            });
                        }
                    }
                });

                // Implementing RxJava
                binding.ivWatchList.setOnClickListener(new View.OnClickListener() {      // When eye floating button is clicked, it will turn into check mark
                    @Override
                    public void onClick(View view) {
                        CompositeDisposable compositeDisposable = new CompositeDisposable();
                        if (isTvShowAvailableInWatchList) {
                            compositeDisposable.add(tvShowDetailsViewModel.removeTvShowFromWatchList(tvShow)
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        isTvShowAvailableInWatchList = false;
                                        Common.IS_WATCHLIST_UPDATED = true;
                                        binding.ivWatchList.setImageResource(R.drawable.icon_eye);
                                        Toast.makeText(TvShowDetailsActivity.this, "Removed from watchlist", Toast.LENGTH_SHORT).show();
                                        compositeDisposable.dispose();
                                    }));
                        } else {
                            compositeDisposable.add(tvShowDetailsViewModel.addToWatchList(tvShow)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Common.IS_WATCHLIST_UPDATED = true;
                                        binding.ivWatchList.setImageResource(R.drawable.icon_check);
                                        Toast.makeText(TvShowDetailsActivity.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                                        compositeDisposable.dispose();
                                    })
                            );
                        }

                    }
                });
                binding.ivWatchList.setVisibility(View.VISIBLE);
                loadBasicTvShowDetails();
            }
        });
    }

    private void loadImageSlider(String[] sliderImages) {
        binding.sliderViewPager.setOffscreenPageLimit(1);
        binding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        binding.sliderViewPager.setVisibility(View.VISIBLE);
        binding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderImages.length);
        binding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_slider_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            binding.llSliderIndicators.addView(indicators[i]);
        }
        binding.llSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = binding.llSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.llSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_slider_indicator_inactive));
            }
        }
    }

    // Getting data from source activity and setting in this destination activity
    private void loadBasicTvShowDetails() {
        binding.setTvShowName(tvShow.getName());
        binding.setNetworkCountry(tvShow.getNetwork() + "(" + tvShow.getCountry() + ")");
        binding.setStatus(tvShow.getStatus());
        binding.setStartedDate(tvShow.getStartDate());
        binding.tvName.setVisibility(View.VISIBLE);
        binding.tvNetworkCountry.setVisibility(View.VISIBLE);
        binding.tvStatus.setVisibility(View.VISIBLE);
        binding.tvStartedDate.setVisibility(View.VISIBLE);
    }
}