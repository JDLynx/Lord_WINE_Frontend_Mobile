package com.juanmolina.lordwine;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.juanmolina.lordwine.ApiService;
import com.juanmolina.lordwine.UsuarioLogin;
import com.juanmolina.lordwine.RespuestaAutenticacion;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextCorreo;
    private EditText editTextContrasena;
    private Button buttonIniciarSesion;
    private TextView textViewOlvidoContrasena;
    private TextView textViewEnlaceRegistro; // Nuevo: TextView para el enlace de registro

    private static final String URL_BASE = "https://lord-wine-backend.onrender.com/";
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextContrasena = findViewById(R.id.editTextContrasena);
        buttonIniciarSesion = findViewById(R.id.buttonIniciarSesion);
        textViewOlvidoContrasena = findViewById(R.id.textViewOlvidoContrasena);
        textViewEnlaceRegistro = findViewById(R.id.textViewEnlaceRegistro); // Nuevo: Inicializar el TextView del enlace

        buttonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        // Listener para el enlace de "Olvidaste tu contraseña"
        textViewOlvidoContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, activity_solicitar_recuperacion.class);
                startActivity(intent);
            }
        });

        // Nuevo: Listener para el enlace de "Regístrate aquí."
        textViewEnlaceRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, activity_registro.class);
                startActivity(intent);
            }
        });
    }

    private void iniciarSesion() {
        String correo = editTextCorreo.getText().toString().trim();
        String contrasena = editTextContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        UsuarioLogin usuarioLogin = new UsuarioLogin(correo, contrasena);

        Call<RespuestaAutenticacion> call = apiService.iniciarSesion(usuarioLogin);

        call.enqueue(new Callback<RespuestaAutenticacion>() {
            @Override
            public void onResponse(Call<RespuestaAutenticacion> call, Response<RespuestaAutenticacion> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RespuestaAutenticacion respuesta = response.body();
                    Log.d(TAG, "Respuesta de la API (cuerpo): " + respuesta.toString());

                    if (respuesta.getToken() != null && respuesta.getRol() != null) {
                        String rolUsuario = respuesta.getRol();
                        Toast.makeText(LoginActivity.this, "¡Bienvenido, " + rolUsuario + "!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(LoginActivity.this, "Respuesta de API inválida: token o rol incompletos.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaAutenticacion> call, Throwable t) {
                Log.e(TAG, "Fallo de conexión", t);
                Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}