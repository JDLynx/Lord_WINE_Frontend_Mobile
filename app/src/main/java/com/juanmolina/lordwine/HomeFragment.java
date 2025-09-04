package com.juanmolina.lordwine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String BASE_URL = "https://lord-wine-backend.onrender.com/";

    private RecyclerView recyclerViewProductos;
    private ProgressBar progressBarLoading;
    private TextView textViewStatusMessage;

    private ProductosAdapter productosAdapter;
    private List<Producto> listaProductos;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar vistas del layout del fragmento
        recyclerViewProductos = view.findViewById(R.id.recyclerViewProductos);
        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        textViewStatusMessage = view.findViewById(R.id.textViewStatusMessage);

        // Configurar RecyclerView
        listaProductos = new ArrayList<>();
        productosAdapter = new ProductosAdapter(getContext(), listaProductos);
        recyclerViewProductos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewProductos.setAdapter(productosAdapter);

        fetchProductos();

        return view;
    }

    private void fetchProductos() {
        progressBarLoading.setVisibility(View.VISIBLE);
        recyclerViewProductos.setVisibility(View.GONE);
        textViewStatusMessage.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Producto>> call = apiService.getProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(@NonNull Call<List<Producto>> call, @NonNull Response<List<Producto>> response) {
                progressBarLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body();
                    if (productos.isEmpty()) {
                        textViewStatusMessage.setText("No se encontraron productos en este momento.");
                        textViewStatusMessage.setVisibility(View.VISIBLE);
                    } else {
                        listaProductos.clear();
                        listaProductos.addAll(productos);
                        productosAdapter.notifyDataSetChanged();
                        recyclerViewProductos.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                    textViewStatusMessage.setText("Error al cargar los productos. Inténtalo de nuevo más tarde.");
                    textViewStatusMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Producto>> call, @NonNull Throwable t) {
                progressBarLoading.setVisibility(View.GONE);
                Log.e(TAG, "Fallo de conexión", t);
                textViewStatusMessage.setText("Error de red: " + t.getMessage());
                textViewStatusMessage.setVisibility(View.VISIBLE);
            }
        });
    }
}