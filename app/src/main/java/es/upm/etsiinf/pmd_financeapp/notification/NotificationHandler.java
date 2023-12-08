package es.upm.etsiinf.pmd_financeapp.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;

public class NotificationHandler extends ContextWrapper {
    private NotificationManager manager;

    //ids para canales
    public static final String CHANNEL_HIGH_ID = "1";
    public static final String CHANNEL_HIGH_NAME = "HIGH CHANNEL";
    public static final String CHANNEL_LOW_ID = "2";
    public static final String CHANNEL_LOW_NAME = "LOW CHANNEL";

    //Crear grupo
    public static final String GROUP_NAME = "GROUP";
    public static final int GROUP_ID = 111;
    public NotificationHandler(Context base) {
        super(base);
    }

    //Asegurar que solo 1 instancia
    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    //Funcion para crear canales
    public void createChannels(){
        NotificationChannel highChannel = new NotificationChannel(CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationChannel lowChannel = new NotificationChannel(CHANNEL_LOW_ID, CHANNEL_LOW_NAME, NotificationManager.IMPORTANCE_LOW);
        highChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        lowChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        //Creamos canales
        getManager().createNotificationChannel(highChannel);
        getManager().createNotificationChannel(lowChannel);
    }

    //Funcion para crear las notificaciones
//    public Notification.Builder createNotification(String title, String msg, String channel){
//
//    }
}
