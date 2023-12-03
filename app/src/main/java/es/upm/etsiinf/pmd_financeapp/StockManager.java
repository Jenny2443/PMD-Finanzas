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
            String response = getURLText(url);
            String[] parts = response.split(",");

            //Update price
            String price = parts[6];
            String newPrice =  price.split(":")[1];
            //Parse the price to double
            double priceDouble = Double.parseDouble(newPrice);
            stock.setPrice(priceDouble);

            //Update maxPrice
            String maxPrice = parts[4];
            String newMaxPrice = maxPrice.split(":")[1];
            double maxPriceDouble = Double.parseDouble(newMaxPrice);
            stock.setMaxPrice(maxPriceDouble);

            //Update minPrice
            String minPrice = parts[5];
            String newMinPrice = minPrice.split(":")[1];
            double minPriceDouble = Double.parseDouble(newMinPrice);
            stock.setMinPrice(minPriceDouble);

            //Update lastUpdate
            stock.setLastUpdate(LocalDateTime.now());


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static String getURLText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        //Check response code
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

    public static boolean checkExistance(String symbol){

        for (Stock stock: stocks) {
            if (stock.getSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

}
