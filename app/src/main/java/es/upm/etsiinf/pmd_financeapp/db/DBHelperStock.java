package es.upm.etsiinf.pmd_financeapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelperStock extends SQLiteOpenHelper {
    //Version de BBDD
    private static final int DATABASE_VERSION = 1;
    //Nombre de BBDD
    private static final String DATABASE_NOMBRE = "finance_app.db";
    //Tabla contactos
    private static final String TABLA_STOCKS = "t_fav_stocks";
    public DBHelperStock(Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear query de crear tabla
        db.execSQL("CREATE TABLE " + TABLA_STOCKS + " (" +
                "ticker TEXT PRIMARY KEY, " +
                "nombre TEXT, " +
                "precioCierre REAL, " +
                "precioMax REAL, " +
                "precioMin REAL, " +
                "lastUpdate DATE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //aqui meter lo que se quiera cambiar de la bbdd
    }
}
