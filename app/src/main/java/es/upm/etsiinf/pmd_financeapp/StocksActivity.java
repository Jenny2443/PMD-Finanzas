package es.upm.etsiinf.pmd_financeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StocksActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);
        bottomNavigationView = findViewById(R.id.main_btn_nav);
        bottomNavigationView.setSelectedItemId(R.id.action_stocks);

    }
}