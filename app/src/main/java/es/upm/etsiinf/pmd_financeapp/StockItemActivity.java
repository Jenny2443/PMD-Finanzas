package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import es.upm.etsiinf.pmd_financeapp.db.DbStock;

public class StockItemActivity extends AppCompatActivity {

    TextView title;
    EditText fullName;
    TextView price;
    TextView maxPrice;
    TextView minPrice;
    TextView lastUpdate;

    Switch switchFavorite;
    ImageView botonReturn;
    public BottomNavigationView bottomNavigationView;
    public DbStock dbStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Stock stock;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_item);

        //Get the symbol of the stock from the intent
        String symbol = getIntent().getStringExtra("symbol");
        title = findViewById(R.id.stocks_item_title);
        title.setText(symbol);
        fullName = findViewById(R.id.full_stock_name);
        price = findViewById(R.id.stock_item_price);
        maxPrice = findViewById(R.id.stock_item_max_price);
        minPrice = findViewById(R.id.stock_item_min_price);
        lastUpdate = findViewById(R.id.stock_item_date);
        switchFavorite = findViewById(R.id.switch_stock_favorite);
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_stocks);
        botonReturn = findViewById(R.id.buttom_retroceder_stock);

        if (StockManager.checkExistance(symbol, this)){
            if (StockManager.getStock(symbol, this).getName() != null)
                fullName.setText(StockManager.getStock(symbol, this).getName());
            //Switch favorite to true
            switchFavorite.setChecked(true);
            stock = StockManager.getStock(symbol, this);
        }else{
            stock = new Stock(symbol, null, 0, null);
            long id = StockManager.addStock(stock, this);
            Log.i("StockItemActivity", "Stock insertado: symbol: " + stock.getSymbol() +  " con id: " + id);
        }

        List<Stock> stocks = StockManager.getStocks(this);
        for (Stock s : stocks) {
            Log.i("StockItemActivity", "Stock: " + s.getSymbol());
        }
        Thread thread = new Thread(new DownloadStockManager(this, symbol) );
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean error;
        if (StockManager.getStock(symbol, this) == null) {
            error = true;
        } else if (StockManager.getStock(symbol, this).getPrice() == 0) {
            error = true;
        } else {
            error = false;

        }
        if (error) {
            Toast.makeText(this, "No existe el símbolo", Toast.LENGTH_SHORT).show();
            openActivityStocks();
            return;
        }

        Stock stock2 = StockManager.getStock(symbol, this);
        price.setText(String.valueOf("Precio cierre: " + stock2.getPrice()));
        maxPrice.setText(String.valueOf("Precio máximo (24h): " + stock2.getMaxPrice()));
        minPrice.setText(String.valueOf("Precio mínimo (24h): " + stock2.getMinPrice()));
        LocalDateTime localDateTime = stock2.getLastUpdate();

        // Especificar el formato deseado (por ejemplo, "yyyy-MM-dd HH:mm:ss")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Convertir LocalDateTime a String
        String formattedDateTime = localDateTime.format(formatter);
        lastUpdate.setText(String.valueOf("Última actualización: " + formattedDateTime));
        Log.println(Log.INFO, "Stocks", "Stocks updated");
        Log.println(Log.INFO, "Stocks", "New price: " + stock.getPrice());


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menu_nav_action_home) {
                    Toast.makeText(StockItemActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(StockItemActivity.this, "Stocks", Toast.LENGTH_SHORT).show();
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    Toast.makeText(StockItemActivity.this, "Historial", Toast.LENGTH_SHORT).show();
                    openActivityHistorial();
                }
                return true;
            }
        });

        dbStock = new DbStock(this);
        botonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salir(stock);
                openActivityStocks();
            }
        });

    }

    protected void onPause() {
        super.onPause();
        // Get the stock
        Stock stock = StockManager.getStock(title.getText().toString(), this);
        salir(stock);


    }

    private void salir(Stock stock) {

        if (!switchFavorite.isChecked()) {
            // If it is not, set the stock as not favorite
            StockManager.removeStock(stock, this);
        }else {
            stock.setName(fullName.getText().toString());
            long id = dbStock.insertarStock(stock.getSymbol(), stock.getName(), stock.getPrice(), stock.getMaxPrice(), stock.getMinPrice(), stock.getLastUpdate());
            //dbStock.borrarStock(stock.getSymbol());
            //Log.i("StockItemActivity", "Stock insertado: nombre:" + stock.getName() +  "con id: " + id);
            Log.i("StockItemActivity", "Stock borrado: nombre:" + stock.getName());
        }

    }
    public void openActivityHistorial(){
        salir(StockManager.getStock(title.getText().toString(), this));
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de home
    public void openActivityHome(){
        salir(StockManager.getStock(title.getText().toString(), this));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openActivityStocks(){
        salir(StockManager.getStock(title.getText().toString(), this));
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }
}