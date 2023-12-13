package es.upm.etsiinf.pmd_financeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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
        ){
            @Override
            public void bindView(View view, Context context, Cursor cursor){
                super.bindView(view, context, cursor);

                ImageView imageView = view.findViewById(R.id.historial_btn_delete);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Toast.makeText(HistorialActivity.this, "Click en borrar", Toast.LENGTH_SHORT).show();
                       //Borrar transaccion de la bbdd
                        int colId = cursor.getColumnIndex("_id");
                        int identificadorTransaccion = cursor.getInt(colId);
                        dbTransacciones.borrarTransaccion(identificadorTransaccion);
                        //Actualizar lista
                        cargarTransacciones();
                        openActivityHome();
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(HistorialActivity.this, "Click en item", Toast.LENGTH_SHORT).show();
                        int colFecha = cursor.getColumnIndex("fecha");
                        int colCantidad = cursor.getColumnIndex("cantidad");
                        int colCategoria = cursor.getColumnIndex("categoria");
                        int identificadorTransaccion = cursor.getColumnIndex("_id");
                        int colNotas = cursor.getColumnIndex("notas");
                        int colImg = cursor.getColumnIndex("imagen");

                        if(colCategoria != -1 && colCantidad != -1 && colFecha != -1 && identificadorTransaccion != -1 && colNotas != -1 && colImg != -1) {
                            String fecha = cursor.getString(colFecha);
                            double cantidad = cursor.getDouble(colCantidad);
                            String categoria = cursor.getString(colCategoria);
                            identificadorTransaccion = cursor.getInt(identificadorTransaccion);
                            String notas = cursor.getString(colNotas);
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
        };
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
    }

    public void openActivityStocks(){
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }
    public void openActivityHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Vuelve a cargar las transacciones cada vez que la actividad se reanuda
        cargarTransacciones();
    }

    private void cargarTransacciones() {
        // Llama a la función para obtener todas las transacciones y actualiza la interfaz de usuario
        // (Esta función debe contener la lógica para actualizar los datos que se muestran en tu lista, adaptador, etc.)
        dbTransacciones.obtenerTodasLasTransacciones();
    }
}

