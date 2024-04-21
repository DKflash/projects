package dev.tomco.my24a_10357_l10;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;


public class TopActivity extends AppCompatActivity {
    private LinearLayout layoutContainer;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        layoutContainer = findViewById(R.id.layoutContainer);
        bottomNavigationView = findViewById(R.id.navigation);

        if (layoutContainer == null) {
            Log.e("TopActivity", "Layout container is not found!");
            return; // Early return if layout container is not initialized
        }
        Utils.setupBottomNavigation(bottomNavigationView, this,
                R.id.navigation_fav, HomePageActivity.class,
                FavoriteActivity.class, HistoryActivity.class,
                TestActivity.class, TopActivity.class);
        loadTopResults();

    }

    private void loadTopResults() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://afeka-crypto-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("results/top");

        ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("TopActivity", "No top data found.");
                    return;
                }

                List<ProfitEntry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    if (data != null && data.containsKey("bundleData")) {
                        Map<String, Object> bundleData = (Map<String, Object>) data.get("bundleData");
                        double tp = ((Number) data.getOrDefault("tp", 0.0)).doubleValue();
                        double sl = ((Number) data.getOrDefault("sl", 0.0)).doubleValue();
                        double totalProfit = ((Number) data.getOrDefault("totalProfit", 0.0)).doubleValue();
                        double winPercentage = bundleData.containsKey("WinPercentage") ? ((Number) bundleData.get("WinPercentage")).doubleValue() : 0.0;
                        String strategyName = (String) data.get("strategyName");

                        if (bundleData != null && !bundleData.isEmpty()) {
                            entries.add(new ProfitEntry(bundleData, strategyName, tp, sl, totalProfit, winPercentage));
                        }
                    }
                }

                // Sort entries by totalProfit
                Collections.sort(entries);

                // Create charts for each entry
                for (ProfitEntry entry : entries) {
                    Utils.createChartWithData(TopActivity.this, layoutContainer, entry.bundleData, entry.strategyName, entry.tp, entry.sl, entry.totalProfit, entry.winPercentage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TopActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }



    public class ProfitEntry implements Comparable<ProfitEntry> {
        Map<String, Object> bundleData;
        String strategyName;
        double tp, sl, totalProfit, winPercentage;

        public ProfitEntry(Map<String, Object> bundleData, String strategyName, double tp, double sl, double totalProfit, double winPercentage) {
            this.bundleData = bundleData;
            this.strategyName = strategyName;
            this.tp = tp;
            this.sl = sl;
            this.totalProfit = totalProfit;
            this.winPercentage = winPercentage;
        }

        @Override
        public int compareTo(ProfitEntry o) {
            return Double.compare(o.totalProfit, this.totalProfit); // Sort descending
        }
    }


}
