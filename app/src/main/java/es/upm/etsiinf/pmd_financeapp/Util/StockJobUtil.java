package es.upm.etsiinf.pmd_financeapp.Util;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;

import es.upm.etsiinf.pmd_financeapp.*;

public class StockJobUtil {
    private static int ID_SERVICIO = 1;
    //private static final long INTERVALO_24H = 24 * 60 * 60 * 1000;

    public static void scheduleJob(Context context){
        //Creamos componente de servicio, TestJobService es la clase q vamos a hacer
        ComponentName serviceComponent = new ComponentName(context, TestJobServiceStock.class);
        //serviceComponent -> componente a ejecutar y un ID
        JobInfo.Builder jobBuilderInfo = new JobInfo.Builder(ID_SERVICIO, serviceComponent);

        //jobBuilderInfo.setPeriodic(15 * 60 * 1000);
        //Propiedades (cuando, condiciones)
        //se mantiene la programación de la tarea tras el reinicio -> si usuario apaga y reinicia, q se mantenga
        //requiere permiso para avisar si se ha reiniciado -> se añade al manifet
        jobBuilderInfo.setPersisted(true);
        //jobBuilderInfo.setMinimumLatency(6000); //Cda 3 segs
        //jobBuilderInfo.setOverrideDeadline(6000); //cada 3 segs
        //Q la bateria no este baja
        jobBuilderInfo.setPeriodic(15 * 60 * 1000);
        jobBuilderInfo.setRequiresBatteryNotLow(true);

        //decir a scheduler q es un servicio del sistema
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        //planifique
        jobScheduler.schedule(jobBuilderInfo.build());
    }
}


