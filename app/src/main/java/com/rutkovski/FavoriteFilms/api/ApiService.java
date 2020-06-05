package com.rutkovski.FavoriteFilms.api;


import android.net.Uri;

import com.rutkovski.FavoriteFilms.data.pojo.ListMovies;
import com.rutkovski.FavoriteFilms.data.pojo.ListReview;
import com.rutkovski.FavoriteFilms.data.pojo.ListTrailer;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;
import com.rutkovski.FavoriteFilms.data.pojo.Review;
import com.rutkovski.FavoriteFilms.data.pojo.Trailer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {





    @GET("discover/movie")
    Observable<ListMovies> getMovies(@Query("api_key") String api_key, @Query("language") String lang, @Query("sort_by") String sort,
                                            @Query("vote_count.gte") int minVoteCount, @Query("page") int page);

    @GET("movie/{id}/reviews")
    Observable<ListReview> getReview (@Path("id") int id, @Query("language") String lang, @Query("api_key") String api_key);

    @GET("movie/{id}/videos")
    Observable<ListTrailer> getTrailers(@Path("id") int id, @Query("api_key") String api_key, @Query("language") String lang);

}
