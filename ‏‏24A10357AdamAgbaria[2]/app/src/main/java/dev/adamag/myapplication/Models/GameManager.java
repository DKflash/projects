package dev.adamag.myapplication.Models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameManager {
    private static final int ROWS = 5;
    private static final int COLUMNS = 5;
    private Player player;
    private List<Obstacle> obstacles;
    private List<Coin> coins;

    private boolean isGameOver;

    public GameManager() {
        this.player = new Player(ROWS - 1, 0);
        this.obstacles = new ArrayList<>();
        this.coins = new ArrayList<>();
        this.isGameOver = false;
        startGame();
    }

    public void startGame() {
        obstacles.clear();
        player.reset();
        isGameOver = false;
    }

    public void updateGame() {
        if (!isGameOver) {
            moveObstacles();
            moveCoins();
            checkCollisions();
            generateObstacle();
        } else {
            restartGame();
        }
    }

    private void moveObstacles() {
        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.moveDown();
            if (obstacle.getRow() >= ROWS) {
                iterator.remove();
            }
        }
    }

    private void moveCoins() {
        Iterator<Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            Coin coin = iterator.next();
            coin.moveDown();
            if (coin.getRow() >= ROWS) {
                iterator.remove();
            }
        }
    }

    public interface CollisionListener {
        void onCollisionDetected();
    }

    private CollisionListener collisionListener;

    public void setCollisionListener(CollisionListener listener) {
        this.collisionListener = listener;
    }

    private void checkCollisions() {
        // Check collision with obstacles
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getRow() == player.getRow() && obstacle.getColumn() == player.getColumn()) {
                player.loseLife();
                if (!player.isAlive()) {
                    isGameOver = true;
                }
                if (collisionListener != null) {
                    collisionListener.onCollisionDetected();
                }
                break;
            }
        }

        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            if (coin.getRow() == player.getRow() && coin.getColumn() == player.getColumn()) {
                coinIterator.remove();
                player.addScore(20);
            }
        }
    }
    private void generateObstacle() {
        Random random = new Random();
        if (random.nextInt(4) == 0) {
            int column = random.nextInt(COLUMNS);
            obstacles.add(new Obstacle(0, column));
            return;
        }

        if (random.nextInt(3) == 0) { // Different frequency for coin appearance
            int column = random.nextInt(COLUMNS);
            coins.add(new Coin(0, column));
        }
    }

    public void movePlayerLeft() {
        if (player.getColumn() > 0) {
            player.setColumn(player.getColumn() - 1);
        }
    }

    public void movePlayerRight() {
        if (player.getColumn() < COLUMNS - 1) {
            player.setColumn(player.getColumn() + 1);
        }
    }



    private void restartGame() {
        startGame();
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getScore() {
        return player.getScore();
    }

    public Player getPlayer() {
        return player;
    }

    public List<Obstacle> getObstacles() {
        return new ArrayList<>(obstacles);
    }
    public List<Coin> getCoins() {
        return new ArrayList<>(coins);
    }
}
