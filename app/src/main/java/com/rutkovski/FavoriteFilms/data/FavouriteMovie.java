package com.rutkovski.FavoriteFilms.data;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.rutkovski.FavoriteFilms.data.pojo.Movie;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie extends Movie {
    FavouriteMovie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String backdropPath, double voteAverage, String releaseDate) {
        super(uniqueId, id, voteCount, title, originalTitle, overview, posterPath, backdropPath, voteAverage, releaseDate);
    }

    @Ignore
    public FavouriteMovie(Movie movie) {
        super(movie.getUniqueId(), movie.getId(), movie.getVoteCount(), movie.getTitle(), movie.getOriginalTitle(), movie.getOverview(), movie.getPosterPath(), movie.getBackdropPath(), movie.getVoteAverage(), movie.getReleaseDate());
    }
}

