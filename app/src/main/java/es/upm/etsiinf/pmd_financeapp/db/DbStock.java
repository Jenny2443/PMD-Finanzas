package es.upm.etsiinf.pmd_financeapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.upm.etsiinf.pmd_financeapp.Stock;

public class DbStock extends DBHelperStock{
    Context context;
    public DbStock(Context context) {
        super(context);
        this.context = context;
    }

    public long insertarStock(String ticker, String nombre, double precioCierre, double precioMax, double precioMin, LocalDateTime lastUpdate){
        long id = -1;


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
            String str_null = null;
            if (lastUpdate == null) values.put("lastUpdate", str_null);
            else values.put("lastUpdate", lastUpdate.toString());

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

    public void actualizarStock(String ticker, String nombre, double precioCierre, double precioMax, double precioMin, LocalDateTime lastUpdate){
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

    public List<Stock> obtenerTodasLosStocks() {
        DBHelperStock dbHelper = new DBHelperStock(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i("Stocks", "obtenerTodosLosStocks");
        //return db.query("t_transacciones", null, null, null, null, null, null);
        //return db.rawQuery("SELECT * FROM t_transacciones", null);
        Cursor cursor = db.query("t_fav_stocks", new String[]{"ticker AS _id", "nombre", "precioCierre", "precioMax", "precioMin", "lastUpdate"}, null, null, null, null, null);
        List<Stock> stocks = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Recupera los datos del cursor y crea un objeto Stock
                if (cursor.getColumnIndex("_id") == -1 || cursor.getColumnIndex("nombre") == -1 || cursor.getColumnIndex("precioCierre") == -1 || cursor.getColumnIndex("precioMax") == -1 || cursor.getColumnIndex("precioMin") == -1 || cursor.getColumnIndex("lastUpdate") == -1) {
                    continue;
                }
                String ticker = cursor.getString(cursor.getColumnIndex("_id"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                double precioCierre = cursor.getDouble(cursor.getColumnIndex("precioCierre"));
                double precioMax = cursor.getDouble(cursor.getColumnIndex("precioMax"));
                double precioMin = cursor.getDouble(cursor.getColumnIndex("precioMin"));
                String lastUpdateStr = cursor.getString(cursor.getColumnIndex("lastUpdate"));
                LocalDateTime lastUpdate = LocalDateTime.parse(lastUpdateStr);
                Stock stock = new Stock(ticker, nombre, precioCierre, precioMax, precioMin, lastUpdate);
                stocks.add(stock);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return stocks;
    }

    public void deleteAllStocks() {
        DBHelperStock dbHelper = new DBHelperStock(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("t_fav_stocks", null, null);
    }
}
