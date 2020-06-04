package com.rutkovski.FavoriteFilms.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rutkovski.FavoriteFilms.R;
import com.rutkovski.FavoriteFilms.adapters.MovieAdapter;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;
import com.rutkovski.FavoriteFilms.utils.NetworkUtils;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static int methodOfSort;
    private static boolean isLoading = true;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private ProgressBar progressBarLoading;
    private MainViewModel viewModel;
    private int page;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem main = menu.findItem(R.id.itemMain);
        main.setVisible(false);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);

        return width / 185 > 2 ? width / 185 : 2;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        recyclerViewPosters = findViewById(R.id.RecyclerViewPosters);
        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);

        switchSort.setChecked(true);

        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                page = 1;
                setMethodSort(b);
            }
        });
        switchSort.setChecked(false);



        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
//                if(page == 1){
//                Log.i("test", "Устанавливаем фильмы в адаптер" + movies.size());
//                movieAdapter.setMovies(movies);
//                progressBarLoading.setVisibility(View.INVISIBLE);
//                isLoading =false;
//                }
            }
        });


        viewModel.getNewMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                Log.i("test", "Устанавливаем фильмы в адаптер" + movies.size());

                movieAdapter.addMovies(movies);
                progressBarLoading.setVisibility(View.INVISIBLE);
                page++;
                isLoading=false;
            }
        });



        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if(!isLoading) {
                    Log.i("test", "блок слушателя конца списка");/*
                    progressBarLoading.setVisibility(View.VISIBLE);
                    viewModel.loadDate(methodOfSort, page);*/
                }
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodSort(boolean isTopRated) {
        if (isTopRated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
        }
        Log.i("test", "сработал переключатель");
        movieAdapter.clear();
        progressBarLoading.setVisibility(View.VISIBLE);
        viewModel.loadDate(methodOfSort,page);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
