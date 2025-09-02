package com.juanmolina.lordwine;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<RespuestaAutenticacion> iniciarSesion(@Body UsuarioLogin usuarioLogin);

    @POST("api/dialogflow/dialogflow-query")
    Call<DialogflowResponse> dialogflowQuery(@Body DialogflowRequest request);

    @GET("api/productos")
    Call<List<Producto>> getProductos();
}