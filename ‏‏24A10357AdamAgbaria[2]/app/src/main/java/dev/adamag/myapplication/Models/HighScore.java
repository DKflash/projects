package dev.adamag.myapplication.Models;

import java.io.Serializable;

public class HighScore implements Comparable<HighScore> {
    private int score;
    private double latitude;
    private double longitude;

    public HighScore(int score, double latitude, double longitude) {
        this.score = score;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getScore() {
        return score;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    @Override
    public int compareTo(HighScore score) {
        return Integer.compare(score.getScore(), getScore());
    }
}
