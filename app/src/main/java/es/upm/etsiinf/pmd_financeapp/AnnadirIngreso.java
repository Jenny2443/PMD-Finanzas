package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.upm.etsiinf.pmd_financeapp.db.DbTransacciones;

public class AnnadirIngreso extends AppCompatActivity {
    private static final int CAMERA_PERM_CODE = 101;

    private static final int CAMERA_REQUEST_CODE = 102;
    public BottomNavigationView bottomNavigationView;

    // categorias que selecciona al crear un ingreso
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
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private Button subirImg;
    private ImageView img;
    private Uri imgUri;

    private TextView txt_fechaSeleccionada;

    private DbTransacciones dbTransacciones;

    private EditText txtCantidad;
    private EditText txtDescripcion;
    private String categoriaSeleccionada;
    private FrameLayout guardado;
    private String datosCompartidos;

    private ImageView AnIn_ok;

    private ImageView AnIn_ctexto;
    private ImageView AnIn_cimagen;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annadir_ingreso);

//        // Verificar y solicitar permisos de la cámara antes de iniciar la captura de imágenes
//        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            //dispatchTakePictureIntent();
//        } else {
//            // Si no se han otorgado los permisos, solicitarlos
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
//        }
        Log.i("AnnadirIngreso", "Permisos para la cámara concedidos");


        subirImg = findViewById(R.id.AnIn_btn_cargar_im);
        img = findViewById(R.id.AnIn_imSubida);
        imgUri = null;

        dbTransacciones = new DbTransacciones(this);


        // Iniciamos botones
        btnCancelar = findViewById(R.id.AnIn_btn_cancelar);
        btnGuardar = findViewById(R.id.AnIn_btn_guardar);

        guardado = findViewById(R.id.guardado);
        AnIn_ok = findViewById(R.id.AnIn_im_ok);
        AnIn_ctexto = findViewById(R.id.AnIn_im_ctexto);
        AnIn_cimagen = findViewById(R.id.AnIn_im_cimagen);

        // Inicialización de la lista de categorías
        Spinner spinnerCat = findViewById(R.id.AnIn_categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        //Datos a recoger (fecha, categorio, cantidad, descripcion)
        txt_fechaSeleccionada = findViewById(R.id.AnIn_fecha_seleccionada);
        txt_fechaSeleccionada.setText(diaActual + "/" + (mesActual + 1) + "/" + anioActual);
        txtCantidad = findViewById(R.id.AnIn_ent_cantidad);
        txtDescripcion = findViewById(R.id.AnIn_ent_notas);


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
        datePicker = findViewById(R.id.AnIn_calendario);
        btn_DatePicker = findViewById(R.id.AnIn_im_Calendario);

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
        DatePicker datePicker = findViewById(R.id.AnIn_calendario);
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
                                    " he tenido un ingreso de " + txtCantidad.getText().toString() +
                                    "€ en la categoría de " + categoriaSeleccionada +
                                    ". Notas: " + txtDescripcion.getText().toString();
                // TODO: GUARDAR GASTO
                DbTransacciones dbTransacciones = new DbTransacciones(AnnadirIngreso.this);

                Log.i("AnnadirIngreso", "Fecha: " + txt_fechaSeleccionada.getText().toString());
                Log.i("AnnadirIngreso", "Cantidad: " + txtCantidad.getText().toString());
                Log.i("AnnadirIngreso", "Categoría: " + categoriaSeleccionada);
                Log.i("AnnadirIngreso", "Notas: " + txtDescripcion.getText().toString());

                long id = dbTransacciones.insertarTransaccion(txt_fechaSeleccionada.getText().toString(), Double.parseDouble(txtCantidad.getText().toString()), categoriaSeleccionada,img, txtDescripcion.getText().toString(),false);
                Log.i("AnnadirIngreso", "Transaccion insertado con id: " + id);
                mostrarGuardado();
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
                    Toast.makeText(AnnadirIngreso.this, "Home", Toast.LENGTH_SHORT).show();
                    openActivityHome();
                }else if(id == R.id.menu_nav_action_stocks) {
                    Toast.makeText(AnnadirIngreso.this, "Stocks", Toast.LENGTH_SHORT).show();
                    openActivityStocks();
                }else if(id == R.id.menu_nav_action_history) {
                    Toast.makeText(AnnadirIngreso.this, "Historial", Toast.LENGTH_SHORT).show();
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

        AnIn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openActivityHome();
                makeNotification();
            }
        });

        AnIn_ctexto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compartirDatos();
            }
        });

        AnIn_cimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartirImagen();
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
                        //dispatchTakePictureIntent();
                        askCameraPermission();
                        break;
                    case 1:
                        pickImageFromGallery();
                        break;
                }
            }
        });
        builder.show();
    }

//    @SuppressLint("QueryPermissionsNeeded")
//    private void dispatchTakePictureIntent() {
//        Log.i("AnnadirIngreso", "dispatchTakePictureIntent()");
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = crearArchivoImagen();
//
//            // Verifica si se pudo crear el archivo
//            if (photoFile != null) {
//                Log.i("AnnadirIngreso", "photoFile != null");
//                imgUri = FileProvider.getUriForFile(this, "es.upm.etsiinf.pmd_financeapp.fileprovider", photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
//                try {
//                    Log.i("AnnadirIngreso", "Antes de startActivityForResult");
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                    Log.i("AnnadirIngreso", "Después de startActivityForResult");
//                } catch (Exception e) {
//                    Log.e("AnnadirIngreso", "Error al iniciar la actividad de la cámara: " + e.getMessage());
//                }
//                //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    @SuppressLint("QueryPermissionsNeeded")
//    private void dispatchTakePictureIntent() {
//        Log.i("AnnadirIngreso", "dispatchTakePictureIntent()");
//        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = crearArchivoImagen();
//
//            // Verifica si se pudo crear el archivo
//            if (photoFile != null) {
//                Log.i("AnnadirIngreso", "photoFile != null");
//                imgUri = FileProvider.getUriForFile(this, "es.upm.etsiinf.pmd_financeapp.fileprovider", photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
//
//                try {
//                    Log.i("AnnadirIngreso", "Antes de startActivityForResult");
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                    Log.i("AnnadirIngreso", "Después de startActivityForResult");
//                } catch (Exception e) {
//                    Log.e("AnnadirIngreso", "Error al iniciar la actividad de la cámara: " + e.getMessage());
//                }
//            } else {
//                Log.e("AnnadirIngreso", "photoFile es null");
//            }
//        } else {
//            Log.e("AnnadirIngreso", "No se encontró actividad de cámara");
//        }
//    }

//    private void dispatchTakePictureIntent() {
//        Log.i("AnnadirIngreso", "dispatchTakePictureIntent()");
//        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        if (pickImageIntent.resolveActivity(getPackageManager()) != null) {
//            try {
//                startActivityForResult(pickImageIntent, REQUEST_IMAGE_CAPTURE);
//            } catch (Exception e) {
//                Log.e("AnnadirIngreso", "Error al iniciar la actividad de la galería: " + e.getMessage());
//            }
//        }
//    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                Toast.makeText(this, "Se necesita permiso para usar la camara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }



    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                //img.setImageBitmap(imageBitmap);
                img.setImageBitmap(image);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                img.setImageURI(selectedImageUri);
                imgUri = selectedImageUri;
            }
        }
//        }
//        if (requestCode == CAMERA_REQUEST_CODE) {
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            img.setImageBitmap(image);
//            imgUri = data.getData();
//        } else if (resultCode == REQUEST_IMAGE_PICK) {
//            Uri selectedImageUri = data.getData();
//            img.setImageURI(selectedImageUri);
//            imgUri = selectedImageUri;
//        }
    }

    //Funcion para abrir la actividad de stocks
    public void openActivityStocks(){
        Intent intent = new Intent(this, StocksActivity.class);
        startActivity(intent);
    }

    //Funcion para abrir la actividad de home

    public void openActivityHome(){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
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

    /*private File guardarBitmapEnArchivo(Bitmap bitmap) {
        File filesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(filesDir, "image_" + System.currentTimeMillis() + ".png");

        try {
            OutputStream os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/
    private File crearArchivoImagen() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombreArchivo = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageFile = File.createTempFile(nombreArchivo, ".jpg", storageDir);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Método para mostrar una notificación
    private void makeNotification(){
        String chanelID = "CHANNEL_ID";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("App de finanzas");
        builder.setContentText("Ingreso añadido");
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), HistorialActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT  | PendingIntent.FLAG_IMMUTABLE);
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