package com.rutkovski.FavoriteFilms.screens.listFilm;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rutkovski.FavoriteFilms.R;
import com.rutkovski.FavoriteFilms.adapters.MovieAdapter;
import com.rutkovski.FavoriteFilms.data.pojo.Movie;
import com.rutkovski.FavoriteFilms.screens.detail.DetailActivity;
import com.rutkovski.FavoriteFilms.screens.listFavoriteFilm.FavouriteActivity;

import java.util.List;

import static com.rutkovski.NetworkConstants.POPULARITY;
import static com.rutkovski.NetworkConstants.TOP_RATED;

public class MainActivity extends AppCompatActivity {
    private static int methodOfSort;
    private static boolean isLoading = false;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private ProgressBar progressBarLoading;
    private MainViewModel viewModel;
    private GridLayoutManager gridLayoutManager;



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
        return Math.max(width / 185, 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        RecyclerView recyclerViewPosters = findViewById(R.id.RecyclerViewPosters);
        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        gridLayoutManager = new GridLayoutManager(this, getColumnCount());
        recyclerViewPosters.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);

        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setMethodSort(b);
            }
        });
        if (savedInstanceState != null) {
            gridLayoutManager.scrollToPosition(savedInstanceState.getInt("adapterPosition", 0));
        } else {
            switchSort.setChecked(false);
        }


        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setMovies(movies);
                progressBarLoading.setVisibility(View.INVISIBLE);
                isLoading = false;
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    viewModel.clearError();
                    progressBarLoading.setVisibility(View.INVISIBLE);
                    isLoading = false;
                }
            }
        });

        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd(int position) {
                if (!isLoading) {
                    progressBarLoading.setVisibility(View.VISIBLE);
                    viewModel.loadDate(methodOfSort);
                    isLoading = true;
                }
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
    }


    public void onClickSetPopularity(View view) {
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        switchSort.setChecked(true);
    }

    private void setMethodSort(boolean isTopRated) {
        if (isTopRated) {
            methodOfSort = TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
        } else {
            methodOfSort = POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
        }
        progressBarLoading.setVisibility(View.VISIBLE);
        viewModel.loadDate(methodOfSort);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("adapterPosition", gridLayoutManager.findFirstVisibleItemPosition());
    }
}
