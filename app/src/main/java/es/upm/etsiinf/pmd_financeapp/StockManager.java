package es.upm.etsiinf.pmd_financeapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import es.upm.etsiinf.pmd_financeapp.db.DbStock;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;

public class StockManager {

    private static final String API_KEY = "uAoBHhszznSW8IO1YhZhMhfcUfGuBE4p";
    private static List<Stock> stocks = new ArrayList<>();

    public static String getApiKey() {
        return API_KEY;
    }

    public static long  addStock (Stock stock, Context context) {
        DbStock dbStock = new DbStock(context);

        String ticker = stock.getSymbol();
        String nombre = stock.getName();
        double precioCierre = stock.getPrice();
        double precioMax = stock.getMaxPrice();
        double precioMin = stock.getMinPrice();
        LocalDateTime lastUpdate = stock.getLastUpdate();
        return dbStock.insertarStock(ticker, nombre, precioCierre, precioMax, precioMin, lastUpdate);
    }



    public static void removeStock (Stock stock, Context context) {
        DbStock dbStock = new DbStock(context);
        dbStock.borrarStock(stock.getSymbol());
        stocks.remove(stock);
    }

    public static List<Stock> getStocks(Context context) {

        DbStock dbStock = new DbStock(context);
        List<Stock> stocks = dbStock.obtenerTodasLosStocks();
        return stocks;

        /*
        List<Stock> stocks = new ArrayList<>();
        Stock stock1 = new Stock("AAPL", "Apple Inc.", 150, 200, 100, null);
        Stock stock2 = new Stock("GOOGL", "Alphabet Inc.", 150, 200, 100, null);
        Stock stock3 = new Stock("AMZN", "Amazon.com, Inc.", 150, 200, 100, null);
        stocks.add(stock1);
        stocks.add(stock2);
        stocks.add(stock3);

        return stocks;
        */


    }

    public static Stock getStock(String symbol, Context context) {
        for (Stock stock: getStocks(context)) {
            if (stock.getSymbol().equals(symbol)) {
                return stock;
            }
        }
        return null;
    }


    public static void updateStocks(Context context) throws IOException{
        for (Stock stock: stocks) {
            Log.println(Log.INFO, "Stocks", "Updating stock " + stock.getSymbol());
            updateStock(stock, context);
        }
    }

    public static void saveStock(Stock stock , Context context) {
        /**
         * This method saves the stock in the database
         */
        DbStock dbStock = new DbStock(context);
        if (checkExistance(stock.getSymbol(), context)) {
            dbStock.actualizarStock(stock.getSymbol(), stock.getName(), stock.getPrice(), stock.getMaxPrice(), stock.getMinPrice(), stock.getLastUpdate());
        } else {
            dbStock.insertarStock(stock.getSymbol(), stock.getName(), stock.getPrice(), stock.getMaxPrice(), stock.getMinPrice(), stock.getLastUpdate());
        }
    }

    public static boolean updateStock(Stock stock, Context context) throws IOException {
        /**
         * This method updates the stock by reference
         * 1. Make the API connection
         * 2. Update the stock by reference
         */
        //Make the API connection and update the stock
        if (stock == null) {
            System.out.println("Stock is null");
            return false;
        }
        Log.println(Log.INFO, "Stocks", "Updating stock " + stock);


        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("Make sure you set your polygon API key in the POLYGON_API_KEY environment variable!");
            System.exit(1);
        }
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Restar un día a la fecha actual
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date desiredDate = calendar.getTime();
        String desiredDateString = formatDate(desiredDate);
        String url = "https://api.polygon.io/v1/open-close/" + stock.getSymbol() + "/" + desiredDateString +"?adjusted=true&apiKey=" + API_KEY;
        HttpURLConnection  con = null;
        try {
            String response = getURLText(url);
            if (response == null) {
                //Send a intent to the main activity to show a toast
            return false;

            }
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

            LocalDateTime localDateTime = desiredDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();            //Update lastUpdate
            stock.setLastUpdate(localDateTime);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        /*
        //BORRAR ESTO
        stock.setPrice(100);
        stock.setMaxPrice(200);
        stock.setMinPrice(50);
        stock.setLastUpdate(LocalDateTime.now());
        */

        Log.println(Log.INFO, "Stocks", "Stock updated: Stock: " + stock);
        return true;


    }

    public static String formatDate(Date date) {
        // Especificar el formato deseado
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Formatear la fecha a una cadena
        return sdf.format(date);
    }

    public static String getURLText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        //Check response code
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);

            in.close();

            return response.toString();
        }catch (Exception e) {
            return null;
        }



    }

    public static boolean checkExistance(String symbol, Context context){

        List<Stock> stocks = getStocks(context);

        for (Stock stock: stocks) {
            if (stock.getSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

}
