package com.juanmolina.lordwine;

import com.google.gson.annotations.SerializedName;
import com.juanmolina.lordwine.model.Category;

public class Producto {

    @SerializedName("prodIdProducto")
    private int prodIdProducto;

    @SerializedName("prodNombre")
    private String prodNombre;

    @SerializedName("prodDescripcion")
    private String prodDescripcion;

    @SerializedName("prodPrecio")
    private double prodPrecio;

    @SerializedName("categoria")
    private Category categoria;

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

    public Category getCategoria() {
        return categoria;
    }
}