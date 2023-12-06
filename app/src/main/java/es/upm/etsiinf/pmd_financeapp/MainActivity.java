package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;

import es.upm.etsiinf.pmd_financeapp.db.DBHelperStock;
import es.upm.etsiinf.pmd_financeapp.db.DBHelperTransacciones;
import es.upm.etsiinf.pmd_financeapp.db.DbStock;

public class MainActivity extends AppCompatActivity{
//    Button btnStocks;
//    Button btnHome;
//    Button btnHistorial;
    Button btnAñadirGasto;
    Button btnAñadirIngreso;
    ImageView imFilter;
    ImageView imCalendar;
    TextView txtBalance;


    //private NavigationBarView bottomNavigationView;
    public BottomNavigationView bottomNavigationView;
    public TextView tituloHome;

    //Grafica
    public PieChart pieChart;

    //BBDD
    private DBHelperStock dbHelperStock;
    private DBHelperTransacciones dbHelperTransacciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        //Call StockManager.updateStocks() for AAPL stock
        StockManager.addStock(new Stock("AAPL", "Apple Inc.", 0, null));
        StockManager.addStock(new Stock("TSLA", "Tesla Inc.", 0, null));
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
        */


        //Inicializacion de botones
         btnAñadirGasto = findViewById(R.id.main_btn_anadir_gasto);
         btnAñadirIngreso = findViewById(R.id.main_btn_anadir_ingreso);
         imFilter = findViewById(R.id.main_btnFilter);
         imCalendar = findViewById(R.id.main_btnCalendar);
         txtBalance = findViewById(R.id.main_txt_balance);

        //Inicializacion de bottom navigation view
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        tituloHome = findViewById(R.id.main_txt_home);


        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menu_nav_action_home) {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(MainActivity.this, "Stocks", Toast.LENGTH_SHORT).show();
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
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
                // Intent para ir a la pantalla de añadir ingreso
                Intent intentAnnadirIngreso = new Intent(MainActivity.this, AnnadirIngreso.class);
                startActivity(intentAnnadirIngreso);
            }
        });

        //Inicializacion de la grafica
        pieChart = findViewById(R.id.main_piechart);

        ArrayList<PieEntry> categorias = new ArrayList<>();
        categorias.add(new PieEntry(5, "Casa"));
        categorias.add(new PieEntry(25, "Comida"));
        categorias.add(new PieEntry(2, "Ropa"));
        categorias.add(new PieEntry(8, "Salud"));
        categorias.add(new PieEntry(40f, "Trasporte"));
        categorias.add(new PieEntry(20f, "Entretenimiento"));

        PieDataSet dataSet = new PieDataSet(categorias, "");
        //dataSet.setColors(Color.rgb(120,178,255), Color.rgb(50,255,150), Color.rgb(255,51,51));
        dataSet.setColors(getColor(R.color.azul), getColor(R.color.verde), getColor(R.color.morado), getColor(R.color.grey), getColor(R.color.rojo), getColor(R.color.amarillo));
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        //data.setValueFormatter(new PercentFormatter(pieChart)); // Utilizar el PercentFormatter
        dataSet.setDrawValues(false);   // No mostrar valores dentro de los segmentos
        //data.setValueTextSize(20f);

        pieChart.setData(data);

        //dataSet.setDrawValues(true); // Mostrar valores dentro de los segmentos

        // Configuraciones adicionales
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(25f);
        //Desactiva descripcion
        pieChart.getDescription().setEnabled(false);

        //Si se activa -> saldria "Categoria 1"... en cada segmento
        pieChart.setDrawEntryLabels(false);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Gastos");
        pieChart.getLegend().setEnabled(true);


        //BBDD
        Log.i("MainActivity", "onCreate: " + getDatabasePath("FinanceApp.db"));
        dbHelperStock = new DBHelperStock(this);
        SQLiteDatabase dbStock = dbHelperStock.getWritableDatabase(); //Indica q vamos a scribir

        Log.i("MainActivity", "onCreate: " + getDatabasePath("FinanceApp.db"));
        dbHelperTransacciones = new DBHelperTransacciones(this);
        SQLiteDatabase dbTransacciones = dbHelperTransacciones.getWritableDatabase(); //Indica q vamos a scribir

        if(dbHelperStock != null){
            Log.d("DatabasePath", "DB: " + dbHelperStock.toString());
            Toast.makeText(MainActivity.this, "Stock Base de datos creada correctamente " + dbHelperStock.toString(), Toast.LENGTH_SHORT).show();
        }

        if(dbHelperTransacciones != null){
            Log.d("DatabasePath", "DB: " + dbHelperTransacciones.toString());
            Toast.makeText(MainActivity.this, "Transaccion Base de datos creada correctamente " + dbHelperTransacciones.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //Funcion para abrir la actividad de stocks
    public void openActivityStocks(){
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de historial

    public void openActivityHistorial(){
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
    }


}