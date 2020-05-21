package com.rutkovski.FavoriteFilms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rutkovski.FavoriteFilms.adapters.ReviewAdapter;
import com.rutkovski.FavoriteFilms.adapters.TrailerAdapter;
import com.rutkovski.FavoriteFilms.data.FavouriteMovie;
import com.rutkovski.FavoriteFilms.data.MainViewModel;
import com.rutkovski.FavoriteFilms.data.Movie;
import com.rutkovski.FavoriteFilms.data.Review;
import com.rutkovski.FavoriteFilms.data.Trailer;
import com.rutkovski.FavoriteFilms.utils.JSONUtils;
import com.rutkovski.FavoriteFilms.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ImageView imageViewAddToFavourite;
    private FavouriteMovie favouriteMovie;
    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private ScrollView scrollViewInfo;
    private static String lang;

    private int id;
    private MainViewModel viewModel;
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
        switch (id){
            case R.id.itemMain:
                Intent intent = new Intent(this,MainActivity.class);
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
        if (actionBar!=null){
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lang = Locale.getDefault().getLanguage();
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        final Intent intent = getIntent();
        if(intent != null && intent.hasExtra("id")){
            id = intent.getIntExtra("id",-1);
            movie = viewModel.getMovieById(id);
        }
        else if(intent != null && intent.hasExtra("idFavourite")){
            id = intent.getIntExtra("idFavourite",-1);
            movie = viewModel.getFavouriteMovieById(id);
        } else {
            finish();
        }


        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.cinema).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());


        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(),lang);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(),lang);
        ArrayList<Trailer> trailers =JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Review>  reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
        scrollViewInfo.smoothScrollTo(0,0);

    }

    @Override
    protected void onStart() {
        setFavourite();
        super.onStart();
    }

    public void onCLickChangeFavourite(View view) {

        if (favouriteMovie == null){
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }
    private void setFavourite(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }

    }
}
