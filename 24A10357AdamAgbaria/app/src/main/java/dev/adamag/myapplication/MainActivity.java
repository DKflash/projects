package dev.adamag.myapplication;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.appcompat.app.AppCompatActivity;
import dev.adamag.myapplication.Models.GameManager;
import dev.adamag.myapplication.Models.Obstacle;

public class MainActivity extends AppCompatActivity {
    private GameManager gameManager;
    private Handler handler = new Handler();
    private final long GAME_UPDATE_DELAY = 1000;

    final int COLUMNS = 3;

    final int ROWS = 4;

    private TextView scoreTextView;
    private FrameLayout gameDisplayArea;
    private ImageView playerCar;

    private ShapeableImageView heart1, heart2, heart3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameManager = new GameManager();


        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);
        updateLivesDisplay(gameManager.getPlayer().getLives());


        scoreTextView = findViewById(R.id.scoreTextView);
        gameDisplayArea = findViewById(R.id.game_display_area);
        playerCar = findViewById(R.id.player_car);


        setupControls();
        startGameLoop();
        gameManager.setCollisionListener(() -> {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Collision Detected!", Toast.LENGTH_SHORT).show();
                vibratePhone();
                updateLivesDisplay(gameManager.getPlayer().getLives());
            });
        });
    }

    private void vibratePhone() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));

        }
    }

    private void updateLivesDisplay(int lives) {
        heart1.setVisibility(lives >= 1 ? View.VISIBLE : View.INVISIBLE);
        heart2.setVisibility(lives >= 2 ? View.VISIBLE : View.INVISIBLE);
        heart3.setVisibility(lives >= 3 ? View.VISIBLE : View.INVISIBLE);
    }

    private void setupControls() {
        Button moveLeftButton = findViewById(R.id.button_move_left);
        Button moveRightButton = findViewById(R.id.button_move_right);

        moveLeftButton.setOnClickListener(v -> {
            gameManager.movePlayerLeft();
            updateCarPosition();
            updateUI();
        });

        moveRightButton.setOnClickListener(v -> {
            gameManager.movePlayerRight();
            updateCarPosition();
            updateUI();
        });
    }

    private void updateUI() {
        scoreTextView.setText("Score: " + gameManager.getScore());
    }

    private void updateCarPosition() {
        int columnWidth = gameDisplayArea.getWidth() / COLUMNS;
        int playerColumn = gameManager.getPlayer().getColumn();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerCar.getLayoutParams();
        params.leftMargin = playerColumn * columnWidth;
        playerCar.setLayoutParams(params);

    }





    private void displayObstacles() {
        gameDisplayArea.removeAllViews();
        gameDisplayArea.addView(playerCar);

        int cellWidth = gameDisplayArea.getWidth() / COLUMNS;
        int cellHeight = gameDisplayArea.getHeight() / ROWS;

        for (Obstacle obstacle : gameManager.getObstacles()) {
            ImageView obstacleView = new ImageView(this);
            obstacleView.setImageResource(R.drawable.obstacle);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cellWidth, cellHeight);

            int topMargin = obstacle.getRow() * cellHeight;

            int leftMargin = obstacle.getColumn() * cellWidth;

            params.setMargins(leftMargin, topMargin, 0, 0);

            obstacleView.setLayoutParams(params);
            gameDisplayArea.addView(obstacleView);
        }
    }


    private void startGameLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!gameManager.isGameOver()) {
                    gameManager.updateGame();
                    updateUI();
                    displayObstacles(); // Update obstacles' positions and display them
                    handler.postDelayed(this, GAME_UPDATE_DELAY);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                    });

                    handler.postDelayed(() -> {
                        gameManager.startGame();
                        updateLivesDisplay(gameManager.getPlayer().getLives());
                        updateUI();
                        startGameLoop();
                    }, 2000);

                }
            }
        }, GAME_UPDATE_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Clean up any scheduled tasks
    }


}

