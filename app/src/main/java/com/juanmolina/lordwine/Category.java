// Archivo: com.juanmolina.lordwine.model/Category.java
package com.juanmolina.lordwine.model;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("categIdCategoria")
    private int id;

    @SerializedName("catNombre")
    private String name;

    public Category() {}

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}