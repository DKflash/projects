package dev.adamag.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
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
import android.content.SharedPreferences;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;



import androidx.appcompat.app.AppCompatActivity;
import dev.adamag.myapplication.Models.GameManager;
import dev.adamag.myapplication.Models.HighScore;
import dev.adamag.myapplication.Models.Obstacle;
import dev.adamag.myapplication.Models.Coin;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private GameManager gameManager;
    private Handler handler = new Handler();
    private long GAME_UPDATE_DELAY = 1000;

    final int COLUMNS = 5;

    final int ROWS = 4;

    private SharedPreferencesC sharedPreferencesC;

    private TextView scoreTextView;
    private FrameLayout gameDisplayArea;
    private ImageView playerCar;

    private ShapeableImageView heart1, heart2, heart3;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private boolean sensorControlEnabled = false;

    private LocationManager locationManager;
    private Location lastKnownLocation;

    private Runnable updateTimer;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameManager = new GameManager();

        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);
        scoreTextView = findViewById(R.id.scoreTextView);
        gameDisplayArea = findViewById(R.id.game_display_area);
        playerCar = findViewById(R.id.player_car);
        sharedPreferencesC = new SharedPreferencesC(this);


        sensorControlEnabled = getIntent().getBooleanExtra("SENSOR_CONTROL", false);
        GAME_UPDATE_DELAY = getIntent().getLongExtra("GAME_UPDATE_DELAY", 1000);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check and request location permissions at runtime
        checkAndRequestLocationPermissions();

        // Setup and request location updates
        setupLocationUpdates();
        // Hide game UI until a game mode is selected
        hideGameUI();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        startGame();
    }

    private void checkAndRequestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private void setupLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastKnownLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sensorControlEnabled) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorControlEnabled) {
            sensorManager.unregisterListener(this);
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (sensorControlEnabled && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // Left and right tilt
            float z = event.values[2]; // Forward and backward tilt

            // Adjust player movement based on left/right tilt
            if (x < -0.75) {
                gameManager.movePlayerLeft();
                updateCarPosition();
            } else if (x > 0.75) {
                gameManager.movePlayerRight();
                updateCarPosition();
            }

            // Adjust game speed based on forward/backward tilt
            if (z > 0) { // Tilting backward
                GAME_UPDATE_DELAY = 1500; // Make game slower
            } else if (z < 0) { // Tilting forward
                GAME_UPDATE_DELAY = 500; // Make game faster
            }

            updateUI(); // Update the game UI accordingly
        }
    }

    private void adjustGameSpeed() {
        handler.removeCallbacks(updateTimer);
        startGameLoop(); // Restart the game loop with the new GAME_UPDATE_DELAY
    }


    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_title);
        mediaPlayer.setOnCompletionListener(mp -> {
            // Called when the sound file has finished playing
            mp.release();
        });
        mediaPlayer.start();
    }




    private void hideGameUI() {
        // Hide game elements initially
        gameDisplayArea.setVisibility(View.INVISIBLE);
        scoreTextView.setVisibility(View.INVISIBLE);
        heart1.setVisibility(View.INVISIBLE);
        heart2.setVisibility(View.INVISIBLE);
        heart3.setVisibility(View.INVISIBLE);
        findViewById(R.id.button_move_left).setVisibility(View.INVISIBLE);
        findViewById(R.id.button_move_right).setVisibility(View.INVISIBLE);
    }

    private void startGame() {
        // Show game UI when game starts

        gameDisplayArea.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
        heart1.setVisibility(View.VISIBLE);
        heart2.setVisibility(View.VISIBLE);
        heart3.setVisibility(View.VISIBLE);
        if (sensorControlEnabled) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        else{
            findViewById(R.id.button_move_left).setVisibility(View.VISIBLE);
            findViewById(R.id.button_move_right).setVisibility(View.VISIBLE);
            setupControls();
        }

        startGameLoop();
        gameManager.setCollisionListener(() -> {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Collision Detected!", Toast.LENGTH_SHORT).show();
                vibratePhone();
                playSound();
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

        // Display obstacles
        for (Obstacle obstacle : gameManager.getObstacles()) {
            ImageView obstacleView = new ImageView(this);
            obstacleView.setImageResource(R.drawable.traffic_308786);
            FrameLayout.LayoutParams obstacleParams = new FrameLayout.LayoutParams(cellWidth, cellHeight);
            int obstacleTopMargin = obstacle.getRow() * cellHeight;
            int obstacleLeftMargin = obstacle.getColumn() * cellWidth;
            obstacleParams.setMargins(obstacleLeftMargin, obstacleTopMargin, 0, 0);
            obstacleView.setLayoutParams(obstacleParams);
            gameDisplayArea.addView(obstacleView);
        }

        // Display coins
        int cellWidthCoin = (int) (cellWidth * 0.8);
        int cellHeightCoin = (int) (cellHeight * 0.8);
        for (Coin coin : gameManager.getCoins()) { 
            ImageView coinView = new ImageView(this);
            coinView.setImageResource(R.drawable.phoenix_1292958);
            FrameLayout.LayoutParams coinParams = new FrameLayout.LayoutParams(cellWidthCoin, cellHeightCoin);
            int coinTopMargin = coin.getRow() * cellHeight;
            int coinLeftMargin = coin.getColumn() * cellWidth;
            coinParams.setMargins(coinLeftMargin, coinTopMargin, 0, 0);
            coinView.setLayoutParams(coinParams);
            gameDisplayArea.addView(coinView);
        }
    }


    private void startGameLoop() {
       handler.postDelayed(
               updateTimer = new Runnable() {
            @Override
            public void run() {
                if (!gameManager.isGameOver()) {
                    gameManager.updateGame();
                    updateUI();
                    displayObstacles();
                    gameManager.getPlayer().addScore(5); 
                    handler.postDelayed(this, GAME_UPDATE_DELAY);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                        gameOver(gameManager.getScore());

                    });

                }
            }
        }, GAME_UPDATE_DELAY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop the timer
        handler.removeCallbacks(updateTimer);
    }

    private void gameOver(int Score) {
        saveHighScore(Score);
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void saveHighScore(int Score) {
        if (lastKnownLocation != null) {
            double longitude = lastKnownLocation.getLongitude();
            double latitude = lastKnownLocation.getLatitude();
            HighScore score = new HighScore(Score, longitude, latitude);
            if (sharedPreferencesC.addScore(score)) {
                Toast.makeText(this, "New high score!: " + Score, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Score: " + Score, Toast.LENGTH_LONG).show();
            }
            // Proceed to save the score along with the location
        } else {
            double longitude = 10; //default
            double latitude = 10;
            HighScore score = new HighScore(Score, longitude, latitude);
            if (sharedPreferencesC.addScore(score)) {
                Toast.makeText(this, "New high score!: " + Score, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Score: " + Score, Toast.LENGTH_LONG).show();
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Clean up any scheduled tasks
    }



}

