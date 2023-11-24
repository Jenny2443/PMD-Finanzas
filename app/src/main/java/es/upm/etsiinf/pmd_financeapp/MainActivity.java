package es.upm.etsiinf.pmd_financeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity{
//    Button btnStocks;
//    Button btnHome;
//    Button btnHistorial;

    private NavigationBarView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializacion de botones
//        btnStocks = findViewById(R.id.main_btn_stocks);
//        btnHome = findViewById(R.id.main_btn_home);
//        btnHistorial = findViewById(R.id.main_btn_historial);

        //Inicializacion de bottom navigation view
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        // Perform item selected listener
        bottomNavigationView.

        bottomNavigationView.setOnClickListener(this);

        //Asignacion de listener
//        btnStocks.setOnClickListener(this);
//        btnHome.setOnClickListener(this);
//        btnHistorial.setOnClickListener(this);

    }


}