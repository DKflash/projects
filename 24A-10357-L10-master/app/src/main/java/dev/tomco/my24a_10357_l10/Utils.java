package dev.tomco.my24a_10357_l10;

import android.os.Bundle;

import java.util.HashMap;
import android.content.Context;

import java.util.Map;
import java.util.Set;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.cardview.widget.CardView;

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

public class Utils {
    /**
     * Converts a Bundle to a Map with String keys and Object values.
     * @param bundle The Bundle to convert.
     * @return A Map representation of the Bundle.
     */
    public static Map<String, Object> bundleToMap(Bundle bundle) {
        Map<String, Object> map = new HashMap<>();
        Set<String> keys = bundle.keySet();  // Retrieve all the keys from the bundle

        for (String key : keys) {
            Object value = bundle.get(key);  // Get the object by key

            // Check for specific types if necessary (for example, if you have custom Parcelable objects)
            // Convert Parcelable or Serializable objects appropriately if needed
            map.put(key, value);
        }

        return map;
    }

    public static void createChartWithData(Context context, ViewGroup layoutContainer, Map<String, Object> bundleData, String strategyName, double tp, double sl, double totalProfit, double winPercentage) {
        CardView cardView = new CardView(context);
        cardView.setCardElevation(8f);
        cardView.setRadius(16f);

        LinearLayout cardContainer = new LinearLayout(context);
        cardContainer.setOrientation(LinearLayout.VERTICAL);
        cardContainer.setPadding(30, 30, 30, 30);

        LineChart chart = new LineChart(context);
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>(bundleData.keySet());

        Collections.sort(dates);  // Ensure dates are sorted in chronological order

        int index = 0;
        for (String date : dates) {
            if (date.matches("\\d{4}-\\d{2}-\\d{2}") && bundleData.get(date) instanceof Number) {
                Double profit = ((Number) bundleData.get(date)).doubleValue();
                values.add(new Entry(index++, profit.floatValue()));
            }
        }

        if (!values.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(values, "Daily Profits");
            dataSet.setColor(Color.BLUE);
            dataSet.setLineWidth(2f);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);

            LineData lineData = new LineData(dataSet);
            Typeface boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

            chart.getXAxis().setTypeface(boldTypeface);
            chart.getAxisLeft().setTypeface(boldTypeface);
            chart.getAxisRight().setTypeface(boldTypeface);
            chart.setData(lineData);

            // Set the custom value formatter
            chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates) {
                @Override
                public String getFormattedValue(float value) {
                    int idx = (int) value;
                    if (idx == 0 || idx == dates.size() - 1) {  // Only label the first and the last index
                        return " 01/01-29/02";
                    }
                    return "";  // Do not label other indices
                }
            });

            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.invalidate();

            LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 800);
            chart.setLayoutParams(chartParams);
            cardContainer.addView(chart);

            // Add TextViews for additional data
            addTextToLayout(context, cardContainer, "Strategy: " + strategyName);
            addTextToLayout(context, cardContainer, "TP: " + tp + "%");
            addTextToLayout(context, cardContainer, "SL: " + sl + "%");
            addTextToLayout(context, cardContainer, "Total Profit: " + totalProfit + "%");
            addTextToLayout(context, cardContainer, "Win Percentage: " + winPercentage + "%");

            // Set Layout Params for CardView and add it to the main layout
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(30, 20, 30, 20);
            cardView.addView(cardContainer);
            cardView.setLayoutParams(cardParams);

            layoutContainer.addView(cardView);
        } else {
            Log.d("ChartUtility", "No data was added to the chart.");
        }
    }






    private static void addTextToLayout(Context context, LinearLayout layoutContainer, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        textView.setLayoutParams(layoutParams);
        layoutContainer.addView(textView);
    }

    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, Context currentContext,
                                             int currentItem, Class<?> homeActivity, Class<?> favoriteActivity,
                                             Class<?> historyActivity, Class<?> testActivity, Class<?> topActivity) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.navigation_home && !currentContext.getClass().equals(homeActivity)) {
                intent = new Intent(currentContext, homeActivity);
            } else if (item.getItemId() == R.id.navigation_fav && !currentContext.getClass().equals(favoriteActivity)) {
                intent = new Intent(currentContext, favoriteActivity);
            } else if (item.getItemId() == R.id.navigation_history && !currentContext.getClass().equals(historyActivity)) {
                intent = new Intent(currentContext, historyActivity);
            } else if (item.getItemId() == R.id.navigation_test && !currentContext.getClass().equals(testActivity)) {
                intent = new Intent(currentContext, testActivity);
            } else if (item.getItemId() == R.id.navigation_top && !currentContext.getClass().equals(topActivity)) {
                intent = new Intent(currentContext, topActivity);
            }

            if (intent != null) {
                currentContext.startActivity(intent);
                return true;
            }
            return false;
        });

    }


}
