package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText etDeliveryAddress;
    private TextView slot1, slot2, slot3, slot4;
    private Button btnBooking;
    private TextView tvBack;

    private String selectedMethod = "pickup"; // default pickup
    private String selectedSlot = "";

    // Data makanan dari Intent
    private String foodId, foodName, partnerAddress, photoUrl, description, partnerId;
    private double originalPrice, discountPrice;
    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        getDataFromIntent();
        initViews();
        displayFoodData();
        setupPickupDelivery();
        setupTimeSlots();
        setupClickListeners();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        foodId = intent.getStringExtra("foodId");
        foodName = intent.getStringExtra("foodName");
        partnerAddress = intent.getStringExtra("partnerAddress");
        photoUrl = intent.getStringExtra("photoUrl");
        description = intent.getStringExtra("description");
        partnerId = intent.getStringExtra("partnerId");
        originalPrice = intent.getDoubleExtra("originalPrice", 0);
        discountPrice = intent.getDoubleExtra("discountPrice", 0);
        quantity = intent.getIntExtra("quantity", 0);
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
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        slot4 = findViewById(R.id.slot4);
        btnBooking = findViewById(R.id.btnBooking);
        tvBack = findViewById(R.id.tvBack);
    }

    private void displayFoodData() {
        tvFoodName.setText(foodName);
        tvPartnerInfo.setText(partnerAddress + " · sisa " + quantity + " porsi");
        tvDiscountPrice.setText("Rp" + (int) discountPrice);
        tvOriginalPrice.setText("Rp" + (int) originalPrice);
        tvAddress.setText(partnerAddress != null ? partnerAddress : "-");
        tvDescription.setText(description != null && !description.isEmpty() ? description : "-");

        // Hitung dan tampilkan persen diskon
        if (originalPrice > 0) {
            int persen = (int) (((originalPrice - discountPrice) / originalPrice) * 100);
            tvDiscountBadge.setText("Hemat " + persen + "%");
        }

        // Load foto
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(imgFood);
        }
    }

    private void setupPickupDelivery() {
        // Pickup selected by default (tampilan sudah di-set di XML)
        btnPickup.setOnClickListener(v -> {
            selectedMethod = Constants.METHOD_PICKUP;
            btnPickup.setBackgroundColor(0xFFE8F5E9);
            btnDelivery.setBackgroundColor(0xFFF5F5F5);
            etDeliveryAddress.setVisibility(View.GONE);
        });

        btnDelivery.setOnClickListener(v -> {
            selectedMethod = Constants.METHOD_DELIVERY;
            btnDelivery.setBackgroundColor(0xFFE8F5E9);
            btnPickup.setBackgroundColor(0xFFF5F5F5);
            etDeliveryAddress.setVisibility(View.VISIBLE);
        });
    }

    private void setupTimeSlots() {
        TextView[] slots = {slot1, slot2, slot3, slot4};

        for (TextView slot : slots) {
            slot.setOnClickListener(v -> {
                // Reset semua slot
                for (TextView s : slots) {
                    s.setBackgroundColor(0xFFF5F5F5);
                    s.setTypeface(null, Typeface.NORMAL);
                }
                // Highlight slot yang dipilih
                slot.setBackgroundColor(0xFFE8F5E9);
                slot.setTypeface(null, Typeface.BOLD);
                selectedSlot = slot.getText().toString();
            });
        }
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());

        btnBooking.setOnClickListener(v -> handleBooking());
    }

    private void handleBooking() {
        // Validasi slot waktu dipilih
        if (selectedSlot.isEmpty()) {
            Toast.makeText(this, "Pilih jadwal waktu dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi alamat kalau delivery
        String deliveryAddress = "";
        if (selectedMethod.equals(Constants.METHOD_DELIVERY)) {
            deliveryAddress = etDeliveryAddress.getText().toString().trim();
            if (deliveryAddress.isEmpty()) {
                etDeliveryAddress.setError("Masukkan alamat pengantaran");
                etDeliveryAddress.requestFocus();
                return;
            }
        }

        // Kirim data ke BookingActivity (Orang 3)
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("foodId", foodId);
        intent.putExtra("foodName", foodName);
        intent.putExtra("partnerId", partnerId);
        intent.putExtra("discountPrice", discountPrice);
        intent.putExtra("deliveryMethod", selectedMethod);
        intent.putExtra("deliveryAddress", deliveryAddress);
        intent.putExtra("timeSlot", selectedSlot);
        intent.putExtra("maxQuantity", quantity);
        startActivity(intent);
    }
}
