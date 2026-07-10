package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PartnerDashboardActivity extends AppCompatActivity {

    private TextView tvPartnerName, tvTotalFoods, tvTotalBookings, tvEmpty, tvLogout;
    private Button btnAddFood;
    private RecyclerView rvPartnerFoods;
    private LinearLayout navBookings;

    private PartnerFoodAdapter adapter;
    private List<Food> foodList = new ArrayList<>();

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn() || !sessionManager.isPartner()) {
            startActivity(new Intent(this, PartnerLoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadData();
    }

    private void initViews() {
        tvPartnerName = findViewById(R.id.tvPartnerName);
        tvTotalFoods = findViewById(R.id.tvTotalFoods);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvLogout = findViewById(R.id.tvLogout);
        btnAddFood = findViewById(R.id.btnAddFood);
        rvPartnerFoods = findViewById(R.id.rvPartnerFoods);
        navBookings = findViewById(R.id.navBookings);

        tvPartnerName.setText(sessionManager.getName());
    }

    private void setupRecyclerView() {
        adapter = new PartnerFoodAdapter(this, foodList, new PartnerFoodAdapter.OnFoodActionListener() {
            @Override
            public void onEdit(Food food) {
                Intent intent = new Intent(PartnerDashboardActivity.this, PartnerFoodFormActivity.class);
                intent.putExtra("foodId", food.getFoodId());
                intent.putExtra("isEdit", true);
                startActivity(intent);
            }

            @Override
            public void onDelete(Food food) {
                new AlertDialog.Builder(PartnerDashboardActivity.this)
                        .setTitle("Hapus Makanan")
                        .setMessage("Yakin hapus \"" + food.getName() + "\"?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            boolean success = dbHelper.deleteFood(food.getFoodId());
                            if (success) {
                                Toast.makeText(PartnerDashboardActivity.this,
                                        "Makanan dihapus", Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });

        rvPartnerFoods.setLayoutManager(new LinearLayoutManager(this));
        rvPartnerFoods.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(this, PartnerFoodFormActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
        });

        navBookings.setOnClickListener(v ->
                startActivity(new Intent(this, PartnerBookingListActivity.class)));

        tvLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Keluar")
                    .setMessage("Yakin ingin keluar?")
                    .setPositiveButton("Keluar", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void loadData() {
        String partnerId = sessionManager.getUid();

        foodList = dbHelper.getFoodsByPartnerId(partnerId);
        adapter.updateList(foodList);

        int totalBookings = dbHelper.getBookingsByPartnerId(partnerId).size();

        tvTotalFoods.setText(String.valueOf(foodList.size()));
        tvTotalBookings.setText(String.valueOf(totalBookings));

        if (foodList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPartnerFoods.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPartnerFoods.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}