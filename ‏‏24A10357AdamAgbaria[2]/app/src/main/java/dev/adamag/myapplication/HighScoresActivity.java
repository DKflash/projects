package dev.adamag.myapplication;
import android.os.Bundle;
import dev.adamag.myapplication.Adapters.HighScoreAdapter;
import dev.adamag.myapplication.Fragments.HighScoreFragment;
import dev.adamag.myapplication.Fragments.MapLocationFragment;
import dev.adamag.myapplication.Models.HighScore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HighScoresActivity extends AppCompatActivity implements HighScoreAdapter.ScoreClickListener {
    MapLocationFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        mapFragment = new MapLocationFragment();
        HighScoreFragment scoreListFragment = new HighScoreFragment();


        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mapFragment, mapFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.scoreListFragment, scoreListFragment)
                .commit();
    }

    @Override
    public void onScoreClick(HighScore score) {
        mapFragment.choose(score.getLatitude(), score.getLongitude());
    }
}
