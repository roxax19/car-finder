package com.example.locaclizarcoche.db;

public class Ubicacion {

    private Integer _id;
    private String nombre;
    private String lat;
    private String lon;
    private String fecha;

    public Ubicacion() {
        this._id = null;
        this.nombre = null;
        this.lat = null;
        this.lon = null;
        this.fecha = null;
    }

    public Ubicacion(Integer id, String nombre, String lat, String lon, String fecha) {
        this._id = id;
        this.nombre = nombre;
        this.lat = lat;
        this.lon = lon;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }
}
