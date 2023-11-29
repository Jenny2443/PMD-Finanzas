package es.upm.etsiinf.pmd_financeapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;

public class StockManager {
    private static final String API_KEY = "uAoBHhszznSW8IO1YhZhMhfcUfGuBE4p";
    private static List<Stock> stocks = new ArrayList<>();

    public static String getApiKey() {
        return API_KEY;
    }

    public static void  addStock (Stock stock) {
        stocks.add(stock);
    }

    public static void removeStock (Stock stock) {
        stocks.remove(stock);
    }

    public static List<Stock> getStocks() {
        return stocks;
    }

    public static Stock getStock(String symbol) {
        for (Stock stock: stocks) {
            if (stock.getSymbol().equals(symbol)) {
                return stock;
            }
        }
        return null;
    }


    public static void updateStocks() throws IOException{
        for (Stock stock: stocks) {
            Log.println(Log.INFO, "Stocks", "Updating stock " + stock.getSymbol());
            updateStock(stock);
        }
    }

    public static void updateStock(Stock stock) throws IOException {
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
        HttpURLConnection  con = null;
        try {
            Log.println(Log.INFO, "Stocks", "Conexion a : " + url);
            String response = getURLText(url);
            Log.println(Log.INFO, "Stocks", "Respuesta: " + response);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        int newPrice = 0;
        stock.setPrice(newPrice);
        LocalDateTime now = LocalDateTime.now();
        stock.setLastUpdate(now);

    }

    public static String getURLText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }






}
