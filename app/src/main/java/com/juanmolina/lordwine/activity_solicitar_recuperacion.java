package com.juanmolina.lordwine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class activity_solicitar_recuperacion extends AppCompatActivity {

    // Declaración de las vistas
    private TextInputEditText correoInput;
    private Button enviarBtn;
    private ProgressBar loadingSpinner;
    private TextView volverLoginTv;

    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_recuperacion);

        correoInput = findViewById(R.id.correo_input);
        enviarBtn = findViewById(R.id.enviar_codigo_btn);
        loadingSpinner = findViewById(R.id.loading_spinner);
        volverLoginTv = findViewById(R.id.volver_login_tv);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://lord-wine-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiService.class);

        enviarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarRecuperacion();
            }
        });

        volverLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_solicitar_recuperacion.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void solicitarRecuperacion() {
        String correo = correoInput.getText().toString().trim();

        if (correo.isEmpty()) {
            Toast.makeText(this, "El correo electrónico es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingSpinner.setVisibility(View.VISIBLE);
        enviarBtn.setEnabled(false);

        SolicitudRecuperacion requestBody = new SolicitudRecuperacion(correo);
        Call<RespuestaRecuperacion> call = api.solicitarRecuperacion(requestBody);

        call.enqueue(new Callback<RespuestaRecuperacion>() {
            @Override
            public void onResponse(Call<RespuestaRecuperacion> call, Response<RespuestaRecuperacion> response) {
                loadingSpinner.setVisibility(View.GONE);
                enviarBtn.setEnabled(true);

                if (response.isSuccessful()) {
                    String mensaje = response.body().getMensaje() != null ? response.body().getMensaje() : "Si el correo existe, se ha enviado un código de verificación.";
                    Toast.makeText(activity_solicitar_recuperacion.this, mensaje, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(activity_solicitar_recuperacion.this, activity_restablecer_contrasena.class);
                    startActivity(intent);
                } else {
                    String errorMsg = "Error: " + response.code();
                    Toast.makeText(activity_solicitar_recuperacion.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaRecuperacion> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE);
                enviarBtn.setEnabled(true);
                Toast.makeText(activity_solicitar_recuperacion.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface ApiService {
        @POST("api/auth/solicitar-recuperacion")
        Call<RespuestaRecuperacion> solicitarRecuperacion(@Body SolicitudRecuperacion requestBody);
    }

    // Modelos de datos para la solicitud y respuesta
    public static class SolicitudRecuperacion {
        private String correo;

        public SolicitudRecuperacion(String correo) {
            this.correo = correo;
        }
    }

    public static class RespuestaRecuperacion {
        private String mensaje;

        public String getMensaje() {
            return mensaje;
        }
    }
}