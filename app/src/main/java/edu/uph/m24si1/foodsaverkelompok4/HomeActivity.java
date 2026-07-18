package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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
    private TextView tvEmpty, tvCartBadge;
    private EditText etSearch;
    private FrameLayout btnCart;
    private TextView tabAll, tabBakery, tabRestoran, tabKafe, tabSwalayan;

    private String currentCategory = "Semua";

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupSearch();
        setupCategoryTabs();
        setupClickListeners();
        setupBottomNav();
        loadFoods();
    }

    private void initViews() {
        rvFoods = findViewById(R.id.rvFoods);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        btnCart = findViewById(R.id.btnCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);

        tabAll = findViewById(R.id.tabAll);
        tabBakery = findViewById(R.id.tabBakery);
        tabRestoran = findViewById(R.id.tabRestoran);
        tabKafe = findViewById(R.id.tabKafe);
        tabSwalayan = findViewById(R.id.tabSwalayan);
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(this, filteredFoods, food -> {
            Intent intent = new Intent(HomeActivity.this, FoodDetailActivity.class);
            intent.putExtra("foodId", food.getFoodId());
            intent.putExtra("foodName", food.getName());
            intent.putExtra("partnerAddress", food.getPartnerAddress());
            intent.putExtra("partnerName", food.getPartnerName());
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
                filterFoods();
            }
        });
    }

    private void setupCategoryTabs() {
        View.OnClickListener categoryClick = v -> {
            TextView clickedTab = (TextView) v;
            currentCategory = clickedTab.getText().toString();
            
            // Reset all tabs
            TextView[] tabs = {tabAll, tabBakery, tabRestoran, tabKafe, tabSwalayan};
            for (TextView tab : tabs) {
                tab.setBackgroundColor(0xFFFFFFFF);
                tab.setTextColor(0xFF555555);
            }
            
            // Active tab
            clickedTab.setBackgroundColor(0xFF4CAF50);
            clickedTab.setTextColor(0xFFFFFFFF);
            
            filterFoods();
        };

        tabAll.setOnClickListener(categoryClick);
        tabBakery.setOnClickListener(categoryClick);
        tabRestoran.setOnClickListener(categoryClick);
        tabKafe.setOnClickListener(categoryClick);
        tabSwalayan.setOnClickListener(categoryClick);
    }

    private void setupClickListeners() {
        btnCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }

    private void filterFoods() {
        String keyword = etSearch.getText().toString().toLowerCase().trim();
        filteredFoods.clear();

        for (Food food : allFoods) {
            boolean matchesSearch = food.getName().toLowerCase().contains(keyword);
            
            // Note: Since 'Category' is not a real field in current Food model, 
            // we'll simulate it for now. In real app, Food object should have categoryId.
            // For this demo, we'll just show all if matches search.
            if (matchesSearch) {
                filteredFoods.add(food);
            }
        }

        foodAdapter.updateList(filteredFoods);
        showEmptyOrList();
    }

    private void loadFoods() {
        progressBar.setVisibility(View.VISIBLE);
        allFoods = dbHelper.getAllAvailableFoods();
        filterFoods();
        progressBar.setVisibility(View.GONE);
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

    private void updateCartBadge() {
        int count = CartManager.getInstance().getItemCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void setupBottomNav() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {}); // Already here
        navHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFoods();
        updateCartBadge();
    }
}
