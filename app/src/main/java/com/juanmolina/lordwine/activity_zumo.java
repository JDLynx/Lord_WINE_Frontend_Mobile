package com.juanmolina.lordwine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.juanmolina.lordwine.model.Category;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class activity_zumo extends AppCompatActivity {

    private RecyclerView recyclerViewZumos;
    private ProgressBar progressBarLoading;
    private TextView textViewStatusMessage;
    private ProductosAdapter productosAdapter;
    private List<Producto> listaZumos;
    private List<Producto> allProductsList;
    private static final String BASE_URL = "https://lord-wine-backend.onrender.com/";

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<Category> categoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zumo);

        recyclerViewZumos = findViewById(R.id.recyclerViewZumos);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        textViewStatusMessage = findViewById(R.id.textViewStatusMessage);

        bottomNavigationView = findViewById(R.id.bottom_navigation_zumo);
        drawerLayout = findViewById(R.id.drawer_layout_zumo);
        navigationView = findViewById(R.id.nav_view_zumo);

        categoriesList = new ArrayList<>();
        allProductsList = new ArrayList<>();

        listaZumos = new ArrayList<>();
        productosAdapter = new ProductosAdapter(this, listaZumos);

        recyclerViewZumos.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewZumos.setAdapter(productosAdapter);

        fetchZumos();
        fetchAndShowCategories();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, activity_home.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_categorias) {
                drawerLayout.openDrawer(navigationView);
                return false;
            } else if (itemId == R.id.nav_chat) {
                Intent intent = new Intent(this, activity_chatbot.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_perfil) {
                return true;
            }
            return true;
        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int categoryId = menuItem.getItemId();

            Category selectedCategory = null;
            for (Category category : categoriesList) {
                if (category.getId() == categoryId) {
                    selectedCategory = category;
                    break;
                }
            }

            if (selectedCategory != null) {
                String categoryName = selectedCategory.getName();
                if (isVinoCategory(categoryName)) {
                    // No hace nada para las categorías de vino
                } else if ("Ron".equals(categoryName)) {
                    Intent intent = new Intent(this, activity_ron.class);
                    startActivity(intent);
                } else if ("Mistela".equals(categoryName)) {
                    Intent intent = new Intent(this, activity_mistelas.class);
                    startActivity(intent);
                } else {
                    filterAndShowProducts(allProductsList, categoryName);
                }
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_categorias);
        }
    }

    private void fetchProductos() {
        progressBarLoading.setVisibility(View.VISIBLE);
        textViewStatusMessage.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Producto>> call = apiService.getProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                progressBarLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allProductsList.clear();
                    allProductsList.addAll(response.body());
                    filterAndShowProducts(allProductsList, "Zumo");
                } else {
                    textViewStatusMessage.setText("Error al cargar los productos.");
                    textViewStatusMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                progressBarLoading.setVisibility(View.GONE);
                textViewStatusMessage.setText("No se pudieron cargar los productos. Inténtalo de nuevo más tarde.");
                textViewStatusMessage.setVisibility(View.VISIBLE);
                Log.e("API_CALL", "Error al obtener productos: " + t.getMessage());
            }
        });
    }

    private void filterAndShowProducts(List<Producto> productosData, String categoryName) {
        List<Producto> productosFiltrados = new ArrayList<>();

        for (Producto producto : productosData) {
            if (producto.getProdNombre() != null && producto.getProdNombre().toLowerCase().contains(categoryName.toLowerCase())) {
                productosFiltrados.add(producto);
            }
        }

        if (productosFiltrados.isEmpty()) {
            textViewStatusMessage.setText("No se encontraron productos en esta categoría.");
            textViewStatusMessage.setVisibility(View.VISIBLE);
            recyclerViewZumos.setVisibility(View.GONE);
        } else {
            listaZumos.clear();
            listaZumos.addAll(productosFiltrados);
            productosAdapter.notifyDataSetChanged();
            recyclerViewZumos.setVisibility(View.VISIBLE);
            textViewStatusMessage.setVisibility(View.GONE);
        }
    }

    private void fetchAndShowCategories() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList.clear();
                    categoriesList.addAll(response.body());
                    navigationView.getMenu().clear();
                    for (Category category : categoriesList) {
                        navigationView.getMenu().add(0, category.getId(), 0, category.getName());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_CALL", "Error al obtener categorías: " + t.getMessage());
            }
        });
    }

    private void fetchZumos() {
        fetchProductos();
    }

    private boolean isVinoCategory(String categoryName) {
        return "Vino".equals(categoryName) ||
                "Vino Tinto".equals(categoryName) ||
                "Vino Rosado".equals(categoryName) ||
                "Vino Blanco".equals(categoryName) ||
                "Espumosos".equals(categoryName);
    }
}