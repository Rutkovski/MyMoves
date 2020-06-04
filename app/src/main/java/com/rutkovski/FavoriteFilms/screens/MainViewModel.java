package com.rutkovski.FavoriteFilms.screens;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rutkovski.FavoriteFilms.api.ApiFactory;
import com.rutkovski.FavoriteFilms.api.ApiService;
import com.rutkovski.FavoriteFilms.data.FavouriteMovie;
import com.rutkovski.FavoriteFilms.data.MovieDatabase;
import com.rutkovski.FavoriteFilms.data.pojo.ListMovies;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;
    private static final String API_KEY = "62444a9c5ef6026706af098905a6867b";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final int MIN_VOTE_COUNT_VALUE = 1000;
    private static String lang = Locale.getDefault().getLanguage();

    private static MovieDatabase database;
    private CompositeDisposable compositeDisposable;
    private LiveData<List<Movie>> movies;
    private MutableLiveData<List<Movie>> newMovies;

    private LiveData<List<FavouriteMovie>> favouriteMovies;

    public LiveData<List<Movie>> getNewMovies() {
        return newMovies;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
        newMovies = new MutableLiveData<>();
    }



    public Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FavouriteMovie getFavouriteMovieById(int id) {
        try {
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public void deleteAllMovies() {
        new DeleteMoviesTask().execute();
    }

    public void insertMovie(Movie movie) {
        new InsertTask().execute(movie);
    }

    public void insertListMovie(List <Movie> movies) {
        new InsertAllTask().execute(movies);
    }

    public void deleteMovie(Movie movie) {
        new DeleteTask().execute(movie);
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void insertFavouriteMovie(FavouriteMovie movie) {
        new InsertFavouriteTask().execute(movie);
    }

    public void deleteFavouriteMovie(FavouriteMovie movie) {
        new DeleteFavouriteTask().execute(movie);
    }

    public void loadDate(int sortBy, final int page) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        String methodOfSort;

        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }


        Disposable disposable = apiService.getMovies(API_KEY,lang,methodOfSort,MIN_VOTE_COUNT_VALUE,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ListMovies>() {
                    @Override
                    public void accept(ListMovies listMovies) throws Exception {
                        List <Movie> movies =  listMovies.getMovies();
                        Log.i("test", "Закончили загрузку, размер"+movies.size());
                        if (page == 1) {
                            deleteAllMovies();
                        }
                        newMovies.setValue(movies);
                        insertListMovie(movies);

                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("test", "снова ошибки");
                        Log.i("test", ""+throwable.toString());

                    }
                });
//                .subscribe(new Consumer<List<Movie>>() {
//                    @Override
//                    public void accept(List<Movie> movies) throws Exception {
//                        deleteAllMovies();
//                        for (Movie movie : movies) {
//                            insertMovie(movie);
//                        }
//
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Toast.makeText(getApplication(), ""+throwable.toString(), Toast.LENGTH_SHORT).show();
//                        Log.i("test", throwable.toString());
//                       // errors.setValue(throwable);
//                    }
//                });*/
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }

    private static class DeleteFavouriteTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteFavouriteMovie(movies[0]);
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

    private static class DeleteTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertAllTask extends AsyncTask<List <Movie>, Void, Void> {
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


}
