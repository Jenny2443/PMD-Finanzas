package es.upm.etsiinf.pmd_financeapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDateTime;
import java.util.Date;

public class DbStock extends DBHelperStock{
    Context context;
    public DbStock(Context context) {
        super(context);
        this.context = context;
    }

    public long insertarStock(String ticker, String nombre, double precioCierre, double precioMax, double precioMin, LocalDateTime lastUpdate){
        long id = 0;
        try{
            DBHelperStock dbHelper = new DBHelperStock(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Damos valores
            ContentValues values = new ContentValues();
            values.put("ticker", ticker);
            values.put("nombre", nombre);
            values.put("precioCierre", precioCierre);
            values.put("precioMax", precioMax);
            values.put("precioMin", precioMin);
            values.put("lastUpdate", lastUpdate.toString());

            //Insertamos
            id = conn.insert("t_fav_stocks", null, values);

            //Cerrar conexion con bbdd
            conn.close();
        }catch (Exception e) {
            e.toString();
        }
        return id;
    }

    public void borrarStock(String ticker){
        try{
            DBHelperStock dbHelper = new DBHelperStock(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Borramos
            conn.delete("t_fav_stocks", "ticker = ?", new String[]{ticker});

            //Cerrar conexion con bbdd
            conn.close();
        }catch (Exception e) {
            e.toString();
        }
    }

    public void actualizarStock(String ticker, String nombre, float precioCierre, float precioMax, float precioMin, Date lastUpdate){
        try{
            DBHelperStock dbHelper = new DBHelperStock(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Damos valores
            ContentValues values = new ContentValues();
            values.put("ticker", ticker);
            values.put("nombre", nombre);
            values.put("precioCierre", precioCierre);
            values.put("precioMax", precioMax);
            values.put("precioMin", precioMin);
            values.put("lastUpdate", lastUpdate.toString());

            //Actualizamos
            conn.update("t_fav_stocks", values, "ticker = ?", new String[]{ticker});

            //Cerrar conexion con bbdd
            conn.close();
        }catch (Exception e) {
            e.toString();
        }
    }

    public void leerStock(String ticker, String groupBy, String having, String orderBy){
        try{
            DBHelperStock dbHelper = new DBHelperStock(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Leemos
            conn.query("t_fav_stocks", null, "ticker = ?", new String[]{ticker}, groupBy, having, orderBy);

            //Cerrar conexion con bbdd
            conn.close();
        }catch (Exception e) {
            e.toString();
        }
    }
}
