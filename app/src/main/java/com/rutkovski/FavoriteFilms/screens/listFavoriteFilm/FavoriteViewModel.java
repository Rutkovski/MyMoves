package com.rutkovski.FavoriteFilms.screens.listFavoriteFilm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rutkovski.FavoriteFilms.data.FavouriteMovie;
import com.rutkovski.FavoriteFilms.data.MovieDatabase;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private LiveData<List<FavouriteMovie>> favouriteMovies;


    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(getApplication());
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
    }
    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

}
