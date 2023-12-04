package es.upm.etsiinf.pmd_financeapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelperTransacciones extends SQLiteOpenHelper {
    //Version de BBDD
    private static final int DATABASE_VERSION = 1;
    //Nombre de BBDD
    private static final String DATABASE_NOMBRE = "finance_app.db";
    //Tabla contactos
    private static final String TABLA_TRANSACCIONES = "t_transacciones";
    public DBHelperTransacciones(Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear query de crear tabla
        db.execSQL("CREATE TABLE " + TABLA_TRANSACCIONES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha DATE NOT NULL, " +
                "cantidad REAL NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "imagen IMAGE, " +
                "notas TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
