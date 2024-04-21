package dev.tomco.my24a_10357_l10;

import android.widget.RelativeLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    private Button buttonNavigate;
    private Button buttonSignOut;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout chartContainer;
    private RelativeLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        buttonNavigate = findViewById(R.id.buttonNavigate);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        bottomNavigationView = findViewById(R.id.navigation);
        chartContainer = findViewById(R.id.chartContainer);
        buttonLayout = findViewById(R.id.buttonLayout);  // Reference to the button layout

        Utils.setupBottomNavigation(bottomNavigationView, this,
                R.id.navigation_home, HomePageActivity.class,
                FavoriteActivity.class, HistoryActivity.class,
                TestActivity.class, TopActivity.class);

        buttonNavigate.setOnClickListener(v -> startActivity(new Intent(HomePageActivity.this, TestActivity.class)));
        buttonSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
            finish();
        });

        loadRecentChart();  // Attempt to load the most recent chart
    }

    private void loadRecentChart() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://afeka-crypto-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("results/recent");

        ref.orderByChild("userId").equalTo(userId).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // There's data, proceed with display
                    DataSnapshot lastSnapshot = dataSnapshot.getChildren().iterator().next();
                    Map<String, Object> data = (Map<String, Object>) lastSnapshot.getValue();
                    if (data.containsKey("bundleData")) {
                        Map<String, Object> bundleData = (Map<String, Object>) data.get("bundleData");
                        String strategyName = (String) data.get("strategyName");
                        double tp = ((Number) data.get("tp")).doubleValue();
                        double sl = ((Number) data.get("sl")).doubleValue();
                        double totalProfit = ((Number) data.get("totalProfit")).doubleValue();
                        double winPercentage = ((Number) bundleData.get("WinPercentage")).doubleValue();
                        Utils.createChartWithData(HomePageActivity.this, chartContainer, bundleData, strategyName, tp, sl, totalProfit, winPercentage);
                    }
                } else {
                    // No chart data found, adjust UI accordingly
                    adjustLayoutForNoChartData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HomePageActivity", "Database error: " + databaseError.getMessage());
                adjustLayoutForNoChartData();
            }
        });
    }

    private void adjustLayoutForNoChartData() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, 0); // Clear any 'below' rule that might have been set
        buttonLayout.setLayoutParams(params);

        chartContainer.setVisibility(View.GONE);
    }


}
