package com.juanmolina.lordwine;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.juanmolina.lordwine.ApiService;
import com.juanmolina.lordwine.model.Category;
import com.juanmolina.lordwine.HomeFragment;
import com.juanmolina.lordwine.activity_chatbot;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;

public class activity_home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final String BASE_URL = "https://lord-wine-backend.onrender.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

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
                    List<Category> categories = response.body();
                    navigationView.getMenu().clear();
                    for (int i = 0; i < categories.size(); i++) {
                        Category category = categories.get(i);
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}