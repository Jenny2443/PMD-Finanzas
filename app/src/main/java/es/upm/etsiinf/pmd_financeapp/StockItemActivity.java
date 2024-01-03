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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_item);

        //Get the symbol of the stock from the intent
        String symbol = getIntent().getStringExtra("symbol");
        title = findViewById(R.id.stocks_title);
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

        Stock stock;
        if (StockManager.checkExistance(symbol, this)){
            // Si existe, obtener el stock de la base de datos
            switchFavorite.setChecked(true);
            stock = StockManager.getStock(symbol, this);
        }else{
            // Si no, lo creamos
            stock = new Stock(symbol, null, 0, null);
        }

        Thread thread = new Thread(new DownloadStockManager(this, stock) );
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean error = stock == null || stock.getPrice() == 0;
        Log.println(Log.INFO, "Stocks", "Stock: " + stock);
        if (error) {
            Toast.makeText(this, "No existe el símbolo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StocksActivity.class);
            startActivity(intent);
            return;
        }

        fullName.setText(stock.getName());
        price.setText(String.valueOf("Precio cierre: " + stock.getPrice()));
        maxPrice.setText(String.valueOf("Precio máximo (24h): " + stock.getMaxPrice()));
        minPrice.setText(String.valueOf("Precio mínimo (24h): " + stock.getMinPrice()));
        LocalDateTime localDateTime = stock.getLastUpdate();
        if (localDateTime == null) localDateTime = LocalDateTime.now();
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
                    salir(stock);
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(StockItemActivity.this, "Stocks", Toast.LENGTH_SHORT).show();
                    salir(stock);
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    Toast.makeText(StockItemActivity.this, "Historial", Toast.LENGTH_SHORT).show();
                    salir(stock);
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
        //salir(stock);


    }

    private void salir(Stock stock) {
        // Si Stock está en favoritos
        Log.println(Log.INFO, "Stocks", "[SALIR] Stock: " + stock);
        if (StockManager.checkExistance(stock.getSymbol(), this)) {
            if (switchFavorite.isChecked()) {
                //Actualizar nombre
                stock.setName(fullName.getText().toString());
                //Guardar stock
                StockManager.saveStock(stock, this);
            } else {
                //Borrar stock
                StockManager.removeStock(stock, this);
            }
        }else{
            if (switchFavorite.isChecked()) {
                //Actualizar nombre
                stock.setName(fullName.getText().toString());
                //Guardar stock
                StockManager.saveStock(stock, this);
            }
        }

    }
    public void openActivityHistorial(){
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
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }

    protected void onDestroy() {
        super.onDestroy();
        RecordarUsuarioManager.salir(this);
    }
}