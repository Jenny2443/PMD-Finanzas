package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import es.upm.etsiinf.pmd_financeapp.db.DbTransacciones;

public class AnnadirGasto extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    // categorias que selecciona al crear un gasto
    public String[] opCategorias = {"Casa", "Comida", "Ropa", "Salud", "Transporte", "Entetenimiento"};

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

    private TextView txt_fechaSeleccionada;

    // Variables para que el usuario suba una imagen
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Button subirImg;
    private ImageView img;

    private FrameLayout guardado;
    private FrameLayout guardado2;

    private ImageView AnGa_ok;
    private ImageView AnGa_ok2;
    private ImageView AnGa_ctexto;
    private ImageView AnGa_ctexto2;
    private ImageView AnGa_cimagen;
    private Uri imgUri;

    private String datosCompartidos;

    private EditText AnGa_dinero;
    private EditText notas;

    private String catSeleccionada;
    private boolean galeria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annadir_gasto);

        subirImg = findViewById(R.id.AnGa_btn_cargar_im);
        img = findViewById(R.id.AnGa_imSubida);
        imgUri = null;
        galeria = false;

        // Iniciamos botones
        btnCancelar = findViewById(R.id.AnGa_btn_cancelar);
        btnGuardar = findViewById(R.id.AnGa_btn_guardar);

        guardado = findViewById(R.id.guardado);
        guardado2 = findViewById(R.id.guardado2);
        AnGa_ok = findViewById(R.id.AnGa_im_ok);
        AnGa_ok2 = findViewById(R.id.AnGa_im_ok2);
        AnGa_ctexto = findViewById(R.id.AnGa_im_ctexto);
        AnGa_ctexto2 = findViewById(R.id.AnGa_im_ctexto2);
        AnGa_cimagen = findViewById(R.id.AnGa_im_cimagen);
        AnGa_dinero = findViewById(R.id.AnGa_ent_cantidad);
        notas = findViewById(R.id.AnGa_ent_notas);


        // Inicialización de la lista de categorías
        Spinner spinnerCat = findViewById(R.id.AnGa_categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        txt_fechaSeleccionada = findViewById(R.id.AnGa_fecha_seleccionada);
        txt_fechaSeleccionada.setText(diaActual + "/" + (mesActual + 1) + "/" + anioActual);

        // Configuración de selección de categoría
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                catSeleccionada = opCategorias[position];

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
                datePicker.setBackgroundColor(getResources().getColor(R.color.white));
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
                datosCompartidos = "En la fecha " + txt_fechaSeleccionada.getText().toString() +
                                    " he tenido un gasto de " + AnGa_dinero.getText().toString() +
                                    "€ en la categoría de " + catSeleccionada +
                                    ". Notas: " + notas.getText().toString();

                DbTransacciones dbTransacciones = new DbTransacciones(AnnadirGasto.this);
                dbTransacciones.insertarTransaccion(
                        txt_fechaSeleccionada.getText().toString(),
                        Double.parseDouble(AnGa_dinero.getText().toString()),
                        catSeleccionada,img,
                        notas.getText().toString(),true
                );
                if(galeria){
                    mostrarGuardado();
                } else {
                    mostrarGuardado2();
                }
            }
        });

        AnGa_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Crear notificacion
                makeNotification();
                openActivityHome();
            }
        });

        AnGa_ok2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Crear notificacion
                makeNotification();
                openActivityHome();
            }
        });

        AnGa_ctexto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compartirDatos();
            }
        });

        AnGa_ctexto2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compartirDatos();
            }
        });

        AnGa_cimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartirImagen();
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
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    openActivityHistorial();
                }
                return true;
            }
        });

        subirImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSeleccion();
            }
        });


    }

    // MENÚ SELECCION FOTO O HACERLA
    private void mostrarSeleccion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar fuente de la imagen");
        builder.setItems(new CharSequence[]{"Cámara", "Galería"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        pickImageFromGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                galeria = false;
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                img.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                galeria = true;
                Uri selectedImageUri = data.getData();
                img.setImageURI(selectedImageUri);
                imgUri = selectedImageUri;
            }
        }
    }

    //Funcion para abrir la actividad de stocks
    public void openActivityStocks(){
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de home

    public void openActivityHome(){
        // volvemos al home
        finish();
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

    public void mostrarGuardado2() {
        // Cambiar la visibilidad del FrameLayout
        if (guardado2.getVisibility() == View.VISIBLE) {
            guardado2.setVisibility(View.GONE);
        } else {
            guardado2.setVisibility(View.VISIBLE);
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

    public void compartirImagen() {
        // Crear un Intent con la acción ACTION_SEND
        Intent intent = new Intent(Intent.ACTION_SEND);

        // Establecer el tipo de contenido
        intent.setType("image/png");

        // Establecer la imagen que se compartirá

        intent.putExtra(Intent.EXTRA_STREAM, imgUri);

        // Mostrar el selector de aplicaciones para compartir
        startActivity(Intent.createChooser(intent, "Compartir con"));
    }

    // Método para mostrar una notificación
    private void makeNotification(){
        String chanelID = "CHANNEL_ID";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("App de finanzas");
        builder.setContentText("Gasto añadido");
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(getApplicationContext(), HistorialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelID);
            if(notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0, builder.build());
    }
}