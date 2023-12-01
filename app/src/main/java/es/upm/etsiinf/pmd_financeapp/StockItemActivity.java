package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class StockItemActivity extends AppCompatActivity {

    TextView title;
    EditText fullName;
    TextView price;

    Switch switchFavorite;
    ImageView botonReturn;
    public BottomNavigationView bottomNavigationView;

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
        switchFavorite = findViewById(R.id.switch_stock_favorite);
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_stocks);
        botonReturn = findViewById(R.id.buttom_retroceder_stock);

        if (StockManager.checkExistance(symbol)){
            if (StockManager.getStock(symbol).getName() != null)
                fullName.setText(StockManager.getStock(symbol).getName());
            //Switch favorite to true
            switchFavorite.setChecked(true);
            stock = StockManager.getStock(symbol);
        }else{
            stock = new Stock(symbol, null, 0, null);
            StockManager.addStock(stock);
        }


        Thread thread = new Thread(new DownloadStockManager(this, symbol) );
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean error = getIntent().getBooleanExtra("ERROR", true);
        if (error) {
            Toast.makeText(this, "No existe el símbolo", Toast.LENGTH_SHORT).show();
            openActivityStocks();
            return;
        }
        price.setText(String.valueOf(StockManager.getStock(symbol).getPrice()));
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
        Stock stock = StockManager.getStock(title.getText().toString());
        salir(stock);


    }

    private void salir(Stock stock) {

        if (!switchFavorite.isChecked()) {
            // If it is not, set the stock as not favorite
            StockManager.removeStock(stock);
        }else {
            stock.setName(fullName.getText().toString());
        }

    }
    public void openActivityHistorial(){
        salir(StockManager.getStock(title.getText().toString()));
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de home
    public void openActivityHome(){
        salir(StockManager.getStock(title.getText().toString()));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openActivityStocks(){
        salir(StockManager.getStock(title.getText().toString()));
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }
}