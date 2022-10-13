package com.rehan.mvvmjava.responses;

import com.google.gson.annotations.SerializedName;
import com.rehan.mvvmjava.models.TvShowDetails;

public class TvShowDetailsResponse {

    @SerializedName("tvShow")
    private TvShowDetails tvShowDetails;

    public TvShowDetailsResponse(TvShowDetails tvShowDetails) {
        this.tvShowDetails = tvShowDetails;
    }

    public TvShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}
