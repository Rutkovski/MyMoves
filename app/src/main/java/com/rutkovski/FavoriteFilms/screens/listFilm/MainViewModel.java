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
import static com.rutkovski.NetworkConstants.API_KEY;
import static com.rutkovski.NetworkConstants.MIN_VOTE_COUNT_VALUE;
import static com.rutkovski.NetworkConstants.POPULARITY;
import static com.rutkovski.NetworkConstants.SORT_BY_POPULARITY;
import static com.rutkovski.NetworkConstants.SORT_BY_TOP_RATED;

public class MainViewModel extends AndroidViewModel {

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


    @SuppressWarnings("unchecked")
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
                    public void accept(ListMovies listMovies)  {
                        List<Movie> movies = listMovies.getMovies();

                        if (page == 1) {
                            deleteAllMovies();
                        }
                        insertListMovie(movies);
                        page++;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        error.setValue(getApplication().getString(R.string.error_load));
                    }
                });

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }


    private static class InsertAllTask extends AsyncTask<List<Movie>, Void, Void> {
        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Movie>... movies) {
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
