package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.github.mikephil.charting.charts.PieChart;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import es.upm.etsiinf.pmd_financeapp.db.DBHelperStock;
import es.upm.etsiinf.pmd_financeapp.db.DBHelperTransacciones;
import es.upm.etsiinf.pmd_financeapp.db.DbTransacciones;

public class MainActivity extends AppCompatActivity {
    Button btnAnadirGasto;
    Button btnAnadirIngreso;
    TextView txtBalance;

    ImageView logOut;

    FirebaseUser user;
    FirebaseAuth mAuth;


    //private NavigationBarView bottomNavigationView;
    public BottomNavigationView bottomNavigationView;
    public TextView tituloHome;

    //Grafica
    public PieChart pieChart;

    //BBDD
    private DBHelperStock dbHelperStock;
    private DBHelperTransacciones dbHelperTransacciones;

    private DbTransacciones dbTransacciones;
    double ingresos = 0;
    double gasto = 0;
    double balance = 0;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //Obtenemos los ingresos y gastos de la bbdd
        dbTransacciones = new DbTransacciones(this);
        ingresos = dbTransacciones.obtenerSumaIngresos();

        gasto = dbTransacciones.obtenerSumaGastos();
        //Inicializacion de botones
         btnAnadirGasto = findViewById(R.id.main_btn_anadir_gasto);
         btnAnadirIngreso = findViewById(R.id.main_btn_anadir_ingreso);
         txtBalance = findViewById(R.id.main_txt_balance);

         //Calculamos el balance en funcion de ingresos y gastos y lo ponemos
        balance = ingresos + gasto;
        txtBalance.setText(String.format("Balance: %.2f €", balance));


        //Inicializacion de boton de logout y su funcion
        logOut = findViewById(R.id.main_btn_logout);
         logOut.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FirebaseAuth.getInstance().signOut();
                 Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                 startActivity(intent);
                 finish();
             }
         });

        //Pedir permiso de notificaciones si no lo tiene activado
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        //Inicializacion de bottom navigation view
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        tituloHome = findViewById(R.id.main_txt_home);

        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menu_nav_action_home) {
                }else if(id == R.id.menu_nav_action_stocks) {
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    openActivityHistorial();
                }
                return true;
            }
        });

        //Anade funcion al boton de anadir gasto
        btnAnadirGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para ir a la pantalla de añadir gasto
                Intent intentAnnadirGasto = new Intent(MainActivity.this, AnnadirGasto.class);
                startActivity(intentAnnadirGasto);
            }
        });

        //Anade funcion al boton de anadir ingreso
        btnAnadirIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAnnadirIngreso = new Intent(MainActivity.this, AnnadirIngreso.class);
                startActivity(intentAnnadirIngreso);
            }
        });

        //Inicializacion de la grafica
        pieChart = findViewById(R.id.main_piechart);
        //Creamos la grafica
        crearPieChart();

        //BBDD
        dbHelperStock = new DBHelperStock(this);
        SQLiteDatabase dbStock = dbHelperStock.getWritableDatabase(); //Indica q vamos a scribir

        dbHelperTransacciones = new DBHelperTransacciones(this);
        SQLiteDatabase dbTransacciones = dbHelperTransacciones.getWritableDatabase(); //Indica q vamos a scribir
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_home);
        // Actualizar el balance u otras partes de la interfaz de usuario aquí
        double nuevoBalance = calcularNuevoBalance();
        txtBalance.setText(String.format("Balance: %.2f €", nuevoBalance));

        // Actualizar la gráfica u otras partes de la interfaz de usuario aquí
        crearPieChart();
    }

    private double calcularNuevoBalance() {
        //Obtenemos los ingresos y gastos de la bbdd
        dbTransacciones = new DbTransacciones(this);
        ingresos = dbTransacciones.obtenerSumaIngresos();
        gasto = dbTransacciones.obtenerSumaGastos();
        return ingresos + gasto;
    }

    private void crearPieChart() {
        DbTransacciones dbTransacciones = new DbTransacciones(this);
        Cursor cursor = dbTransacciones.obtenerTodasLasTransacciones();
        ArrayList<PieEntry> categorias = new ArrayList<>();
        // Mapa para almacenar el recuento de gastos por categoría
        HashMap<String, Integer> countMap = new HashMap<>();
        double totalGastos = 0;
        if(cursor.moveToFirst()){
            int colCantidad = cursor.getColumnIndex("cantidad");
            int colCategoria = cursor.getColumnIndex("categoria");
            if(colCantidad != -1 && colCategoria != -1){
                do{
                    double cantidad = cursor.getDouble(colCantidad);
                    if(cantidad < 0){
                        String categoria = cursor.getString(colCategoria);
                        if(countMap.containsKey(categoria)){
                            countMap.put(categoria, countMap.get(categoria) + 1);
                        }else{
                            countMap.put(categoria, 1);
                        }
                    }
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        for(Map.Entry<String,Integer> entry: countMap.entrySet()){
            String categoria = entry.getKey();
            int count = entry.getValue();
            categorias.add(new PieEntry(count, categoria));
        }

        PieDataSet dataSet = new PieDataSet(categorias, "");
        dataSet.setColors(getColor(R.color.azul), getColor(R.color.verde), getColor(R.color.morado), getColor(R.color.grey), getColor(R.color.rojo), getColor(R.color.amarillo));
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        dataSet.setDrawValues(false);   // No mostrar valores dentro de los segmentos

        pieChart.setData(data);

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
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        // Desplazar la leyenda hacia la izquierda a medida que se añaden más categorías
        if (countMap.size() > 1) {
            legend.setXEntrySpace(20f * (countMap.size() - 1));
        } else {
            legend.setXEntrySpace(20f);
        }
        pieChart.invalidate();
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