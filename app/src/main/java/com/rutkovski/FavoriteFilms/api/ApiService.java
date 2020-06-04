package com.rutkovski.FavoriteFilms.api;


import com.rutkovski.FavoriteFilms.data.pojo.ListMovies;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("discover/movie")
    Observable<ListMovies> getMovies(@Query("api_key") String api_key, @Query("lang") String lang, @Query("sort_by") String sort,
                                            @Query("vote_count.gte") int minVoteCount, @Query("page") int page);

}
