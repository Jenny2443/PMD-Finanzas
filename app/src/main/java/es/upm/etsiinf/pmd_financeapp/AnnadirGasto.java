package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

public class AnnadirGasto extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    // categorias que selecciona al crear un gasto
    public String[] opCategorias = {"Casa", "Comida", "Ropa", "Salud", "Transporte", "Entetenimiento"};

    // fechas calendario
    Calendar calendario = Calendar.getInstance();
    int anioActual = calendario.get(Calendar.YEAR);
    // Los meses se cuentan desde 0, por eso se suma 1
    int mesActual = calendario.get(Calendar.MONTH) + 1;;
    int diaActual = calendario.get(Calendar.DAY_OF_MONTH);;

    // Declaración variables para mostrar calendario
    private DatePicker datePicker;
    private ImageView btn_DatePicker;

    // Botones de cancelar y guardar
    private Button btnCancelar;
    private Button btnGuardar;

    private TextView txt_fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annadir_gasto);

        // Iniciamos botones
        btnCancelar = findViewById(R.id.AnGa_btn_cancelar);
        btnGuardar = findViewById(R.id.AnGa_btn_guardar);

        // Inicialización de la lista de categorías
        Spinner spinnerCat = findViewById(R.id.AnGa_categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        txt_fechaSeleccionada = findViewById(R.id.AnGa_fecha_seleccionada);

        // Configuración de selección de categoría
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String catSeleccionada = opCategorias[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        // Configuración de botón de mostrar datePicker (el calendario desplegable para elegir una fecha)
        datePicker = findViewById(R.id.AnGa_calendario);
        btn_DatePicker = findViewById(R.id.AnGa_im_Calendario);

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
                datePicker.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        });



        // Configuración de selección de fecha
        DatePicker datePicker = findViewById(R.id.AnGa_calendario);
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

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: GUARDAR GASTO en BBDD
                //Crear notificacion
                mostrarNotificacion("Gasto guardado");
                openActivityHome();
            }
        });

        // Inicialización de bottom navigation view
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_nav_action_home);

        // Funcion para cambiar de actividad al pulsar un boton del menu de navegacion inferior
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menu_nav_action_home) {
                    Toast.makeText(AnnadirGasto.this, "Home", Toast.LENGTH_SHORT).show();
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(AnnadirGasto.this, "Stocks", Toast.LENGTH_SHORT).show();
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    Toast.makeText(AnnadirGasto.this, "Historial", Toast.LENGTH_SHORT).show();
                    openActivityHistorial();
                }
                return true;
            }
        });


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

    // Método para mostrar una notificación
    private void mostrarNotificacion(String mensaje) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Verificar la versión de Android para crear un canal de notificación (requerido a partir de Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "mi_canal_id";
            CharSequence channelName = "Mi Canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(this, "mi_canal_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("App de Finanzas")
                .setContentText(mensaje)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }



}