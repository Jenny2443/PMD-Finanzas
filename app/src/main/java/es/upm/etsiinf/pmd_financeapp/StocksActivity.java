package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class StocksActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    public TextView tituloStocks;
    public SearchView barraBusqueda;

    public ListView stocksListView;
    public TextView emptyStocksList;

    public StocksAdapter stocksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);
        //Inicializamos el menu de navegacion inferior
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_stocks);

        //Inicializamos el texto de la actividad (titulo)
        tituloStocks = findViewById(R.id.stocks_title);
        barraBusqueda = findViewById(R.id.search_stocks);
        stocksListView = findViewById(R.id.stocks_list);
        emptyStocksList = findViewById(R.id.emptyListViewMessage);

        ArrayList<Stock> stocks = (ArrayList<Stock>) StockManager.getStocks(this);
        StocksAdapter stocksAdapter = new StocksAdapter(this, stocks);

        stocksListView.setAdapter(stocksAdapter);

        stocksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Acción a realizar cuando se pulsa un elemento de la lista
                Stock selectedStock = stocks.get(position);

                //Create a intent to open the StockItemActivity
                Intent intent = new Intent(StocksActivity.this, StockItemActivity.class);
                //Add the symbol of the stock to the intent
                intent.putExtra("symbol", selectedStock.getSymbol());
                //Start the activity
                startActivity(intent);
            }
        });

        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Este método se llama cuando se presiona Enter o se envía la búsqueda
                Log.d("MainActivity", "Query submitted: " + query);
                //Create a intent to open the StockItemActivity
                Intent intent = new Intent(StocksActivity.this, StockItemActivity.class);
                //Add the symbol of the stock to the intent
                intent.putExtra("symbol", query);
                //Start the activity
                startActivity(intent);
                //Delete text
                barraBusqueda.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Este método se llama cuando cambia el texto en la barra de búsqueda
                return false;
            }
        });


        //Funcion para cambiar de actividad al pulsar un boton del menu de navegacion inferior
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menu_nav_action_home) {
                    Toast.makeText(StocksActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(StocksActivity.this, "Stocks", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_nav_action_history) {
                    Toast.makeText(StocksActivity.this, "Historial", Toast.LENGTH_SHORT).show();
                    openActivityHistorial();
                }
                return true;
            }
        });

        if (stocks.isEmpty()){
            emptyStocksList.setVisibility(View.VISIBLE);
            stocksListView.setVisibility(View.GONE);
        }else{
            emptyStocksList.setVisibility(View.GONE);
            stocksListView.setVisibility(View.VISIBLE);
        }
    }

    //Funcion para abrir la actividad de historial
    public void openActivityHistorial(){
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de home
    public void openActivityHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

