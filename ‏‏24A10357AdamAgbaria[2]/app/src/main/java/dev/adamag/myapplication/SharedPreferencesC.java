package dev.adamag.myapplication;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import dev.adamag.myapplication.Models.HighScore;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SharedPreferencesC {

    private SharedPreferences shareP;

    private final Gson g = new Gson();

    public SharedPreferencesC(Context context) {
        shareP = context.getSharedPreferences("dev.adamag.myapplication", Context.MODE_PRIVATE);
    }


    public boolean addScore(HighScore score) {
        List<HighScore> allScores = getScores();
        // Find the position where the new score should be inserted
        int position = -1;
        for (int i = 0; i < allScores.size(); i++) {
            if (score.getScore() > allScores.get(i).getScore()) {
                position = i;
                break;
            }
        }

        // If the score is higher than any in the list or there's room for more scores
        if (position != -1 || allScores.size() < 10) {
            if (position == -1) {
                // If the new score is lower than any existing scores but there's room, add it to the end
                allScores.add(score);
            } else {
                // Insert the new score at the found position
                allScores.add(position, score);
            }

            // Ensure only the top 10 scores are kept
            while (allScores.size() > 10) {
                allScores.remove(allScores.size() - 1);
            }

            saveScores(allScores); // Implement this method to save the list back to SharedPreferences
            return true;
        }

        return false; // Indicates the score was not high enough to be added to the list
    }

    private void saveScores(List<HighScore> allScores) {

        Set<String> scores_strings = allScores.stream()
                .map(g::toJson)
                .collect(Collectors.toSet());

        shareP.edit().putStringSet("scores", scores_strings)
                .apply();

    }

    public List<HighScore> getScores() {
        Set<String> scores_strings = shareP.getStringSet("scores", new HashSet<>());
        List<HighScore> scores = new ArrayList<>();
        for (String scoreJson : scores_strings) {
            scores.add(g.fromJson(scoreJson, HighScore.class));
        }
        Collections.sort(scores);
        return scores;
    }

}
