package es.upm.etsiinf.pmd_financeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import es.upm.etsiinf.pmd_financeapp.db.DbTransacciones;

public class HistorialActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    private TextView tituloHistorial;

    public DbTransacciones dbTransacciones;
    public Cursor cursor;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_history);

        tituloHistorial = findViewById(R.id.historial_title);

        listView = findViewById(R.id.historial_listview);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menu_nav_action_home) {
                    Toast.makeText(HistorialActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(HistorialActivity.this, "Stocks", Toast.LENGTH_SHORT).show();
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    Toast.makeText(HistorialActivity.this, "Historial", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        dbTransacciones = new DbTransacciones(this);
        cursor = dbTransacciones.obtenerTodasLasTransacciones();

        // Configuración del adaptador
        String[] fromColumns = {"fecha", "cantidad", "categoria"};
        int[] toViews = {R.id.textFecha, R.id.textCantidad, R.id.textCategoria};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_transaccion,
                cursor,
                fromColumns,
                toViews,
                0
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @SuppressLint("DefaultLocale")
            @Override
            public boolean setViewValue(android.view.View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndex("cantidad")) {
                    double cantidad = cursor.getDouble(columnIndex);
                    TextView textView = (TextView) view;
                    textView.setText(String.format("%.2f €", cantidad));
                    return true;
                }
                return false;
            }
        });

        // Establecer el adaptador en el ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                int colFecha = c.getColumnIndex("fecha");
                int colCantidad = c.getColumnIndex("cantidad");
                int colCategoria = c.getColumnIndex("categoria");
                int identificadorTransaccion = c.getColumnIndex("_id");
                int colNotas = c.getColumnIndex("notas");
                int colImg = c.getColumnIndex("imagen");

                if(colCategoria != -1 && colCantidad != -1 && colFecha != -1 && identificadorTransaccion != -1 && colNotas != -1 && colImg != -1) {
                    String fecha = c.getString(colFecha);
                    double cantidad = c.getDouble(colCantidad);
                    String categoria = c.getString(colCategoria);
                    identificadorTransaccion = c.getInt(identificadorTransaccion);
                    String notas = c.getString(colNotas);
                    Toast.makeText(HistorialActivity.this, "Fecha: " + fecha + "\nCantidad: " + cantidad + "\nCategoria: " + categoria, Toast.LENGTH_SHORT).show();

                        //Verificar si es gasto o ingreso
                        if (cantidad < 0) {
                            Intent intent = new Intent(HistorialActivity.this, EditarGasto.class);
                            intent.putExtra("fecha", fecha);
                            intent.putExtra("cantidad", cantidad);
                            intent.putExtra("categoria", categoria);
                            intent.putExtra("id", identificadorTransaccion);
                            intent.putExtra("notas", notas);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HistorialActivity.this, EditarIngreso.class);
                            intent.putExtra("fecha", fecha);
                            intent.putExtra("cantidad", cantidad);
                            intent.putExtra("categoria", categoria);
                            intent.putExtra("id", identificadorTransaccion);
                            intent.putExtra("notas", notas);
                            startActivity(intent);
                        }


                    }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_history);
        Toast.makeText(this, "onResume Historial", Toast.LENGTH_SHORT).show();

        //Actualizar lista de transacciones
        cursor = dbTransacciones.obtenerTodasLasTransacciones();
        ((SimpleCursorAdapter)listView.getAdapter()).changeCursor(cursor);

    }

    public void openActivityStocks(){
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }
    public void openActivityHome(){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

    private void cargarTransacciones() {
        // Llama a la función para obtener todas las transacciones y actualiza la interfaz de usuario
        // (Esta función debe contener la lógica para actualizar los datos que se muestran en tu lista, adaptador, etc.)
        dbTransacciones.obtenerTodasLasTransacciones();
    }
}

