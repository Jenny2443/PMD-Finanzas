package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializacion de botones
//        btnStocks = findViewById(R.id.main_btn_stocks);
//        btnHome = findViewById(R.id.main_btn_home);
//        btnHistorial = findViewById(R.id.main_btn_historial);
         btnAñadirGasto = findViewById(R.id.añadir_gasto);
         btnAñadirIngreso = findViewById(R.id.añadir_ingreso);
         imFilter = findViewById(R.id.btnFilter);
         imCalendar = findViewById(R.id.btnCalendar);

        //Inicializacion de bottom navigation view
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        textoPrueba = findViewById(R.id.txt_home);

        //Inicializacion de toolbar
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //bottomNavigationView.setSelectedItemId(R.id.action_home);
        //Asignacion de listener
//        btnStocks.setOnClickListener(this);
//        btnHome.setOnClickListener(this);
//        btnHistorial.setOnClickListener(this);
        //btnAñadirGasto.setOnClickListener(this);
        //btnAñadirIngreso.setOnClickListener(this);
        //imFilter.setOnClickListener(this);
        //imCalendar.setOnClickListener(this);


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