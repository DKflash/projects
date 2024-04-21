package dev.tomco.my24a_10357_l10.Backtest;

import java.util.List;

public class TradeStrategies {

    public TradeAction evaluateVolumeStrategy(FinancialData data) {
        double buyThreshold = data.quoteVolume * 0.6;
        double sellThreshold = data.quoteVolume * 0.4;

        if (data.takerBuyQuoteVolume > buyThreshold) {
            return TradeAction.BUY;
        } else if (data.takerBuyQuoteVolume < sellThreshold) {
            return TradeAction.SELL;
        } else {
            return TradeAction.HOLD;
        }
    }

    public TradeAction evaluateOverBoughtSoldStrategy(FinancialData data) {
        // Example implementation of OverBought-Sold logic
        if (data.close > data.high * 0.95) { // If close price is near the high price, it may be overbought
            return TradeAction.SELL;
        } else if (data.close < data.low * 1.05) { // If close price is near the low price, it may be oversold
            return TradeAction.BUY;
        }
        return TradeAction.HOLD;
    }

}
