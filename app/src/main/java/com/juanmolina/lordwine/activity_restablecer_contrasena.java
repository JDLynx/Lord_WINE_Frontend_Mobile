package com.juanmolina.lordwine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class activity_restablecer_contrasena extends AppCompatActivity {

    // Declaración de las vistas
    private TextInputEditText correoInput;
    private TextInputEditText tokenInput;
    private TextInputEditText nuevaContrasenaInput;
    private Button restablecerBtn;
    private ProgressBar loadingSpinner;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_contrasena);

        correoInput = findViewById(R.id.correo_input);
        tokenInput = findViewById(R.id.token_input);
        nuevaContrasenaInput = findViewById(R.id.nueva_contrasena_input);
        restablecerBtn = findViewById(R.id.restablecer_btn);
        loadingSpinner = findViewById(R.id.loading_spinner);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://lord-wine-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiService.class);

        // Listener para el botón de restablecer
        restablecerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restablecerContrasena();
            }
        });
    }

    private void restablecerContrasena() {
        String correo = correoInput.getText().toString().trim();
        String token = tokenInput.getText().toString().trim();
        String nuevaContrasena = nuevaContrasenaInput.getText().toString().trim();

        if (correo.isEmpty() || token.isEmpty() || nuevaContrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingSpinner.setVisibility(View.VISIBLE);
        restablecerBtn.setEnabled(false);

        SolicitudRestablecer requestBody = new SolicitudRestablecer(correo, token, nuevaContrasena);
        Call<RespuestaRestablecer> call = api.restablecerContrasena(requestBody);

        call.enqueue(new Callback<RespuestaRestablecer>() {
            @Override
            public void onResponse(Call<RespuestaRestablecer> call, Response<RespuestaRestablecer> response) {
                loadingSpinner.setVisibility(View.GONE);
                restablecerBtn.setEnabled(true);

                if (response.isSuccessful()) {
                    String mensaje = response.body().getMensaje() != null ? response.body().getMensaje() : "Contraseña actualizada exitosamente.";
                    Toast.makeText(activity_restablecer_contrasena.this, mensaje, Toast.LENGTH_LONG).show();

                    // Navegar de vuelta a la actividad de Login después de un retraso
                    new Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(activity_restablecer_contrasena.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },
                            3000
                    );
                } else {
                    String errorMsg = "Error: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(activity_restablecer_contrasena.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaRestablecer> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE);
                restablecerBtn.setEnabled(true);
                Toast.makeText(activity_restablecer_contrasena.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface ApiService {
        @POST("api/auth/restablecer-contrasena")
        Call<RespuestaRestablecer> restablecerContrasena(@Body SolicitudRestablecer requestBody);
    }

    public static class SolicitudRestablecer {
        private String correo;
        private String token;
        private String nuevaContrasena;

        public SolicitudRestablecer(String correo, String token, String nuevaContrasena) {
            this.correo = correo;
            this.token = token;
            this.nuevaContrasena = nuevaContrasena;
        }
    }

    public static class RespuestaRestablecer {
        private String mensaje;

        public String getMensaje() {
            return mensaje;
        }
    }
}