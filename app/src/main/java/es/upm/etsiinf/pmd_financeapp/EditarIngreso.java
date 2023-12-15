package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

import es.upm.etsiinf.pmd_financeapp.db.DbTransacciones;

public class EditarIngreso extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    public String[] opCategorias = {"Ahorros", "Depósitos", "Salario", "Ventas", "Activos", "Donaciones"};

    // fechas calendario
    Calendar calendario = Calendar.getInstance();
    int anioActual = calendario.get(Calendar.YEAR);
    // Los meses se cuentan desde 0, por eso se suma 1
    int mesActual = calendario.get(Calendar.MONTH);
    int diaActual = calendario.get(Calendar.DAY_OF_MONTH);

    // Declaración variables para mostrar calendario
    private DatePicker datePicker;
    private ImageView btn_DatePicker;


    // Botones de cancelar y guardar
    private Button btnCancelar;
    private Button btnGuardar;

    // Variables para que el usuario suba una imagen
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button subirImg;
    private ImageView img;

    private TextView txt_fechaSeleccionada;

    private DbTransacciones dbTransacciones;

    private EditText txtCantidad;
    private EditText txtDescripcion;
    private String categoriaSeleccionada;
    private FrameLayout guardado;
    private String datosCompartidos;

    private ImageView EdIn_ok;

    private ImageView EdIn_ctexto;

    public int identificadorTransaccion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ingreso);

        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_history);

        subirImg = findViewById(R.id.EdIn_btn_cargar_im);
        img = findViewById(R.id.EdIn_imSubida);

        EdIn_ok = findViewById(R.id.EdIn_im_ok);
        EdIn_ctexto = findViewById(R.id.EdIn_im_ctexto);

        // Iniciamos botones
        btnCancelar = findViewById(R.id.EdIn_btn_cancelar);
        btnGuardar = findViewById(R.id.EdIn_btn_guardar);

        guardado = findViewById(R.id.EdIn_guardado);

        // Inicialización de la lista de categorías
        Spinner spinnerCat = findViewById(R.id.EdIn_categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        //Datos a recoger (fecha, categorio, cantidad, descripcion)
        txt_fechaSeleccionada = findViewById(R.id.EdIn_fecha_seleccionada);
        txtCantidad = findViewById(R.id.EdIn_ent_cantidad);
        txtDescripcion = findViewById(R.id.EdIn_ent_notas);

        dbTransacciones = new DbTransacciones(this);

        // Configuración de selección de categoría
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                categoriaSeleccionada = opCategorias[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        // Configuración de botón de mostrar datePicker (el calendario desplegable para elegir una fecha)
        datePicker = findViewById(R.id.EdIn_calendario);
        btn_DatePicker = findViewById(R.id.EdIn_im_Calendario);

        // Desactivar calendatio al inicio
        datePicker.setEnabled(false);
        datePicker.setVisibility(View.INVISIBLE);

        //Si hace click en el calendario
        btn_DatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activar DatePicker al hacer clic en el botón
                datePicker.setEnabled(true);
                datePicker.setVisibility(View.VISIBLE);
                //Qtamos el boton de calendario
                btn_DatePicker.setVisibility(View.INVISIBLE);
                //Hacemos el background mas oscuro para que se vea mejor
                datePicker.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        // Configuración de selección de fecha
        DatePicker datePicker = findViewById(R.id.EdIn_calendario);
        // Configura el DatePicker para mostrar el calendario
        datePicker.init(
                anioActual,
                mesActual,
                diaActual,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String fechaSeleccionada = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    datePicker.setEnabled(false);
                    datePicker.setVisibility(View.INVISIBLE);
                    btn_DatePicker.setVisibility(View.VISIBLE);
                    txt_fechaSeleccionada.setText(fechaSeleccionada);

                }
        );


        // Configuración de botones de cancelar y guardar
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityHome();
            }
        });

        // Obtener datos extras del Intent
        Intent intent = getIntent();
        if (intent != null) {
            //Obtenemos la fecha que ya habia
            String fecha = intent.getStringExtra("fecha");
            //Obtenemos la categoria que ya habia
            categoriaSeleccionada = intent.getStringExtra("categoria");
            //Obtenemos la cantidad que ya habia
            Double cantidad = intent.getDoubleExtra("cantidad", 0.0);
            //Obtenemos la descripcion que ya habia
            String descripcion = intent.getStringExtra("notas");

            identificadorTransaccion = intent.getIntExtra("id", 0);
            // Configurar vistas con datos recibidos
            int categoriaPosition = adapter.getPosition(categoriaSeleccionada);
            spinnerCat.setSelection(categoriaPosition);

            txt_fechaSeleccionada.setText(fecha);
            txtCantidad.setText(String.valueOf(cantidad));
            txtDescripcion.setText(descripcion);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datosCompartidos = "En la fecha " + txt_fechaSeleccionada.getText().toString() +
                        " he tenido un ingreso de " + txtCantidad.getText().toString() +
                        "€ en la categoría de " + categoriaSeleccionada +
                        ". Notas: " + txtDescripcion.getText().toString();
                // TODO: GUARDAR GASTO
                DbTransacciones dbTransacciones = new DbTransacciones(EditarIngreso.this);

                Log.i("AnnadirIngreso", "Fecha: " + txt_fechaSeleccionada.getText().toString());
                Log.i("AnnadirIngreso", "Cantidad: " + txtCantidad.getText().toString());
                Log.i("AnnadirIngreso", "Categoría: " + categoriaSeleccionada);
                Log.i("AnnadirIngreso", "Notas: " + txtDescripcion.getText().toString());

                //long id = dbTransacciones.insertarTransaccion(txt_fechaSeleccionada.getText().toString(), Double.parseDouble(txtCantidad.getText().toString()), categoriaSeleccionada,img, txtDescripcion.getText().toString());

                //Actualizar en la bbdd
                dbTransacciones.actualizarTransaccion(identificadorTransaccion, txt_fechaSeleccionada.getText().toString(), Double.parseDouble(txtCantidad.getText().toString()), categoriaSeleccionada, img, txtDescripcion.getText().toString(),false);
                Log.i("EditarIngreso", "Transaccion modificada");
                mostrarGuardado();
            }
        });

                EdIn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: ACTUALIZAR GASTO en BBDD
                //Crear notificación
                //mostrarNotificacion("Gasto actualizado");
                openActivityHome();
            }
        });

        EdIn_ctexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartirDatos();
            }
        });

        // Funcion para cambiar de actividad al pulsar un boton del menu de navegacion inferior
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menu_nav_action_home) {
                    Toast.makeText(EditarIngreso.this, "Home", Toast.LENGTH_SHORT).show();
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(EditarIngreso.this, "Stocks", Toast.LENGTH_SHORT).show();
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    Toast.makeText(EditarIngreso.this, "Historial", Toast.LENGTH_SHORT).show();
                    openActivityHistorial();
                }
                return true;
            }
        });

        subirImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });




    }

    // Tratar imagen subida
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imagenUri = data.getData();

            img.setImageURI(imagenUri);
        }
    }

    //Funcion para abrir la actividad de stocks
    public void openActivityStocks(){
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de home

    public void openActivityHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de historial
    public void openActivityHistorial(){
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
    }

    public void mostrarGuardado() {
        // Cambiar la visibilidad del FrameLayout
        if (guardado.getVisibility() == View.VISIBLE) {
            guardado.setVisibility(View.GONE);
        } else {
            guardado.setVisibility(View.VISIBLE);
        }
    }

    public void compartirDatos() {
        // Crear un Intent con la acción ACTION_SEND
        Intent intent = new Intent(Intent.ACTION_SEND);

        // Establecer el tipo de contenido
        intent.setType("text/plain");

        // Establecer el texto que se compartirá
        intent.putExtra(Intent.EXTRA_TEXT, datosCompartidos);

        // Mostrar el selector de aplicaciones para compartir
        startActivity(Intent.createChooser(intent, "Compartir con"));
    }


}