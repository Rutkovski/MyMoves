package com.rutkovski.FavoriteFilms.screens.listFilm;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rutkovski.FavoriteFilms.R;
import com.rutkovski.FavoriteFilms.api.ApiFactory;
import com.rutkovski.FavoriteFilms.api.ApiService;
import com.rutkovski.FavoriteFilms.data.MovieDatabase;
import com.rutkovski.FavoriteFilms.data.pojo.ListMovies;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    static final int POPULARITY = 0;
    static final int TOP_RATED = 1;
    private static final String API_KEY = "62444a9c5ef6026706af098905a6867b";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final int MIN_VOTE_COUNT_VALUE = 1000;
    private static String lang = Locale.getDefault().getLanguage();
    private static int page = 1;
    private static int lastSortBy;
    private CompositeDisposable compositeDisposable;

    private static MovieDatabase database;
    private LiveData<List<Movie>> movies;

    private MutableLiveData<String> error;


    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        error = new MutableLiveData<>();
    }

    MutableLiveData<String> getError() {
        return error;
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    void clearError() {
        error.setValue(null);
    }

    private void deleteAllMovies() {
        new DeleteMoviesTask().execute();
    }

    private void insertListMovie(List<Movie> movies) {
        new InsertAllTask().execute(movies);
    }



    void loadDate(int sortBy) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }
        if (lastSortBy != sortBy) {
            page = 1;
            lastSortBy = sortBy;
        }
        Disposable disposable = apiService.getMovies(API_KEY, lang, methodOfSort, MIN_VOTE_COUNT_VALUE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ListMovies>() {
                    @Override
                    public void accept(ListMovies listMovies) throws Exception {
                        List<Movie> movies = listMovies.getMovies();

                        if (page == 1) {
                            deleteAllMovies();
                        }
                        insertListMovie(movies);
                        page++;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        error.setValue(getApplication().getString(R.string.error_load));
                    }
                });

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }


    private static class InsertAllTask extends AsyncTask<List<Movie>, Void, Void> {
        @Override
        protected Void doInBackground(List<Movie>... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertAllMovies(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... integers) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }


}
