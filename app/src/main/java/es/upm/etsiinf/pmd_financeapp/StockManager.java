package es.upm.etsiinf.pmd_financeapp;

import java.time.LocalDateTime;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;

public class StockManager {
    private static final String API_KEY = "uAoBHhszznSW8IO1YhZhMhfcUfGuBE4p";

    public static String getApiKey() {
        return API_KEY;
    }

    public static void updateStocks(Stock stock) {
        //Make the API connection and update the stock
        if (stock == null) {
            System.out.println("Stock is null");
            return;
        }
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("Make sure you set your polygon API key in the POLYGON_API_KEY environment variable!");
            System.exit(1);
        }
        PolygonRestClient client = new PolygonRestClient(API_KEY);
        final MarketsDTO markets = client.getReferenceClient().getSupportedMarketsBlocking();


        int newPrice = 0;
        stock.setPrice(newPrice);
        LocalDateTime now = LocalDateTime.now();
        stock.setLastUpdate(now);

    }






}
