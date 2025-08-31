package com.juanmolina.lordwine;

import com.google.gson.annotations.SerializedName;

public class Cliente {
    @SerializedName("clCodCliente")
    private int clCodCliente;

    @SerializedName("clIdCliente")
    private String clIdCliente;

    @SerializedName("clNombre")
    private String clNombre;

    @SerializedName("clDireccion")
    private String clDireccion;

    @SerializedName("clTelefono")
    private String clTelefono;

    @SerializedName("clCorreoElectronico")
    private String clCorreoElectronico;

    @SerializedName("clResetToken")
    private String clResetToken;

    @SerializedName("clResetTokenExpiration")
    private String clResetTokenExpiration;

    public int getClCodCliente() {
        return clCodCliente;
    }

    public String getClIdCliente() {
        return clIdCliente;
    }

    public String getClNombre() {
        return clNombre;
    }

    public String getClDireccion() {
        return clDireccion;
    }

    public String getClTelefono() {
        return clTelefono;
    }

    public String getClCorreoElectronico() {
        return clCorreoElectronico;
    }

    public String getClResetToken() {
        return clResetToken;
    }

    public String getClResetTokenExpiration() {
        return clResetTokenExpiration;
    }
}