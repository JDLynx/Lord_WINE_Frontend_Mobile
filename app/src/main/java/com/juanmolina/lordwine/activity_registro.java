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

public class activity_registro extends AppCompatActivity {

    private TextInputEditText inputId;
    private TextInputEditText inputNombre;
    private TextInputEditText inputDireccion;
    private TextInputEditText inputTelefono;
    private TextInputEditText inputCorreo;
    private TextInputEditText inputContrasena;
    private TextInputEditText inputConfirmarContrasena;
    private Button btnRegistrar;
    private ProgressBar loadingSpinner;

    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inputId = findViewById(R.id.input_id);
        inputNombre = findViewById(R.id.input_nombre);
        inputDireccion = findViewById(R.id.input_direccion);
        inputTelefono = findViewById(R.id.input_telefono);
        inputCorreo = findViewById(R.id.input_correo);
        inputContrasena = findViewById(R.id.input_contrasena);
        inputConfirmarContrasena = findViewById(R.id.input_confirmar_contrasena);
        btnRegistrar = findViewById(R.id.btn_registrar);
        loadingSpinner = findViewById(R.id.loading_spinner);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://lord-wine-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiService.class);

        // Listener para el botón de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarCliente();
            }
        });
    }

    private void registrarCliente() {
        String id = inputId.getText().toString().trim();
        String nombre = inputNombre.getText().toString().trim();
        String direccion = inputDireccion.getText().toString().trim();
        String telefono = inputTelefono.getText().toString().trim();
        String correo = inputCorreo.getText().toString().trim();
        String contrasena = inputContrasena.getText().toString().trim();
        String confirmarContrasena = inputConfirmarContrasena.getText().toString().trim();

        if (id.isEmpty() || nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|hotmail\\.com|outlook\\.com|live\\.com|yahoo\\.com|icloud\\.com|protonmail\\.com|aol\\.com|msn\\.com|zoho\\.com|gmx\\.com|mail\\.com|yahoo\\.com\\.mx|hotmail\\.com\\.mx|outlook\\.com\\.mx|live\\.com\\.mx|yahoo\\.com\\.co|hotmail\\.com\\.co|outlook\\.com\\.co)$";
        if (!correo.matches(emailPattern)) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico de un dominio válido.", Toast.LENGTH_LONG).show();
            return;
        }

        loadingSpinner.setVisibility(View.VISIBLE);
        btnRegistrar.setEnabled(false);

        SolicitudRegistro requestBody = new SolicitudRegistro(id, nombre, direccion, telefono, correo, contrasena);
        Call<RespuestaRegistro> call = api.registrarCliente(requestBody);

        call.enqueue(new Callback<RespuestaRegistro>() {
            @Override
            public void onResponse(Call<RespuestaRegistro> call, Response<RespuestaRegistro> response) {
                loadingSpinner.setVisibility(View.GONE);
                btnRegistrar.setEnabled(true);

                if (response.isSuccessful()) {
                    String mensaje = response.body().getMensaje() != null ? response.body().getMensaje() : "Registro exitoso. Redirigiendo al inicio de sesión...";
                    Toast.makeText(activity_registro.this, mensaje, Toast.LENGTH_LONG).show();

                    // Navegar a la actividad de Login después de un retraso
                    new Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(activity_registro.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },
                            2000
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
                    Toast.makeText(activity_registro.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaRegistro> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE);
                btnRegistrar.setEnabled(true);
                Toast.makeText(activity_registro.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public interface ApiService {
        @POST("api/clientes/")
        Call<RespuestaRegistro> registrarCliente(@Body SolicitudRegistro requestBody);
    }

    public static class SolicitudRegistro {
        private String clIdCliente;
        private String clNombre;
        private String clDireccion;
        private String clTelefono;
        private String clCorreoElectronico;
        private String clContrasena;

        public SolicitudRegistro(String clIdCliente, String clNombre, String clDireccion, String clTelefono, String clCorreoElectronico, String clContrasena) {
            this.clIdCliente = clIdCliente;
            this.clNombre = clNombre;
            this.clDireccion = clDireccion;
            this.clTelefono = clTelefono;
            this.clCorreoElectronico = clCorreoElectronico;
            this.clContrasena = clContrasena;
        }
    }

    public static class RespuestaRegistro {
        private String mensaje;
        public String getMensaje() {
            return mensaje;
        }
    }
}