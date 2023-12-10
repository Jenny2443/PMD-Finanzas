package es.upm.etsiinf.pmd_financeapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

//import es.upm.etsiinf.pmd_financeapp.Util.StockJobUtil;

public class TestJobServiceStock extends JobService {

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("TestJobServiceStock", "Servicio ejecutado: " );
        actualizarAPI(params, getApplicationContext());
        Log.i("TestJobServiceStock", "Servicio reprogramado: " );
        return true;
    }

    //Funcion para llamar a la API y actualizar datos a traves de otro thread
    private void actualizarAPI(JobParameters jobParameters, Context context) {
        Log.i("TestJobService", "Actualizando API");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StockManager.updateStocks();
                    //makeNotification();
                    //Ponemos que se vuelva a ejecutar el servicio
                    jobFinished(jobParameters, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

//    private void makeNotification(){
//        Log.i("TestJobService", "Notificacion");
//        String chanelID = "CHANNEL_ID";
//        String groupId = "GROUP_ID"; // Identificador de grupo
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelID);
//        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
//        builder.setContentTitle("Titulo");
//        builder.setContentText("Texto");
//        builder.setAutoCancel(true);
//        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        builder.setGroup(groupId); // Asignar el grupo a la notificación
//
//        Intent intent = new Intent(getApplicationContext(), StocksActivity.class);
//        //TODO: VER Q FLAGS PONER (LOS ACTIVOS SON DE UN VIDEO DE YT) LOS COMENTADOS SON DE CLASE, POR AHORA FUNCIONA CON LOS DEL VIDEO
//        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
//        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT  | PendingIntent.FLAG_IMMUTABLE);
//        builder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelID);
//            if(notificationChannel == null){
//                int importance = NotificationManager.IMPORTANCE_HIGH;
//                notificationChannel = new NotificationChannel(chanelID, "NOTIFICATION_CHANNEL_NAME", importance);
//                notificationChannel.setLightColor(Color.GREEN);
//                notificationChannel.enableVibration(true);
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//        notificationManager.notify(0, builder.build());
//    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
