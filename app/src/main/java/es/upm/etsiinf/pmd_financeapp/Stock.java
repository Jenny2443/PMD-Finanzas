package es.upm.etsiinf.pmd_financeapp;

import android.util.Log;

import java.time.LocalDateTime;

public class Stock {
    private String symbol;
    private String name;
    private double price;



    private double maxPrice;
    private double minPrice;
    private LocalDateTime lastUpdate;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) { this.price = price;}

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }



    public Stock(String symbol, String name, double price, LocalDateTime lastUpdate) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.lastUpdate = lastUpdate;
    }
    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", maxPrice=" + maxPrice +
                ", minPrice=" + minPrice +
                ", lastUpdate=" + lastUpdate +
                '}';
    }


}
