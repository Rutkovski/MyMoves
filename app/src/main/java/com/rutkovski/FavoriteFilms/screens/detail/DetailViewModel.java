package com.rutkovski.FavoriteFilms.screens.detail;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.rutkovski.FavoriteFilms.api.ApiFactory;
import com.rutkovski.FavoriteFilms.api.ApiService;
import com.rutkovski.FavoriteFilms.data.FavouriteMovie;
import com.rutkovski.FavoriteFilms.data.MovieDatabase;
import com.rutkovski.FavoriteFilms.data.pojo.ListReview;
import com.rutkovski.FavoriteFilms.data.pojo.ListTrailer;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;
import com.rutkovski.FavoriteFilms.data.pojo.Review;
import com.rutkovski.FavoriteFilms.data.pojo.Trailer;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class DetailViewModel extends AndroidViewModel {
    private static final String API_KEY = "62444a9c5ef6026706af098905a6867b";
    private static String lang = Locale.getDefault().getLanguage();
    private static MovieDatabase database;
    private MutableLiveData<List<Trailer>> trailersLiveData;
    private MutableLiveData<List<Review>> reviewsLiveData;


    public DetailViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        trailersLiveData = new MutableLiveData<>();
        reviewsLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<List<Trailer>> getTrailersLiveData() {
        return trailersLiveData;
    }

    public MutableLiveData<List<Review>> getReviewsLiveData() {
        return reviewsLiveData;
    }

    boolean isFavorite(int id){
        return getFavouriteMovieById(id) != null;
    }

    Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    FavouriteMovie getFavouriteMovieById(int id) {
        try {
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    void insertFavouriteMovie(FavouriteMovie movie) {
        new InsertFavouriteTask().execute(movie);
    }

    void deleteFavouriteMovieById (int id) {
        new DeleteFavouriteTaskById().execute(id);
    }


    void loadDate(int id) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        Disposable disposable = apiService.getTrailers(id, API_KEY, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer <ListTrailer>() {
                    @Override
                    public void accept(ListTrailer listTrailers) throws Exception {
                        trailersLiveData.setValue(listTrailers.getResults());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("test", "ошибка в трейлерах" + throwable.toString());

                    }
                });
        Disposable disposable2 = apiService.getReview(id,lang,API_KEY).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer <ListReview>() {
                    @Override
                    public void accept(ListReview listReviews) throws Exception {
                       reviewsLiveData.setValue(listReviews.getReviews());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("test", "ошибка в отзывах" + throwable.toString());

                    }
                });

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
        compositeDisposable.add(disposable2);
    }


    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class GetFavouriteMovieTask extends AsyncTask<Integer, Void, FavouriteMovie> {
        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class InsertFavouriteTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertFavouriteMovie(movies[0]);
            }
            return null;
        }
    }


    private static class DeleteFavouriteTaskById extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                database.movieDao().deleteFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }


}
