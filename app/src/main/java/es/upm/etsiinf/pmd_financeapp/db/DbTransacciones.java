package es.upm.etsiinf.pmd_financeapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.ImageView;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DbTransacciones extends DBHelperTransacciones{
    Context context;
    public DbTransacciones(Context context) {
        super(context);
        this.context = context;
    }

    //Las imagenes y las notas no las pongo como argumentos porq no son obligatorios
    public long insertarTransaccion(String fecha, double cantidad, String categoria, ImageView imagen, String notas, boolean esGasto){
        long id = 0;
        try{
            DBHelperTransacciones dbHelper = new DBHelperTransacciones(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Damos valores
            ContentValues values = new ContentValues();
            double cantidadConSigno = esGasto ? -cantidad : cantidad;

            values.put("fecha", fecha);
            values.put("cantidad", cantidadConSigno);
            values.put("categoria", categoria);

            if(imagen != null) {
                values.put("imagen", imagen.toString());
            }

            if(notas != null) {
                values.put("notas", notas);
            }

            //Insertamos
            id = conn.insert("t_transacciones", null, values);

            //Cerrar conexion con bbdd
            conn.close();
        }catch (Exception e) {
            Log.e("DbTransacciones", "Error al insertar transacción: " + e.toString());
        }
        return id;
    }

    public void borrarTransaccion(int id){
        try{
            DBHelperTransacciones dbHelper = new DBHelperTransacciones(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Borramos
            conn.delete("t_transacciones", "id = ?", new String[]{String.valueOf(id)});

            //Cerrar conexion con bbdd
            conn.close();
        }catch (Exception e) {
            e.toString();
        }
    }

    public void actualizarTransaccion(int id, String fecha, double cantidad, String categoria, ImageView imagen, String notas){
        try{
            DBHelperTransacciones dbHelper = new DBHelperTransacciones(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Damos valores
            ContentValues values = new ContentValues();
            values.put("fecha", fecha.toString());
            values.put("cantidad", cantidad);
            values.put("categoria", categoria);
            if(imagen != null) {
                values.put("imagen", imagen.toString());
            }
            if(notas != null) {
                values.put("notas", notas);
            }

            //Actualizamos
            conn.update("t_transacciones", values, "id = ?", new String[]{String.valueOf(id)});
            conn.close();

        }catch(Exception e){
            e.toString();
        }
    }

    public void leerTransaccion(int id, String groupBy, String having, String orderBy){
        try{
            DBHelperTransacciones dbHelper = new DBHelperTransacciones(context);
            //Creamos conexion
            SQLiteDatabase conn = dbHelper.getWritableDatabase();

            //Leemos
            conn.query("t_transacciones", null, "id = ?", new String[]{String.valueOf(id)}, groupBy, having, orderBy);
            conn.close();

        }catch(Exception e){
            e.toString();
        }
    }

    public Cursor obtenerTodasLasTransacciones() {
        DBHelperTransacciones dbHelper = new DBHelperTransacciones(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i("Historial", "obtenerTodasLasTransacciones");
        //return db.query("t_transacciones", null, null, null, null, null, null);
        //return db.rawQuery("SELECT * FROM t_transacciones", null);
        return db.query("t_transacciones", new String[]{"id AS _id", "fecha", "cantidad", "categoria", "notas", "imagen"}, null, null, null, null, null);
    }

}
