package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvFoods;
    private FoodAdapter foodAdapter;
    private List<Food> allFoods = new ArrayList<>();
    private List<Food> filteredFoods = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView tvEmpty, tvBannerTitle;
    private EditText etSearch;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Proteksi: kalau belum login, paksa ke Login
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupSearch();
        setupBottomNav();
        loadFoods();
        updateBanner();
    }

    private void initViews() {
        rvFoods = findViewById(R.id.rvFoods);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvBannerTitle = findViewById(R.id.tvBannerTitle);
        etSearch = findViewById(R.id.etSearch);
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(this, filteredFoods, food -> {
            Intent intent = new Intent(HomeActivity.this, FoodDetailActivity.class);
            intent.putExtra("foodId", food.getFoodId());
            intent.putExtra("foodName", food.getName());
            intent.putExtra("partnerAddress", food.getPartnerAddress());
            intent.putExtra("originalPrice", food.getOriginalPrice());
            intent.putExtra("discountPrice", food.getDiscountPrice());
            intent.putExtra("photoUrl", food.getPhotoUrl());
            intent.putExtra("description", food.getDescription());
            intent.putExtra("partnerId", food.getPartnerId());
            intent.putExtra("quantity", food.getQuantity());
            startActivity(intent);
        });

        rvFoods.setLayoutManager(new LinearLayoutManager(this));
        rvFoods.setAdapter(foodAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoods(s.toString());
            }
        });
    }

    private void filterFoods(String keyword) {
        filteredFoods.clear();

        if (keyword.isEmpty()) {
            filteredFoods.addAll(allFoods);
        } else {
            String lower = keyword.toLowerCase();
            for (Food food : allFoods) {
                if (food.getName().toLowerCase().contains(lower)) {
                    filteredFoods.add(food);
                }
            }
        }

        foodAdapter.updateList(filteredFoods);
        showEmptyOrList();
    }

    private void loadFoods() {
        showLoading(true);
        allFoods = dbHelper.getAllAvailableFoods();
        filteredFoods.clear();
        filteredFoods.addAll(allFoods);
        foodAdapter.updateList(filteredFoods);
        showLoading(false);
        showEmptyOrList();
    }

    private void updateBanner() {
        String uid = sessionManager.getUid();
        int total = dbHelper.countCompletedBookings(uid);
        tvBannerTitle.setText("Kamu sudah selamatkan " + total + " porsi makanan");
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showEmptyOrList() {
        if (filteredFoods.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFoods.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvFoods.setVisibility(View.VISIBLE);
        }
    }

    private void setupBottomNav() {
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navSearch = findViewById(R.id.navSearch);

        navHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        navSearch.setOnClickListener(v ->
                etSearch.requestFocus());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload setiap kembali ke Home supaya stok selalu terbaru
        loadFoods();
        updateBanner();
    }
}
