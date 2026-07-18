package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvTotalPrice, tvBack;
    private LinearLayout layoutEmpty, layoutSummary;
    private Button btnCheckout, btnGoShopping;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        updateUI();
        setupClickListeners();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvBack = findViewById(R.id.tvBack);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        layoutSummary = findViewById(R.id.layoutSummary);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnGoShopping = findViewById(R.id.btnGoShopping);
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this, CartManager.getInstance().getCartItems(), () -> {
            updateUI();
        });
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);
    }

    private void updateUI() {
        if (CartManager.getInstance().getItemCount() == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
            layoutSummary.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            layoutSummary.setVisibility(View.VISIBLE);
            tvTotalPrice.setText("Rp" + (int) CartManager.getInstance().getTotalPrice());
        }
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());
        btnGoShopping.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getItemCount() > 0) {
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
