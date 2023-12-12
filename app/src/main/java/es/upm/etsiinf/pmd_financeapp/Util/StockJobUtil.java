package es.upm.etsiinf.pmd_financeapp.Util;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import es.upm.etsiinf.pmd_financeapp.*;

public class StockJobUtil {
    private static int ID_SERVICIO = 1;
    //private static final long INTERVALO_24H = 24 * 60 * 60 * 1000;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void scheduleJob(Context context){
        //Creamos componente de servicio, TestJobService es la clase q vamos a hacer
        ComponentName serviceComponent = new ComponentName(context, TestJobServiceStock.class);
        //serviceComponent -> componente a ejecutar y un ID
        JobInfo.Builder jobBuilderInfo = new JobInfo.Builder(ID_SERVICIO, serviceComponent);

        jobBuilderInfo.setPersisted(true);

        //Se ejecuta cada 15 min aprox
        jobBuilderInfo.setPeriodic(15 * 60 * 1000);

        //decir a scheduler q es un servicio del sistema
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        //planificar
        jobScheduler.schedule(jobBuilderInfo.build());
    }
}


