package es.upm.etsiinf.pmd_financeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;


public class PruebaGrafica extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_grafica);

        PieChart pieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(25f, "Categoría 1"));
        entries.add(new PieEntry(35f, "Categoría 2"));
        entries.add(new PieEntry(40f, "Categoría 3"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.rgb(120,178,255), Color.rgb(50,255,150), Color.rgb(255,51,51));


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart)); // Utilizar el PercentFormatter
        data.setValueTextSize(20f);
        pieChart.setData(data);

        dataSet.setDrawValues(true); // Mostrar valores dentro de los segmentos

        // Configuraciones adicionales
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(25f);
        //Desactiva descripcion
        pieChart.getDescription().setEnabled(false);

        //Si se activa -> saldria "Categoria 1"... en cada segmento
        //pieChart.setDrawEntryLabels(false);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Gastos");
        pieChart.getLegend().setEnabled(false);

        pieChart.invalidate();
    }
}