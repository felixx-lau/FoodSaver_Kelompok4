package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgFood;
    private TextView tvFoodName, tvPartnerInfo, tvDiscountPrice, tvOriginalPrice;
    private TextView tvDiscountBadge, tvDescription, tvAddress;
    private LinearLayout btnPickup, btnDelivery;
    private TextView slot1, slot2, slot3, slot4;
    private TextView tvQuantity, btnMinus, btnPlus;
    private Button btnAddToCart;
    private TextView tvBack;

    private String selectedMethod = Constants.METHOD_PICKUP;
    private String selectedSlot = "";
    private int currentQuantity = 1;

    // Data dari Intent
    private String foodId, foodName, partnerAddress, partnerName, photoUrl, description, partnerId;
    private double originalPrice, discountPrice;
    private int stock;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        getDataFromIntent();
        initViews();
        displayFoodData();
        setupPickupDelivery();
        setupTimeSlots();
        setupQuantitySelector();
        setupClickListeners();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        foodId = intent.getStringExtra("foodId");
        foodName = intent.getStringExtra("foodName");
        partnerAddress = intent.getStringExtra("partnerAddress");
        partnerName = intent.getStringExtra("partnerName");
        photoUrl = intent.getStringExtra("photoUrl");
        description = intent.getStringExtra("description");
        partnerId = intent.getStringExtra("partnerId");
        originalPrice = intent.getDoubleExtra("originalPrice", 0);
        discountPrice = intent.getDoubleExtra("discountPrice", 0);
        stock = intent.getIntExtra("quantity", 0);
    }

    private void initViews() {
        imgFood = findViewById(R.id.imgFood);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvPartnerInfo = findViewById(R.id.tvPartnerInfo);
        tvDiscountPrice = findViewById(R.id.tvDiscountPrice);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvDiscountBadge = findViewById(R.id.tvDiscountBadge);
        tvDescription = findViewById(R.id.tvDescription);
        tvAddress = findViewById(R.id.tvAddress);
        btnPickup = findViewById(R.id.btnPickup);
        btnDelivery = findViewById(R.id.btnDelivery);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        slot4 = findViewById(R.id.slot4);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        tvBack = findViewById(R.id.tvBack);
    }

    private void displayFoodData() {
        tvFoodName.setText(foodName);
        String displayPartner = (partnerName != null && !partnerName.isEmpty()) ? partnerName : "Penjual";
        tvPartnerInfo.setText(displayPartner + " · sisa " + stock + " porsi");
        
        tvDiscountPrice.setText("Rp" + (int) discountPrice);
        tvOriginalPrice.setText("Rp" + (int) originalPrice);
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        
        tvAddress.setText(partnerAddress != null ? partnerAddress : "-");
        tvDescription.setText(description != null && !description.isEmpty() ? description : "-");

        if (originalPrice > 0) {
            int percent = (int) (((originalPrice - discountPrice) / originalPrice) * 100);
            tvDiscountBadge.setText("Hemat " + percent + "%");
        }

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this).load(photoUrl).placeholder(android.R.drawable.ic_menu_gallery).into(imgFood);
        }
    }

    private void setupPickupDelivery() {
        btnPickup.setOnClickListener(v -> {
            selectedMethod = Constants.METHOD_PICKUP;
            btnPickup.setBackgroundColor(0xFFE8F5E9);
            btnDelivery.setBackgroundColor(0xFFF5F5F5);
            ((TextView) btnPickup.getChildAt(1)).setTypeface(null, Typeface.BOLD);
            ((TextView) btnPickup.getChildAt(1)).setTextColor(0xFF2E7D32);
            ((TextView) btnDelivery.getChildAt(1)).setTypeface(null, Typeface.NORMAL);
            ((TextView) btnDelivery.getChildAt(1)).setTextColor(0xFF555555);
        });

        btnDelivery.setOnClickListener(v -> {
            selectedMethod = Constants.METHOD_DELIVERY;
            btnDelivery.setBackgroundColor(0xFFE8F5E9);
            btnPickup.setBackgroundColor(0xFFF5F5F5);
            ((TextView) btnDelivery.getChildAt(1)).setTypeface(null, Typeface.BOLD);
            ((TextView) btnDelivery.getChildAt(1)).setTextColor(0xFF2E7D32);
            ((TextView) btnPickup.getChildAt(1)).setTypeface(null, Typeface.NORMAL);
            ((TextView) btnPickup.getChildAt(1)).setTextColor(0xFF555555);
        });
    }

    private void setupTimeSlots() {
        TextView[] slots = {slot1, slot2, slot3, slot4};
        for (TextView slot : slots) {
            slot.setOnClickListener(v -> {
                for (TextView s : slots) {
                    s.setBackgroundColor(0xFFF5F5F5);
                    s.setTextColor(0xFF333333);
                    s.setTypeface(null, Typeface.NORMAL);
                }
                slot.setBackgroundColor(0xFFE8F5E9);
                slot.setTextColor(0xFF2E7D32);
                slot.setTypeface(null, Typeface.BOLD);
                selectedSlot = slot.getText().toString();
            });
        }
    }

    private void setupQuantitySelector() {
        btnMinus.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (currentQuantity < stock) {
                currentQuantity++;
                tvQuantity.setText(String.valueOf(currentQuantity));
            } else {
                Toast.makeText(this, "Stok terbatas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());
        btnAddToCart.setOnClickListener(v -> handleAddToCart());
    }

    private void handleAddToCart() {
        if (selectedSlot.isEmpty()) {
            Toast.makeText(this, "Pilih slot waktu terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem item = new CartItem(
                foodId, foodName, partnerId, partnerName, partnerAddress,
                discountPrice, currentQuantity, selectedMethod, selectedSlot, photoUrl
        );

        CartManager.getInstance().addItem(item);
        Toast.makeText(this, "Berhasil ditambah ke keranjang! 🛒", Toast.LENGTH_SHORT).show();
        finish();
    }
}
