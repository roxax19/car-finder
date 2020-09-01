package com.example.locaclizarcoche.db;

public class Constantes {

    //TABLA LOCALIZACIONES
    public static final String TABLA_UBICACIONES = "localizaciones";
    public static final String UBICACIONES_COLUMN_ID = "_id";
    public static final String UBICACIONES_COLUMN_NOMBRE = "nombre";
    public static final String UBICACIONES_COLUMN_LAT = "lat";
    public static final String UBICACIONES_COLUMN_LON = "lon";
    public static final String UBICACIONES_COLUMN_FECHA = "fecha";


    //CREATES
    public static final String CREAR_TABLA_UBICACIONES = "CREATE TABLE " +TABLA_UBICACIONES+" ("+
            UBICACIONES_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            UBICACIONES_COLUMN_NOMBRE+" VARCHAR(255), "+
            UBICACIONES_COLUMN_LAT+" VARCHAR(255), "+
            UBICACIONES_COLUMN_LON+" VARCHAR(255), "+
            UBICACIONES_COLUMN_FECHA+" DATETIME)";

}
