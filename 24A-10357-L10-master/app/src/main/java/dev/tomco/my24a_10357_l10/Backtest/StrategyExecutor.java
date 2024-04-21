package dev.tomco.my24a_10357_l10.Backtest;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;

public class StrategyExecutor {
    private Context context;
    private FileHandler fileHandler;
    private TradeStrategies tradeStrategies;
    private Trade openTrade = null;
    private double slPercentage;
    private double tpPercentage;
    private double totalProfit = 0;
    private int winCount = 0;
    private int tradeCount = 0;
    private String strategyName; // Add strategy name field

    private Map<String, Double> dailyProfits = new HashMap<>();

    public StrategyExecutor(Context context, double slPercentage, double tpPercentage, String strategyName) {
        this.context = context;
        this.fileHandler = new FileHandler(context);
        this.tradeStrategies = new TradeStrategies();
        this.slPercentage = slPercentage;
        this.tpPercentage = tpPercentage;
        this.strategyName = strategyName; // Initialize strategy name
    }

    public void executeStrategy(String fileName, String startDateStr, String endDateStr) {
        List<FinancialData> data = fileHandler.readCsvFromAssets(fileName, startDateStr, endDateStr);
        String previousDate = "";
        double lastProfit = 0;

        for (FinancialData entry : data) {
            String currentDate = entry.closeTime.split(" ")[0];

            if (!currentDate.equals(previousDate)) {
                lastProfit = dailyProfits.getOrDefault(previousDate, 0.0);
            }
            previousDate = currentDate;

            if (openTrade != null && checkTradeClosure(entry)) {
                double profit = calculateProfit(entry);
                totalProfit += profit;
                tradeCount++;
                if (profit > 0) {
                    winCount++;
                }
                dailyProfits.put(currentDate, dailyProfits.getOrDefault(currentDate, lastProfit) + profit);
                openTrade = null;
            }
            if (openTrade == null) {
                TradeAction action;
                if ("Buying Pressure".equals(strategyName)) {
                    action = tradeStrategies.evaluateVolumeStrategy(entry);
                } else if ("OverBought-Sold".equals(strategyName)) {
                    action = tradeStrategies.evaluateOverBoughtSoldStrategy(entry);
                } else {
                    action = TradeAction.HOLD;
                }
                if (action != TradeAction.HOLD) {
                    openTrade = new Trade(entry.close, calculateSL(entry, action), calculateTP(entry, action), action);
                }
            }
        }
        totalProfit = lastProfit; // Consider updating this to handle strategy results correctly
    }





    private boolean checkTradeClosure(FinancialData data) {
        if (openTrade.action == TradeAction.BUY) {
            return data.low <= openTrade.stopLoss || data.high >= openTrade.takeProfit;
        } else {
            return data.high >= openTrade.stopLoss || data.low <= openTrade.takeProfit;
        }
    }

    private double calculateProfit(FinancialData data) {
        if (openTrade.action == TradeAction.BUY) {
            if (data.low <= openTrade.stopLoss) {
                // Trade closed at stop loss
                return -slPercentage;  // Return negative SL percentage indicating a loss
            } else if (data.high >= openTrade.takeProfit) {
                // Trade closed at take profit
                return tpPercentage;  // Return positive TP percentage indicating a gain
            }
        } else if (openTrade.action == TradeAction.SELL) {
            if (data.high >= openTrade.stopLoss) {
                // Trade closed at stop loss
                return -slPercentage;  // Return negative SL percentage indicating a loss
            } else if (data.low <= openTrade.takeProfit) {
                // Trade closed at take profit
                return tpPercentage;  // Return positive TP percentage indicating a gain
            }
        }
        return 0;  // In case the trade hasn't hit TP or SL, no profit or loss
    }

    public Bundle getResultsBundle() {
        Bundle bundle = new Bundle();
        bundle.putDouble("TotalGain", totalProfit);
        bundle.putDouble("WinPercentage", (double) winCount / tradeCount * 100);
//        bundle.putDouble("tp", this.tpPercentage);
//        bundle.putDouble("sl", this.slPercentage);
//        bundle.putString("StrategyName", this.strategyName);


        // Assuming dailyProfits is a Map<String, Double>
        for (Map.Entry<String, Double> entry : dailyProfits.entrySet()) {
            bundle.putDouble(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    private double calculateSL(FinancialData data, TradeAction action) {
        return action == TradeAction.BUY ? data.close * (1 - slPercentage / 100) : data.close * (1 + slPercentage / 100);
    }

    private double calculateTP(FinancialData data, TradeAction action) {
        return action == TradeAction.BUY ? data.close * (1 + tpPercentage / 100) : data.close * (1 - tpPercentage / 100);
    }

    private void logResults() {
        Log.d("StrategyExecutor", "Total Profit: " + totalProfit + "%");
        double winPercentage = tradeCount > 0 ? (double) winCount / tradeCount * 100 : 0;
        Log.d("StrategyExecutor", "Winning Trades Percentage: " + winPercentage + "%");
        for (Map.Entry<String, Double> entry : dailyProfits.entrySet()) {
            Log.d("StrategyExecutor", "Profit on " + entry.getKey() + ": " + entry.getValue() + "%");
        }
    }
}
