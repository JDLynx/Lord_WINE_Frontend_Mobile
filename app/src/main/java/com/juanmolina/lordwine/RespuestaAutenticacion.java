package com.juanmolina.lordwine;

import com.google.gson.annotations.SerializedName;

public class RespuestaAutenticacion {
    @SerializedName("token")
    private String token;

    @SerializedName("rol")
    private String rol;

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    public String getToken() {
        return token;
    }

    public String getRol() {
        return rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "RespuestaAutenticacion{" +
                "token='" + token + '\'' +
                ", rol='" + rol + '\'' +
                ", id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}