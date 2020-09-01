package com.example.locaclizarcoche.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class ConnSQLiteHelper extends SQLiteOpenHelper {

    Context context;
    SQLiteDatabase db;

    public ConnSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constantes.CREAR_TABLA_UBICACIONES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Constantes.TABLA_UBICACIONES);
        onCreate(db);
    }

    public Cursor queryUbicacion(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        cursor = db.query(
                Constantes.TABLA_UBICACIONES,       //Tabla para realizar la consulta
                null,                       //null es todas las columnas
                null,                       //WHERE statement
                null,                   //WHERE args
                null,                       //GROUP BY
                null,                        //HAVING
                null,                       //ORDER BY
                null                          //LIMIT
        );

        return cursor;
    }

    public Cursor queryUbicacionPorNombre(String nombre){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        //Definimos query
        // Define 'where' part of query.
        String selection = Constantes.UBICACIONES_COLUMN_NOMBRE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { nombre };

        cursor = db.query(
                Constantes.TABLA_UBICACIONES,       //Tabla para realizar la consulta
                null,                       //null es todas las columnas
                selection,                       //WHERE statement
                selectionArgs,                   //WHERE args
                null,                       //GROUP BY
                null,                        //HAVING
                null,                       //ORDER BY
                null                          //LIMIT
        );

        return cursor;
    }

    public Long addUbicacion(Ubicacion ubicacion){
        SQLiteDatabase db = this.getWritableDatabase();
        Long idResultante;

        ContentValues values = new ContentValues();
        values.put(Constantes.UBICACIONES_COLUMN_NOMBRE, ubicacion.getNombre());
        values.put(Constantes.UBICACIONES_COLUMN_LAT, ubicacion.getLat());
        values.put(Constantes.UBICACIONES_COLUMN_LON, ubicacion.getLon());
        values.put(Constantes.UBICACIONES_COLUMN_FECHA, ubicacion.getFecha());


        try {

            idResultante = db.insertOrThrow(Constantes.TABLA_UBICACIONES, Constantes.UBICACIONES_COLUMN_ID, values);

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            idResultante= Long.valueOf(-1);
        }

        return idResultante;

    }

    public Long addOrUpdateUbicacion(Ubicacion ubicacion){
        SQLiteDatabase db = this.getWritableDatabase();
        Long idResultante;
        Cursor cursor;

        //Compruebo si ya hay una ubicación con ese nombre
        cursor = this.queryUbicacionPorNombre(ubicacion.getNombre());

        //Si hay ubicacion con ese nombre las borro
        if(cursor.getCount() != 0){
            int deleted = this.deleteUbicacionPorNombre(ubicacion.getNombre());
            Log.d("DELETED:", Integer.toString(deleted));
        }

        //Añado la nueva ubicación
        return this.addUbicacion(ubicacion);

    }

    public int deleteUbicacion(Ubicacion ubicacion){
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows;
        Integer id = ubicacion.get_id();

        try {
            // Define 'where' part of query.
            String selection = Constantes.UBICACIONES_COLUMN_ID + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { id.toString() };
            // Issue SQL statement.
            deletedRows = db.delete(Constantes.TABLA_UBICACIONES, selection, selectionArgs);

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            deletedRows= 0;
        }

        return deletedRows;
    }

    public int deleteUbicacionPorNombre(String nombre){
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows;


        try {
            // Define 'where' part of query.
            String selection = Constantes.UBICACIONES_COLUMN_NOMBRE + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { nombre };
            // Issue SQL statement.
            deletedRows = db.delete(Constantes.TABLA_UBICACIONES, selection, selectionArgs);

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            deletedRows= 0;
        }

        return deletedRows;
    }

    public int modifyUbicacion(Ubicacion original, Ubicacion modificada){
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows;
        Integer id = original.get_id();

        try {
            // Specify the new arguments
            ContentValues values = new ContentValues();
            values.put(Constantes.UBICACIONES_COLUMN_NOMBRE, modificada.getNombre());
            values.put(Constantes.UBICACIONES_COLUMN_LAT, modificada.getLat());
            values.put(Constantes.UBICACIONES_COLUMN_LON, modificada.getLon());
            values.put(Constantes.UBICACIONES_COLUMN_FECHA, modificada.getFecha());

            // Define 'where' part of query.
            String selection = Constantes.UBICACIONES_COLUMN_ID + " LIKE ?";

            // Specify arguments in placeholder order.
            String[] selectionArgs = { id.toString() };

            // Issue SQL statement.
            affectedRows = db.update(Constantes.TABLA_UBICACIONES,values, selection, selectionArgs);

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            affectedRows= 0;
        }
        return affectedRows;

    }

    public void deleteBD(){
        context.deleteDatabase("db_test");
    }


}
