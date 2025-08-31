package com.juanmolina.lordwine;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<RespuestaAutenticacion> iniciarSesion(@Body UsuarioLogin usuarioLogin);
}