package com.rutkovski.FavoriteFilms.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rutkovski.FavoriteFilms.data.pojo.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    @Query("SELECT * FROM movies WHERE id == :movieId")
    Movie getMovieById(int movieId);

    @Query("SELECT * FROM favourite_movies WHERE id == :movieId")
    FavouriteMovie getFavouriteMovieById(int movieId);

    @Query("DELETE FROM movies")
    void deleteAllMovies();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMovies(List<Movie> movies);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavouriteMovie(FavouriteMovie movie);

    @Query("DELETE FROM favourite_movies WHERE id == :id")
    void deleteFavouriteMovieById(int id);
}