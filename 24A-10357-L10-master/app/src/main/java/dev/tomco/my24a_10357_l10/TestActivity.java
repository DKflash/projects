package dev.tomco.my24a_10357_l10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.tomco.my24a_10357_l10.Backtest.StrategyExecutor;

public class TestActivity extends AppCompatActivity {

    private Spinner spinnerStrategy;
    private EditText etTP, etSL;
    private Button btnExecuteStrategy;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        spinnerStrategy = findViewById(R.id.spinnerStrategy);
        etTP = findViewById(R.id.etTP);
        etSL = findViewById(R.id.etSL);
        btnExecuteStrategy = findViewById(R.id.btnExecuteStrategy);
        bottomNavigationView = findViewById(R.id.navigation);

        setupSpinner();
        setupButtonListener();
        Utils.setupBottomNavigation(bottomNavigationView, this,
                R.id.navigation_test, HomePageActivity.class,
                FavoriteActivity.class, HistoryActivity.class,
                TestActivity.class, TopActivity.class);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strategy_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrategy.setAdapter(adapter);
    }

    private void setupButtonListener() {
        btnExecuteStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeStrategyWithProgressDialog();
            }
        });
    }

    private void executeStrategyWithProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(TestActivity.this);
        progressDialog.setMessage("Executing Strategy...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String strategyName = spinnerStrategy.getSelectedItem().toString();
        double tp = Double.parseDouble(etTP.getText().toString());
        double sl = Double.parseDouble(etSL.getText().toString());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getMainLooper());

        executor.execute(() -> {
            StrategyExecutor strategyExecutor = new StrategyExecutor(TestActivity.this, sl, tp, strategyName);
            strategyExecutor.executeStrategy("BNB15mdata.csv", "01/01/2024", "29/02/2024");

            handler.post(() -> {
                progressDialog.dismiss();
                Bundle results = strategyExecutor.getResultsBundle();
                Intent intent = new Intent(TestActivity.this, ResultActivity.class);
                intent.putExtra("Results", results);
                intent.putExtra("StrategyName", strategyName);
                intent.putExtra("TP", tp);
                intent.putExtra("SL", sl);
                saveResultsToFirebase(results, strategyName, tp, sl);
                startActivity(intent);
            });
        });
    }


    private void saveResultsToFirebase(Bundle results, String strategyName, double tp, double sl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://afeka-crypto-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("results");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        double totalProfit = results.getDouble("TotalGain");
        Log.d("Firebase", "Total Profit: " + totalProfit);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("strategyName", strategyName);
        resultData.put("tp", tp);
        resultData.put("sl", sl);
        resultData.put("userId", userId);
        resultData.put("totalProfit", totalProfit);
        Map<String, Object> bundleMap = Utils.bundleToMap(results);
        resultData.put("bundleData", bundleMap);

        // Handle "recent" node updates
        DatabaseReference recentRef = ref.child("recent");
        manageRecentEntries(recentRef, resultData);

        // Handle "top" node updates
        DatabaseReference topRef = ref.child("top");
        manageTopEntries(topRef, resultData, totalProfit);
    }

    private void manageRecentEntries(DatabaseReference recentRef, Map<String, Object> resultData) {
        recentRef.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() >= 10) {
                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                    firstChild.getRef().removeValue(); // Remove the oldest entry
                }
                recentRef.push().setValue(resultData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DB_ERROR", "Failed to manage recent entries.", databaseError.toException());
            }
        });
    }

    private void manageTopEntries(DatabaseReference topRef, Map<String, Object> resultData, double totalProfit) {
        topRef.orderByChild("totalProfit").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 10) {
                    // If less than 10 entries, simply add the new result
                    topRef.push().setValue(resultData);
                } else {
                    // If there are already 10 entries, we need to determine if the new one qualifies
                    double lowestProfitInTop = Double.MAX_VALUE;
                    DataSnapshot leastProfitableEntry = null;

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        double profit = child.child("totalProfit").getValue(Double.class);
                        if (profit < lowestProfitInTop) {
                            lowestProfitInTop = profit;
                            leastProfitableEntry = child;
                        }
                    }

                    // Check if the new entry has a higher profit than the lowest in the top 10
                    if (totalProfit > lowestProfitInTop) {
                        // Remove the least profitable and add the new one
                        if (leastProfitableEntry != null) {
                            leastProfitableEntry.getRef().removeValue();
                            topRef.push().setValue(resultData);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DB_ERROR", "Failed to manage top entries.", databaseError.toException());
            }
        });
    }






}