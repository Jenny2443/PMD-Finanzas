package es.upm.etsiinf.pmd_financeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnStocks;
    Button btnHome;
    Button btnHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializacion de botones
        btnStocks = findViewById(R.id.main_btn_stocks);
        btnHome = findViewById(R.id.main_btn_home);
        btnHistorial = findViewById(R.id.main_btn_historial);

        //Asignacion de listener
        btnStocks.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnHistorial.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.main_btn_stocks){
            //Lanzamos la notificacion del boton high
            Toast.makeText(this, "Boton stocks", Toast.LENGTH_SHORT).show();
        }else if(v.getId() == R.id.main_btn_home){
            //Lanzamos la notificacion del boton high
            Toast.makeText(this, "Boton home", Toast.LENGTH_SHORT).show();
        }else if(v.getId() == R.id.main_btn_historial){
            //Lanzamos la notificacion del boton high
            Toast.makeText(this, "Boton historial", Toast.LENGTH_SHORT).show();
        }
    }
}