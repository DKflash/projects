package dev.tomco.my24a_10357_l10.Backtest;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileHandler {
    private Context context;
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);




    public FileHandler(Context context) {
        this.context = context;
    }

    public List<FinancialData> readCsvFromAssets(String fileName, String startDateStr, String endDateStr) {
        List<FinancialData> dataList = new ArrayList<>();
        try {
            Date startDate = parseDate(startDateStr);
            Date endDate = parseDate(endDateStr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                Date openTime = parseDate(tokens[0]);
                if (!openTime.before(startDate) && !openTime.after(endDate)) {
                    FinancialData data = new FinancialData(
                            tokens[0], // Open time as string
                            Double.parseDouble(tokens[1]),
                            Double.parseDouble(tokens[2]),
                            Double.parseDouble(tokens[3]),
                            Double.parseDouble(tokens[4]),
                            Double.parseDouble(tokens[5]),
                            tokens[6], // Close time as string
                            Double.parseDouble(tokens[7]),
                            Integer.parseInt(tokens[8]),
                            Double.parseDouble(tokens[9]),
                            Double.parseDouble(tokens[10]),
                            tokens[11]
                    );
                    dataList.add(data);
                }
            }
            reader.close();
        } catch (IOException ex) {
            Log.e("FileHandler", "Error reading CSV file", ex);
        }
        return dataList;
    }

    // Assuming this method in FileHandler processes the CSV data
    public Date parseDate(String dateString) {
        try {
            return dateFormat1.parse(dateString);
        } catch (ParseException e) {
            try {
                return dateFormat2.parse(dateString);
            } catch (ParseException ex) {
                Log.e("FileHandler", "Failed to parse date: " + dateString, ex);
                return null;  // Or handle more appropriately
            }
        }
    }

}

class FinancialData {
    String openTime;
    double open;
    double high;
    double low;
    double close;
    double volume;
    String closeTime;
    double quoteVolume;
    int count;
    double takerBuyVolume;
    double takerBuyQuoteVolume;
    String ignore;

    public FinancialData(String openTime, double open, double high, double low, double close, double volume,
                         String closeTime, double quoteVolume, int count, double takerBuyVolume,
                         double takerBuyQuoteVolume, String ignore) {
        this.openTime = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.closeTime = closeTime;
        this.quoteVolume = quoteVolume;
        this.count = count;
        this.takerBuyVolume = takerBuyVolume;
        this.takerBuyQuoteVolume = takerBuyQuoteVolume;
        this.ignore = ignore;
    }
}
