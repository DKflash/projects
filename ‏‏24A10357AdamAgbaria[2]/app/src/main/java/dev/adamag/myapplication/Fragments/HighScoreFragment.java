package dev.adamag.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dev.adamag.myapplication.Adapters.HighScoreAdapter;
import dev.adamag.myapplication.Models.HighScore;

import dev.adamag.myapplication.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.adamag.myapplication.HighScoresActivity;
import dev.adamag.myapplication.SharedPreferencesC;

public class HighScoreFragment extends Fragment {

    private SharedPreferencesC sharedPrefManager;

    private RecyclerView scoresRecycView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.highscore_container_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefManager = new SharedPreferencesC(getContext());
        scoresRecycView = view.findViewById(R.id.scoresRecycView);
        scoresRecycView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<HighScore> scores = sharedPrefManager.getScores();
        HighScoreAdapter adapter = new HighScoreAdapter(scores, (HighScoresActivity) getActivity());
        scoresRecycView.setAdapter(adapter);
    }
}