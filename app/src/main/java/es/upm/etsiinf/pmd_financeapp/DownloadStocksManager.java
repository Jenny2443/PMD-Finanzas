package es.upm.etsiinf.pmd_financeapp;

import java.io.IOException;

public class DownloadStocksManager implements Runnable{

    private static final String API_KEY = "uAoBHhszznSW8IO1YhZhMhfcUfGuBE4p";
    @Override
    public void run() {
        //No funciona
        try {
            StockManager.updateStocks(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
