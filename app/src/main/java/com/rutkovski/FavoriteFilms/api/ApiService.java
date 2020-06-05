package com.rutkovski.FavoriteFilms.api;
import com.rutkovski.FavoriteFilms.data.pojo.ListMovies;
import com.rutkovski.FavoriteFilms.data.pojo.ListReview;
import com.rutkovski.FavoriteFilms.data.pojo.ListTrailer;
import com.rutkovski.NetworkConstants;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET(NetworkConstants.BASE_URL_MOVIE)
    Observable<ListMovies> getMovies(@Query(NetworkConstants.PARAMS_API_KEY) String api_key,
                                     @Query(NetworkConstants.PARAMS_LANGUAGE) String lang,
                                     @Query(NetworkConstants.PARAMS_SORT_BY) String sort,
                                     @Query(NetworkConstants.PARAMS_MIN_VOTE_COUNT) int minVoteCount,
                                     @Query(NetworkConstants.PARAMS_PAGE) int page);
    @GET(NetworkConstants.BASE_URL_REVIEWS)
    Observable<ListReview> getReview(@Path(NetworkConstants.PARAMS_ID) int id, @Query(NetworkConstants.PARAMS_LANGUAGE) String lang, @Query(NetworkConstants.PARAMS_API_KEY) String api_key);

    @GET(NetworkConstants.BASE_URL_VIDEOS)
    Observable<ListTrailer> getTrailers(@Path(NetworkConstants.PARAMS_ID) int id, @Query(NetworkConstants.PARAMS_API_KEY) String api_key, @Query(NetworkConstants.PARAMS_LANGUAGE) String lang);

}
