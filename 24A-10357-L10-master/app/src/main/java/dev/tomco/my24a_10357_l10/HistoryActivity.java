package dev.tomco.my24a_10357_l10;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;


public class HistoryActivity extends AppCompatActivity {
    private LinearLayout layoutContainer;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        layoutContainer = findViewById(R.id.layoutContainer);
        bottomNavigationView = findViewById(R.id.navigation);

        if (layoutContainer == null) {
            Log.e("FavoriteActivity", "Layout container is not found!");
            return; // Early return if layout container is not initialized
        }
        Utils.setupBottomNavigation(bottomNavigationView, this,
                R.id.navigation_history, HomePageActivity.class,
                FavoriteActivity.class, HistoryActivity.class,
                TestActivity.class, TopActivity.class);
        loadHistoryResults();

    }

    private void loadHistoryResults() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://afeka-crypto-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("results/recent");

        ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("RecentActivity", "No favorite data found.");
                    return; // Early return if no data
                }
                ArrayList<Map<String, Object>> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    dataList.add(data);
                }

                // Reverse the dataList to show the newest entries first
                Collections.reverse(dataList);

                for (Map<String, Object> data : dataList) {
                    if (data != null && data.containsKey("bundleData")) {
                        Map<String, Object> bundleData = (Map<String, Object>) data.get("bundleData");
                        double tp = data.containsKey("tp") ? ((Number) data.get("tp")).doubleValue() : 0.0;
                        double sl = data.containsKey("sl") ? ((Number) data.get("sl")).doubleValue() : 0.0;
                        double totalProfit = data.containsKey("totalProfit") ? ((Number) data.get("totalProfit")).doubleValue() : 0.0;
                        double winPercentage = bundleData.containsKey("WinPercentage") ? ((Number) bundleData.get("WinPercentage")).doubleValue() : 0.0;
                        String strategyName = (String) data.get("strategyName");

                        if (bundleData != null && !bundleData.isEmpty()) {
                            Utils.createChartWithData(HistoryActivity.this, layoutContainer, bundleData, strategyName, tp, sl, totalProfit, winPercentage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HistoryActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }






}