package com.rutkovski.FavoriteFilms.screens.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rutkovski.FavoriteFilms.R;
import com.rutkovski.FavoriteFilms.adapters.ReviewAdapter;
import com.rutkovski.FavoriteFilms.adapters.TrailerAdapter;
import com.rutkovski.FavoriteFilms.data.FavouriteMovie;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;
import com.rutkovski.FavoriteFilms.data.pojo.Review;
import com.rutkovski.FavoriteFilms.data.pojo.Trailer;
import com.rutkovski.FavoriteFilms.screens.listFavoriteFilm.FavouriteActivity;
import com.rutkovski.FavoriteFilms.screens.listFilm.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewAddToFavourite;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private boolean isFavorite;
    private int id;
    private DetailViewModel viewModel;
    private Movie movie;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
            movie = viewModel.getMovieById(id);
        } else if (intent != null && intent.hasExtra("idFavourite")) {
            id = intent.getIntExtra("idFavourite", -1);
            movie = viewModel.getFavouriteMovieById(id);
        } else {
            finish();
        }

        isFavorite = viewModel.isFavorite(id);
        ImageView imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        TextView textViewRating = findViewById(R.id.textViewRating);
        TextView textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        TextView textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        ScrollView scrollViewInfo = findViewById(R.id.scrollViewInfo);
        RecyclerView recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        RecyclerView recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.cinema).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(String.format(Locale.getDefault(),"%.1f",movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);

        isFavorite = viewModel.isFavorite(id);
        setFavourite();
        viewModel.loadDate(movie.getId());
        viewModel.getReviewsLiveData().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                reviewAdapter.setReviews(reviews);
            }
        });
        viewModel.getTrailersLiveData().observe(this, new Observer<List<Trailer>>() {
            @Override
            public void onChanged(List<Trailer> trailers) {
                trailerAdapter.setTrailers(trailers);
            }
        });

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        scrollViewInfo.smoothScrollTo(0, 0);
    }


    public void onCLickChangeFavourite(View view) {
        if (!isFavorite) {
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovieById(id);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        isFavorite = !isFavorite;
        setFavourite();
    }

    private void setFavourite() {
        if (!isFavorite) {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }


}
