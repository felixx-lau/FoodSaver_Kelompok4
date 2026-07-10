package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PartnerBookingListActivity extends AppCompatActivity {

    private RecyclerView rvPartnerBookings;
    private TextView tvEmpty, tvBack;
    private PartnerBookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_booking_list);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadBookings();

        tvBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        rvPartnerBookings = findViewById(R.id.rvPartnerBookings);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvBack = findViewById(R.id.tvBack);
    }

    private void setupRecyclerView() {
        adapter = new PartnerBookingAdapter(this, bookingList, (booking, newStatus) -> {
            boolean success = dbHelper.updateBookingStatus(booking.getBookingId(), newStatus);
            if (success) {
                Toast.makeText(this, "Status booking diperbarui", Toast.LENGTH_SHORT).show();
                loadBookings();
            } else {
                Toast.makeText(this, "Gagal update status", Toast.LENGTH_SHORT).show();
            }
        });

        rvPartnerBookings.setLayoutManager(new LinearLayoutManager(this));
        rvPartnerBookings.setAdapter(adapter);
    }

    private void loadBookings() {
        String partnerId = sessionManager.getUid();
        bookingList = dbHelper.getBookingsByPartnerId(partnerId);
        adapter.updateList(bookingList);

        if (bookingList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPartnerBookings.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPartnerBookings.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}