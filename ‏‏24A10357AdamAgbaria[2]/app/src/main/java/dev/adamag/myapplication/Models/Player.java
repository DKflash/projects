package dev.adamag.myapplication.Models;

public class Player {
    private int column;
    private final int row;
    private int lives;
    private int score;

    public Player(int row, int startColumn) {
        this.row = row;
        this.column = startColumn;
        this.lives = 3;
        this.score = 0;
    }

    public void moveLeft() {
        if (column > 0) {
            this.column--;
        }
    }

    public void moveRight() {
        if (column < 2) {
            this.column++;
        }
    }

    public int getColumn() {
    return column;
}

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void loseLife() {
        if (lives > 0) {
            this.lives--;
        }
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void reset() {
        this.column = 1;
        this.lives = 3;
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void resetScore() {
        this.score = 0;
    }
}
