package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private LinearLayout layoutOrderItems, layoutDeliveryDetails;
    private TextView tvTotalCheckout, tvBack;
    private EditText etCheckoutAddress;
    private Button btnConfirmCheckout;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            finish();
            return;
        }

        initViews();
        displayOrderSummary();
        setupClickListeners();
    }

    private void initViews() {
        layoutOrderItems = findViewById(R.id.layoutOrderItems);
        layoutDeliveryDetails = findViewById(R.id.layoutDeliveryDetails);
        tvTotalCheckout = findViewById(R.id.tvTotalCheckout);
        tvBack = findViewById(R.id.tvBack);
        etCheckoutAddress = findViewById(R.id.etCheckoutAddress);
        btnConfirmCheckout = findViewById(R.id.btnConfirmCheckout);
    }

    private void displayOrderSummary() {
        layoutOrderItems.removeAllViews();
        boolean hasDelivery = false;

        for (CartItem item : cartItems) {
            View itemView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null);
            TextView text1 = itemView.findViewById(android.R.id.text1);
            TextView text2 = itemView.findViewById(android.R.id.text2);

            text1.setText(item.getFoodName() + " x" + item.getQuantity());
            text1.setTextColor(0xFF1A1A1A);
            text1.setTextSize(14);
            
            String method = item.getDeliveryMethod().equalsIgnoreCase(Constants.METHOD_PICKUP) ? "Pickup" : "Delivery";
            text2.setText(method + " · " + item.getTimeSlot() + " · Rp" + (int)item.getSubtotal());
            text2.setTextColor(0xFF888888);
            text2.setTextSize(12);

            layoutOrderItems.addView(itemView);

            if (item.getDeliveryMethod().equalsIgnoreCase(Constants.METHOD_DELIVERY)) {
                hasDelivery = true;
            }
        }

        layoutDeliveryDetails.setVisibility(hasDelivery ? View.VISIBLE : View.GONE);
        tvTotalCheckout.setText("Rp" + (int) CartManager.getInstance().getTotalPrice());
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());
        btnConfirmCheckout.setOnClickListener(v -> handleConfirmOrder());
    }

    private void handleConfirmOrder() {
        String address = etCheckoutAddress.getText().toString().trim();
        
        // Check if delivery address is needed
        boolean hasDelivery = false;
        for (CartItem item : cartItems) {
            if (item.getDeliveryMethod().equalsIgnoreCase(Constants.METHOD_DELIVERY)) {
                hasDelivery = true;
                break;
            }
        }

        if (hasDelivery && TextUtils.isEmpty(address)) {
            etCheckoutAddress.setError("Alamat pengantaran wajib diisi");
            etCheckoutAddress.requestFocus();
            return;
        }

        // Save each item as a separate booking in SQLite
        boolean allSuccess = true;
        for (CartItem item : cartItems) {
            Booking booking = new Booking();
            booking.setUserId(sessionManager.getUid());
            booking.setUserName(sessionManager.getName());
            booking.setFoodId(item.getFoodId());
            booking.setFoodName(item.getFoodName());
            booking.setPartnerId(item.getPartnerId());
            booking.setPartnerName(item.getPartnerName());
            booking.setQuantity(item.getQuantity());
            booking.setDeliveryMethod(item.getDeliveryMethod());
            booking.setDeliveryAddress(item.getDeliveryMethod().equalsIgnoreCase(Constants.METHOD_DELIVERY) ? address : "");
            booking.setScheduledTimeSlot(item.getTimeSlot());
            booking.setStatus(Constants.BOOKING_PENDING);

            boolean success = dbHelper.insertBooking(booking);
            if (success) {
                // Reduce stock in DB
                dbHelper.reduceQuantity(item.getFoodId(), item.getQuantity());
            } else {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            CartManager.getInstance().clearCart();
            Toast.makeText(this, "Pesanan berhasil dikirim!", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Gagal memproses beberapa pesanan", Toast.LENGTH_SHORT).show();
        }
    }
}
