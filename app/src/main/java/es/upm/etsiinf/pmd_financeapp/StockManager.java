package es.upm.etsiinf.pmd_financeapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.net.URI;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;

public class StockManager {
    private static final String API_KEY = "uAoBHhszznSW8IO1YhZhMhfcUfGuBE4p";

    public static String getApiKey() {
        return API_KEY;
    }

    public static void updateStocks(Stock stock) throws IOException {
        //Make the API connection and update the stock
        if (stock == null) {
            System.out.println("Stock is null");
            return;
        }
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("Make sure you set your polygon API key in the POLYGON_API_KEY environment variable!");
            System.exit(1);
        }
        String url = "https://api.polygon.io/v1/open-close/" + stock.getSymbol() + "/2023-11-24?adjusted=true&apiKey=" + API_KEY;

        URLConnection con = null;
        try {
            URL url1 = new URL(url);
            con = url1.openConnection();
            //Print the JSON response
            System.out.println(con.getInputStream().toString());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }






        int newPrice = 0;
        stock.setPrice(newPrice);
        LocalDateTime now = LocalDateTime.now();
        stock.setLastUpdate(now);

    }






}
