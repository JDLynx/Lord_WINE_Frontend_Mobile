package com.juanmolina.lordwine;

import com.google.gson.annotations.SerializedName;

public class Producto {

    @SerializedName("prodIdProducto")
    private int prodIdProducto;

    @SerializedName("prodNombre")
    private String prodNombre;

    @SerializedName("prodDescripcion")
    private String prodDescripcion;

    @SerializedName("prodPrecio")
    private double prodPrecio;

    @SerializedName("prodUrlImagen")
    private String prodUrlImagen;

    @SerializedName("categIdCategoria")
    private int categIdCategoria;

    // Getters
    public int getProdIdProducto() {
        return prodIdProducto;
    }

    public String getProdNombre() {
        return prodNombre;
    }

    public String getProdDescripcion() {
        return prodDescripcion;
    }

    public double getProdPrecio() {
        return prodPrecio;
    }

    public String getProdUrlImagen() {
        return prodUrlImagen;
    }

    public int getCategIdCategoria() {
        return categIdCategoria;
    }
}