package es.upm.etsiinf.pmd_financeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class DownloadStockManager implements Runnable{

    private Stock stock;
    private Context context;


    public DownloadStockManager(Context context, Stock stock) {
        this.context = context;
        this.stock = stock;
    }
    @Override
    public void run() {
        try {
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
