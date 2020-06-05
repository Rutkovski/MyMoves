package com.rutkovski.FavoriteFilms.data.pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListTrailer {

    @SerializedName("results")
    @Expose
    private List<Trailer> trailers;

    public List<Trailer> getResults() {
        return trailers;
    }

    public void setResults(List<Trailer> results) {
        this.trailers = results;
    }
}
