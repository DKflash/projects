package dev.tomco.my24a_10357_l10;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;


import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultActivity extends AppCompatActivity {

    private LineChart chart;
    private TextView totalGainTextView;
    private TextView winPercentageTextView;
    private Button btnSaveToFavorites;



    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialize the views
        chart = findViewById(R.id.chart);
        totalGainTextView = findViewById(R.id.tvTotalGain);
        winPercentageTextView = findViewById(R.id.tvWinPercentage);
        btnSaveToFavorites = findViewById(R.id.btnSaveToFavorites);
        bottomNavigationView = findViewById(R.id.navigation);

        // Get the results passed from the previous activity
        Bundle results = getIntent().getBundleExtra("Results");
        if (results != null) {
            setupChartWithData(results);
            displayResults(results);
        }
        Utils.setupBottomNavigation(bottomNavigationView, this,
                R.id.navigation_test, HomePageActivity.class,
                FavoriteActivity.class, HistoryActivity.class,
                TestActivity.class, TopActivity.class);
        // Setup favorite button listener
        btnSaveToFavorites.setOnClickListener(v -> saveToFavorites(results));
    }

    private void saveToFavorites(Bundle results) {
        if (results == null) {
            Log.e("Firebase", "No results to save.");
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://afeka-crypto-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("results");

        DatabaseReference favoriteRef = ref.child("favorites");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String strategyName = getIntent().getStringExtra("StrategyName");
        double tp = getIntent().getDoubleExtra("TP", 0);
        double sl = getIntent().getDoubleExtra("SL", 0);
        double value = results.getDouble("TotalGain", 0);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("strategyName", strategyName);
        resultData.put("tp", tp);
        resultData.put("sl", sl);
        resultData.put("userId", userId);
        resultData.put("totalProfit", value);
        resultData.put("bundleData",  Utils.bundleToMap(results));

        // Save to Firebase under the 'favorites' node
        favoriteRef.push().setValue(resultData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Result saved to favorites successfully");
                    btnSaveToFavorites.setText("Saved!");
                    btnSaveToFavorites.setEnabled(false);
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to save to favorites", e));
    }


    private void setupChartWithData(Bundle results) {
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        int index = 0;

        Log.d("ChartDebug", "Total keys in bundle: " + results.keySet().size());

        for (String key : results.keySet()) {
            if (key.matches("\\d{4}-\\d{2}-\\d{2}")) {  // Ensure this matches your date format
                double profit = results.getDouble(key, 0.0);
                dates.add(key);
                values.add(new Entry(index++, (float) profit));
            }
        }

        if (!values.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(values, "Daily Profits");
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setLineWidth(2f);
//            dataSet.setHighLightColor(Color.GREEN);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);  // Disable circle indicators

            LineData lineData = new LineData(dataSet);
            Typeface boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

            // Apply the typeface to the X and Y axes
            chart.getXAxis().setTypeface(boldTypeface);
            chart.getAxisLeft().setTypeface(boldTypeface);
            chart.getAxisRight().setTypeface(boldTypeface);

            chart.setData(lineData);

            final int labelCount = 4; // We want exactly four labels
            int step = Math.max(1, dates.size() / labelCount);

            chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates) {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index % step == 0 && index < dates.size()) {
                        return dates.get(index);
                    }
                    return "";
                }
            });

            chart.getXAxis().setGranularity(step); // Set granularity to step to ensure spacing
            chart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setGranularityEnabled(true); // Enable granularity
            chart.getXAxis().setDrawLabels(true);

            chart.setScaleEnabled(true);
            chart.setDragEnabled(true);
//            chart.setPinchZoom(true);

            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(true);

            chart.invalidate();
        } else {
            Log.d("ChartDebug", "No data was added to the chart.");
        }
    }




    private void displayResults(Bundle results) {
        double totalGain = results.getDouble("TotalGain");
        double winPercentage = results.getDouble("WinPercentage");

        totalGainTextView.setText(String.format("Total Gain: %.2f%%", totalGain));
        winPercentageTextView.setText(String.format("Trades Won: %.2f%%", winPercentage));
    }


}
