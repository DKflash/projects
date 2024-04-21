package dev.tomco.my24a_10357_l10.Backtest;

class Trade {
    double entryPrice;
    double stopLoss;
    double takeProfit;
    TradeAction action; // BUY or SELL

    public Trade(double entryPrice, double stopLoss, double takeProfit, TradeAction action) {
        this.entryPrice = entryPrice;
        this.stopLoss = stopLoss;
        this.takeProfit = takeProfit;
        this.action = action;
    }
}
