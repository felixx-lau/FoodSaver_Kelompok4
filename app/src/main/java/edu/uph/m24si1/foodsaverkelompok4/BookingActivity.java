package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private TextView tvBack, tvFoodName, tvMethod, tvTimeSlot;
    private TextView tvDeliveryAddress, tvQuantity, tvMaxQty;
    private TextView tvPricePerItem, tvTotalPrice;
    private LinearLayout rowDeliveryAddress;
    private Button btnMinus, btnPlus, btnConfirm;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // Data dari Intent
    private String foodId, foodName, partnerId;
    private String deliveryMethod, deliveryAddress, timeSlot;
    private double discountPrice;
    private int maxQuantity;
    private int currentQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        getDataFromIntent();
        initViews();
        displayData();
        setupQuantityButtons();
        setupClickListeners();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        foodId = intent.getStringExtra("foodId");
        foodName = intent.getStringExtra("foodName");
        partnerId = intent.getStringExtra("partnerId");
        deliveryMethod = intent.getStringExtra("deliveryMethod");
        deliveryAddress = intent.getStringExtra("deliveryAddress");
        timeSlot = intent.getStringExtra("timeSlot");
        discountPrice = intent.getDoubleExtra("discountPrice", 0);
        maxQuantity = intent.getIntExtra("maxQuantity", 1);
    }

    private void initViews() {
        tvBack = findViewById(R.id.tvBack);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvMethod = findViewById(R.id.tvMethod);
        tvTimeSlot = findViewById(R.id.tvTimeSlot);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        rowDeliveryAddress = findViewById(R.id.rowDeliveryAddress);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvMaxQty = findViewById(R.id.tvMaxQty);
        tvPricePerItem = findViewById(R.id.tvPricePerItem);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void displayData() {
        tvFoodName.setText(foodName);
        tvTimeSlot.setText(timeSlot);
        tvMaxQty.setText("(maks. " + maxQuantity + " porsi)");
        tvPricePerItem.setText("Rp" + (int) discountPrice);

        // Tampilkan metode
        if (Constants.METHOD_PICKUP.equals(deliveryMethod)) {
            tvMethod.setText("🚶 Pickup");
            rowDeliveryAddress.setVisibility(View.GONE);
        } else {
            tvMethod.setText("🚴 Delivery");
            rowDeliveryAddress.setVisibility(View.VISIBLE);
            tvDeliveryAddress.setText(deliveryAddress);
        }

        updateTotalPrice();
    }

    private void setupQuantityButtons() {
        btnMinus.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
                updateTotalPrice();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (currentQuantity < maxQuantity) {
                currentQuantity++;
                tvQuantity.setText(String.valueOf(currentQuantity));
                updateTotalPrice();
            } else {
                Toast.makeText(this, "Stok hanya tersedia " + maxQuantity + " porsi",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice() {
        double total = discountPrice * currentQuantity;
        tvTotalPrice.setText("Rp" + (int) total);
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> handleConfirmBooking());
    }

    private void handleConfirmBooking() {

        btnConfirm.setEnabled(false);
        btnConfirm.setText("Memproses...");

        String userId = sessionManager.getUid();
        String userName = sessionManager.getName();

        // Buat objek Booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFoodId(foodId);
        booking.setPartnerId(partnerId);
        booking.setFoodName(foodName);
        booking.setUserName(userName);
        booking.setQuantity(currentQuantity);
        booking.setStatus(Constants.BOOKING_PENDING);
        booking.setDeliveryMethod(deliveryMethod);
        booking.setDeliveryAddress(deliveryAddress);
        booking.setScheduledTimeSlot(timeSlot);

        // Simpan booking ke SQLite
        boolean bookingSuccess = dbHelper.insertBooking(booking);

        if (!bookingSuccess) {
            Toast.makeText(this, "Gagal membuat booking, coba lagi",
                    Toast.LENGTH_SHORT).show();
            btnConfirm.setEnabled(true);
            btnConfirm.setText("Konfirmasi Booking");
            return;
        }

        // Kurangi stok makanan
        boolean reduceSuccess = dbHelper.reduceQuantity(foodId, currentQuantity);

        if (!reduceSuccess) {
            Toast.makeText(this, "Stok tidak cukup", Toast.LENGTH_SHORT).show();
            btnConfirm.setEnabled(true);
            btnConfirm.setText("Konfirmasi Booking");
            return;
        }

        // Berhasil — pergi ke History
        Toast.makeText(this, "Booking berhasil! 🎉", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}