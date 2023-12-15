package es.upm.etsiinf.pmd_financeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class DownloadStockManager implements Runnable{

    private String symbol;
    private Context context;


    public DownloadStockManager(Context context, String symbol) {
        this.context = context;
        this.symbol = symbol;
    }
    @Override
    public void run() {
        try {
            Log.println(Log.INFO, "Stocks", "Updating stock " + symbol);
            Stock stock = StockManager.getStock(symbol, context);
            if(StockManager.updateStock(stock, context)) {
                Log.println(Log.INFO, "Stocks", "Stock updated");
            } else {
                Log.println(Log.INFO, "Stocks", "Stock not updated");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showToast(String message) {
        // Utilizar runOnUiThread para mostrar Toast desde un hilo secundario
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(() -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
