package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{
//    Button btnStocks;
//    Button btnHome;
//    Button btnHistorial;
    Button btnAñadirGasto;
    Button btnAñadirIngreso;
    ImageView imFilter;
    ImageView imCalendar;


    //private NavigationBarView bottomNavigationView;
    public BottomNavigationView bottomNavigationView;
    private TextView textoPrueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call StockManager.updateStocks() for AAPL stock
        StockManager.addStock(new Stock("AAPL", "Apple Inc.", 0, null));
        //StockManager.addStock(new Stock("TSLA", "Tesla Inc.", 0, null));
        Thread thread = new Thread(new DownloadStocksManager());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Stock stock: StockManager.getStocks()) {
            Log.println(Log.INFO, "Stocks", stock.toString());
        }

        //Inicializacion de botones
         btnAñadirGasto = findViewById(R.id.añadir_gasto);
         btnAñadirIngreso = findViewById(R.id.añadir_ingreso);
         imFilter = findViewById(R.id.btnFilter);
         imCalendar = findViewById(R.id.btnCalendar);

        //Inicializacion de bottom navigation view
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        textoPrueba = findViewById(R.id.txt_home);


        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.action_home) {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.action_stocks) {
                    Toast.makeText(MainActivity.this, "Stocks", Toast.LENGTH_SHORT).show();
                    openActivityStocks();
                }else if(id == R.id.action_history) {
                    Toast.makeText(MainActivity.this, "Historial", Toast.LENGTH_SHORT).show();
                    openActivityHistorial();
                }
                return true;
            }
        });

        btnAñadirGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Añadir gasto", Toast.LENGTH_SHORT).show();
                // Intent para ir a la pantalla de añadir gasto
                Intent intentAnnadirGasto = new Intent(MainActivity.this, AnnadirGasto.class);
                startActivity(intentAnnadirGasto);
            }
        });

        btnAñadirIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Añadir ingreso", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void openActivityStocks(){
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }
    public void openActivityHistorial(){
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
    }


}