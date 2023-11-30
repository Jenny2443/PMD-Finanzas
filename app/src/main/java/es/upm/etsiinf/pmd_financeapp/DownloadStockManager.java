package es.upm.etsiinf.pmd_financeapp;

import android.util.Log;

import java.io.IOException;

public class DownloadStockManager implements Runnable{

    private String symbol;


    public DownloadStockManager(String symbol) {
        this.symbol = symbol;
    }
    @Override
    public void run() {
        try {
            Log.println(Log.INFO, "Stocks", "Updating stock " + symbol);
            Stock stock = StockManager.getStock(symbol);
            StockManager.updateStock(stock);
            Log.println(Log.INFO, "Stocks", "Stock updated");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
