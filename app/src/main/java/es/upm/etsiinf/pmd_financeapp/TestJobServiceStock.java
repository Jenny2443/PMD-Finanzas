package es.upm.etsiinf.pmd_financeapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import es.upm.etsiinf.pmd_financeapp.Util.StockJobUtil;

public class TestJobServiceStock extends JobService {
    private static final int JOB_SCHEDULE_INTERVAL = 15 * 60 * 1000; // 15 minutos

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("TestJobServiceStock", "Servicio ejecutado");
        //TODO: llamar a API
        // Llama al método de actualización de la API
        actualizarAPI(params);

        StockJobUtil.scheduleJob(this);
        return true;
    }

    private void actualizarAPI(JobParameters jobParameters) {
        Log.i("TestJobService", "Actualizando API");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StockManager.updateStocks();
                    jobFinished(jobParameters, false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
