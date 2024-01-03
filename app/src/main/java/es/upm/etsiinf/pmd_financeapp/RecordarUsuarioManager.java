package es.upm.etsiinf.pmd_financeapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class RecordarUsuarioManager {
    public static final String FILE_NAME = "login";

    public static void salir(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        boolean recordarUsuario = sharedPreferences.getBoolean("recordar_usuario", false);
        Log.println(Log.INFO, "RecordarUsuarioManager", "Recordar usuario: " + recordarUsuario);
        if (!recordarUsuario) {
            Log.println(Log.INFO, "RecordarUsuarioManager", "Logout");
            FirebaseAuth.getInstance().signOut();
        }


    }
}
