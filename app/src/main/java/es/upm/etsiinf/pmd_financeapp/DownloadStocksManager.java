package es.upm.etsiinf.pmd_financeapp;

import java.io.IOException;

public class DownloadStocksManager implements Runnable{

    private static final String API_KEY = "uAoBHhszznSW8IO1YhZhMhfcUfGuBE4p";
    @Override
    public void run() {
        try {
            StockManager.updateStocks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
