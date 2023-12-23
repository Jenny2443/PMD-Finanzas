package es.upm.etsiinf.pmd_financeapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

import es.upm.etsiinf.pmd_financeapp.db.DbTransacciones;

public class HistorialActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    private TextView tituloHistorial;

    public DbTransacciones dbTransacciones;
    public Cursor cursor;
    ListView listView;

    ImageView filter, calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_history);

        tituloHistorial = findViewById(R.id.historial_title);
        filter = findViewById(R.id.historial_btnFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoCategorias();
            }
        });

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
          public void bindView(View view, Context context, Cursor c){
              super.bindView(view, context, c);

              ImageView img = view.findViewById(R.id.historial_btn_delete);
              @SuppressLint("Range") final int transaccionId = c.getInt(c.getColumnIndex("_id"));
              img.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Toast.makeText(HistorialActivity.this, "Borrar", Toast.LENGTH_SHORT).show();
                      //int colId = c.getColumnIndex("_id");
                      //int id = c.getInt(colId);
                      dbTransacciones.borrarTransaccion(transaccionId);
                      cursor = dbTransacciones.obtenerTodasLasTransacciones();
                      ((SimpleCursorAdapter)listView.getAdapter()).changeCursor(cursor);
                  }
              });

//              view.setOnClickListener(new View.OnClickListener() {
//                  @Override
//                  public void onClick(View v) {
//                      Toast.makeText(HistorialActivity.this, "Editar", Toast.LENGTH_SHORT).show();
//                      int colFecha = c.getColumnIndex("fecha");
//                        int colCantidad = c.getColumnIndex("cantidad");
//                        int colCategoria = c.getColumnIndex("categoria");
//                        //int identificadorTransaccion = c.getColumnIndex("_id");
//                        int colNotas = c.getColumnIndex("notas");
//                        int colImg = c.getColumnIndex("imagen");
//
//                        if(colFecha != -1 && colCantidad != -1 && colCategoria != -1 && colNotas != -1 && colImg != -1) {
//                            String fecha = c.getString(colFecha);
//                            double cantidad = c.getDouble(colCantidad);
//                            String categoria = c.getString(colCategoria);
//                            //identificadorTransaccion = c.getInt(identificadorTransaccion);
//                            String notas = c.getString(colNotas);
//                            Toast.makeText(HistorialActivity.this, "Fecha: " + fecha + "\nCantidad: " + cantidad + "\nCategoria: " + categoria, Toast.LENGTH_SHORT).show();
//
//                            //Verificar si es gasto o ingreso
//                            if (cantidad < 0) {
//                                Log.i("HistorialActivity", "Es gasto");
//                                Intent intent = new Intent(HistorialActivity.this, EditarGasto.class);
//                                intent.putExtra("fecha", fecha);
//                                intent.putExtra("cantidad", cantidad);
//                                intent.putExtra("categoria", categoria);
//                                intent.putExtra("id", transaccionId);
//                                intent.putExtra("notas", notas);
//                                startActivity(intent);
//                            } else {
//                                Log.i("HistorialActivity", "Es ingreso");
//                                Intent intent = new Intent(HistorialActivity.this, EditarIngreso.class);
//                                intent.putExtra("fecha", fecha);
//                                intent.putExtra("cantidad", cantidad);
//                                intent.putExtra("categoria", categoria);
//                                intent.putExtra("id", transaccionId);
//                                intent.putExtra("notas", notas);
//                                startActivity(intent);
//                            }
//                        }
//                  }
//              });
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

    private void mostrarDialogoCategorias() {
        // Obtener las categorías disponibles (puedes obtenerlas de la base de datos)
        String[] categorias = obtenerCategoriasDisponibles();

        String[] opciones = new String[categorias.length + 1];
        opciones[0] = "Todas";
        System.arraycopy(categorias, 0, opciones, 1, categorias.length);

        // Crear un diálogo con opciones de categorías
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la categoría");
//
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoriaSeleccionada = opciones[which];
                if("Todas".equals(categoriaSeleccionada)) {
                    cursor = dbTransacciones.obtenerTodasLasTransacciones();
                    ((SimpleCursorAdapter)listView.getAdapter()).changeCursor(cursor);
                } else {
                    filtrarPorCategoria(categoriaSeleccionada);
                }
            }
        });

        // Mostrar el diálogo
        builder.show();
    }


    private String[] obtenerCategoriasDisponibles() {
        // Obtener las categorías disponibles (puedes obtenerlas de la base de datos)
        return new String[]{"Casa", "Comida", "Ropa", "Salud", "Transporte", "Entetenimiento","Ahorros", "Depósitos", "Salario", "Ventas", "Activos", "Donaciones" };
    }

    private void filtrarPorCategoria(String categoria) {
        // Obtener un nuevo cursor con las transacciones de la categoría seleccionada
        Cursor nuevoCursor = dbTransacciones.obtenerTransaccionesPorCategoria(categoria);

        // Actualizar el cursor del Adapter
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
        adapter.changeCursor(nuevoCursor);
    }
}

