package dev.adamag.myapplication.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.adamag.myapplication.Models.HighScore;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import dev.adamag.myapplication.R;


public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ScoreViewHolder> {



    public interface ScoreClickListener {
        void onScoreClick(HighScore score);
    }
    private List<HighScore> HighScoreList;
    private ScoreClickListener scoreClickListener;

    public HighScoreAdapter(List<HighScore> HighScoreList, ScoreClickListener scoreClickListener) {
        this.HighScoreList = HighScoreList;
        this.scoreClickListener = scoreClickListener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscore_index,parent,false);
        return new ScoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        HighScore score = HighScoreList.get(position);
        holder.populate(score);
    }

    @Override
    public int getItemCount() {
        return HighScoreList.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {

        private TextView locationTextView;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);

            locationTextView = itemView.findViewById(R.id.locationTextView);
        }

        public void populate(HighScore score) {

            locationTextView.setText("Longitude: " + score.getLongitude() + ", Latitude: " + score.getLatitude());
            itemView.setOnClickListener(v -> scoreClickListener.onScoreClick(score));
        }
    }
}

