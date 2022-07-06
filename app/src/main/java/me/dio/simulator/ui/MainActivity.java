package me.dio.simulator.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

import me.dio.simulator.R;
import me.dio.simulator.data.MatchesApi;
import me.dio.simulator.databinding.ActivityMainBinding;
import me.dio.simulator.domain.Match;
import me.dio.simulator.ui.adapter.MatchesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MatchesApi matchesApi;
    private MatchesAdapter matchesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpHttpClient();
        setUpMatchesList();
        setUpMatchesRefresh();
        setUpFloatingActionButton();
    }

    private void setUpHttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pedro-afk.github.io/matches-simulator-api/")
                // serialização  com a lib do google Gson
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        matchesApi = retrofit.create(MatchesApi.class);
    }

    private void setUpMatchesList() {
        binding.rvMatches.setHasFixedSize(true);
        binding.rvMatches.setLayoutManager(new LinearLayoutManager(this));
        findMatchesFromApi();
    }


    private void setUpMatchesRefresh() {
        binding.srlMatches.setOnRefreshListener(this::findMatchesFromApi);
    }

    private void setUpFloatingActionButton() {
        binding.fabSimulate.setOnClickListener(view -> {
            view.animate().rotationBy(360).setDuration(800).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Random random = new Random();
                    for (int i = 0; i < matchesAdapter.getItemCount(); i++) {
                        Match match = MatchesAdapter.getMatches().get(i);
                        match.getHomeTeam().setScore(random.nextInt(match.getHomeTeam().getStars() + 1));
                        match.getAwayteam().setScore(random.nextInt(match.getAwayteam().getStars() + 1));
                        matchesAdapter.notifyItemChanged(i);
                    }
                    super.onAnimationEnd(animation);
                }
            });
        });
    }

    private void findMatchesFromApi() {
        binding.srlMatches.setRefreshing(true);
        matchesApi.getMatches().enqueue(
                new Callback<List<Match>>() {
                    @Override
                    public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                        if (response.isSuccessful()) {
                            List<Match> matches = response.body();
                            matchesAdapter = new MatchesAdapter(matches);
                            binding.rvMatches.setAdapter(matchesAdapter);
                        } else {
                            showErrorMessage();
                        }
                        binding.srlMatches.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<List<Match>> call, Throwable t) {
                        binding.srlMatches.setRefreshing(false);
                        showErrorMessage();
                    }
                }
        );
    }

    private void showErrorMessage() {
        Snackbar.make(binding.fabSimulate, R.string.error_api, Snackbar.LENGTH_LONG).show();
    }
}
