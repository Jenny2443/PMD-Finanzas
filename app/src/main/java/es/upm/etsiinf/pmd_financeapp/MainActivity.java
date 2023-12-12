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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
    ImageView imFilter;
    ImageView imCalendar;
    TextView txtBalance;

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
//    private static final String PREFS_NAME = "MyPrefsFile";
//    private static final String JOB_SCHEDULED_KEY = "jobScheduled";


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
         btnAnadirGasto = findViewById(R.id.main_btn_anadir_gasto);
         btnAnadirIngreso = findViewById(R.id.main_btn_anadir_ingreso);
         imFilter = findViewById(R.id.main_btnFilter);
         imCalendar = findViewById(R.id.main_btnCalendar);
         txtBalance = findViewById(R.id.main_txt_balance);

        //Inicializacion de bottom navigation view
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        tituloHome = findViewById(R.id.main_txt_home);

        //Pedir permiso de notificaciones si no lo tiene activado
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

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

        btnAnadirGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Añadir gasto", Toast.LENGTH_SHORT).show();
                // Intent para ir a la pantalla de añadir gasto
                Intent intentAnnadirGasto = new Intent(MainActivity.this, AnnadirGasto.class);
                startActivity(intentAnnadirGasto);
            }
        });

        btnAnadirIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Añadir ingreso", Toast.LENGTH_SHORT).show();
                Log.i("MainActivity", "click en añadir ingreso");
                Intent intentAnnadirIngreso = new Intent(MainActivity.this, AnnadirIngreso.class);
                startActivity(intentAnnadirIngreso);
            }
        });

        //Inicializacion de la grafica
        pieChart = findViewById(R.id.main_piechart);

        crearPieChart();


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

        Log.i("TestJobServiceStock","Mainactivity on create");
        //StockJobUtil.scheduleJob(this);

        // Verificar si el trabajo ya está programado
//        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean jobScheduled = preferences.getBoolean(JOB_SCHEDULED_KEY, false);

//        if (!jobScheduled) {
//            // Programar el trabajo utilizando JobScheduler
//            StockJobUtil.scheduleJob(this);
//
//            // Marcar que el trabajo ya ha sido programado
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean(JOB_SCHEDULED_KEY, true);
//            editor.apply();
//        }
    }

//    private void crearPieChart() {
//        ArrayList<PieEntry> categorias = new ArrayList<>();
//
//        categorias.add(new PieEntry(5, "Casa"));
//        categorias.add(new PieEntry(25, "Comida"));
//        categorias.add(new PieEntry(2, "Ropa"));
//        categorias.add(new PieEntry(8, "Salud"));
//        categorias.add(new PieEntry(40f, "Trasporte"));
//        categorias.add(new PieEntry(20f, "Entretenimiento"));
//
//        PieDataSet dataSet = new PieDataSet(categorias, "");
//        //dataSet.setColors(Color.rgb(120,178,255), Color.rgb(50,255,150), Color.rgb(255,51,51));
//        dataSet.setColors(getColor(R.color.azul), getColor(R.color.verde), getColor(R.color.morado), getColor(R.color.grey), getColor(R.color.rojo), getColor(R.color.amarillo));
//        dataSet.setSliceSpace(2f);
//
//        PieData data = new PieData(dataSet);
//        //data.setValueFormatter(new PercentFormatter(pieChart)); // Utilizar el PercentFormatter
//        dataSet.setDrawValues(false);   // No mostrar valores dentro de los segmentos
//        //data.setValueTextSize(20f);
//
//        pieChart.setData(data);
//
//        //dataSet.setDrawValues(true); // Mostrar valores dentro de los segmentos
//
//        // Configuraciones adicionales
//        pieChart.setHoleRadius(20f);
//        pieChart.setTransparentCircleRadius(25f);
//        //Desactiva descripcion
//        pieChart.getDescription().setEnabled(false);
//
//        //Si se activa -> saldria "Categoria 1"... en cada segmento
//        pieChart.setDrawEntryLabels(false);
//
//        pieChart.setDrawCenterText(true);
//        pieChart.setCenterText("Gastos");
//        pieChart.getLegend().setEnabled(true);
//    }

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

//        categorias.add(new PieEntry(5, "Casa"));
//        categorias.add(new PieEntry(25, "Comida"));
//        categorias.add(new PieEntry(2, "Ropa"));
//        categorias.add(new PieEntry(8, "Salud"));
//        categorias.add(new PieEntry(40f, "Trasporte"));
//        categorias.add(new PieEntry(20f, "Entretenimiento"));

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

//    private void makeNotification(){
//        String chanelID = "CHANNEL_ID";
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
//        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
//        builder.setContentTitle("Titulo");
//        builder.setContentText("Texto");
//        builder.setAutoCancel(true);
//        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        Intent intent = new Intent(getApplicationContext(), StocksActivity.class);
//        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
//        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT  | PendingIntent.FLAG_IMMUTABLE);
//        builder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelID);
//            if(notificationChannel == null){
//                int importance = NotificationManager.IMPORTANCE_HIGH;
//                notificationChannel = new NotificationChannel(chanelID, "NOTIFICATION_CHANNEL_NAME", importance);
//                notificationChannel.setLightColor(Color.GREEN);
//                notificationChannel.enableVibration(true);
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//        notificationManager.notify(0, builder.build());
//    }


}