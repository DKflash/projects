package dev.adamag.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        Button btnSlow = findViewById(R.id.button_slow);
        Button btnFast = findViewById(R.id.button_fast);
        Button btnSensors = findViewById(R.id.button_sensors);
        Button btnHighScore = findViewById(R.id.button_highScore);

        btnSlow.setOnClickListener(v -> startGame(false, 1500));
        btnFast.setOnClickListener(v -> startGame(false, 500));
        btnSensors.setOnClickListener(v -> startGame(true, 1000)); // Assuming default speed for sensor mode
        btnHighScore.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HighScoresActivity.class);
            startActivity(intent);
        });
    }

    private void startGame(boolean sensorControl, long gameUpdateDelay) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra("SENSOR_CONTROL", sensorControl);
        intent.putExtra("GAME_UPDATE_DELAY", gameUpdateDelay);
        startActivity(intent);
    }
}
