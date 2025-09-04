package com.juanmolina.lordwine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

public class activity_home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<Category> categoriesList;
    private static final String BASE_URL = "https://lord-wine-backend.onrender.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        categoriesList = new ArrayList<>();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                drawerLayout.closeDrawer(navigationView);
            } else if (itemId == R.id.nav_categorias) {
                drawerLayout.openDrawer(navigationView);
                return false;
            } else if (itemId == R.id.nav_chat) {
                Intent intent = new Intent(activity_home.this, activity_chatbot.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_perfil) {
                drawerLayout.closeDrawer(navigationView);
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        fetchAndShowCategories();

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
                } else if ("Zumo".equals(categoryName)) {
                    Intent intent = new Intent(this, activity_zumo.class);
                    startActivity(intent);
                } else if ("Ron".equals(categoryName)) {
                    Intent intent = new Intent(this, activity_ron.class);
                    startActivity(intent);
                } else if ("Mistela".equals(categoryName)) {
                    Intent intent = new Intent(this, activity_mistelas.class);
                    startActivity(intent);
                }
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
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
                    for (int i = 0; i < categoriesList.size(); i++) {
                        Category category = categoriesList.get(i);
                        navigationView.getMenu().add(0, category.getId(), i, category.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_CALL", "Error al obtener categorías: " + t.getMessage());
            }
        });
    }

    private boolean isVinoCategory(String categoryName) {
        return "Vino".equals(categoryName) ||
                "Vino Tinto".equals(categoryName) ||
                "Vino Rosado".equals(categoryName) ||
                "Vino Blanco".equals(categoryName) ||
                "Espumosos".equals(categoryName);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}