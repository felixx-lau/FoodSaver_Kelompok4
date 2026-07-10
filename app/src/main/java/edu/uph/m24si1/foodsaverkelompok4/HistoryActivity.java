package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private BookingAdapter bookingAdapter;
    private TextView tvEmpty;
    private TextView tabSemua, tabBerlangsung, tabSelesai, tabDibatalkan;

    private List<Booking> allBookings = new ArrayList<>();
    private List<Booking> filteredBookings = new ArrayList<>();

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private String activeTab = "semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupTabs();
        setupBottomNav();
        loadBookings();
    }

    private void initViews() {
        rvBookings = findViewById(R.id.rvBookings);
        tvEmpty = findViewById(R.id.tvEmpty);
        tabSemua = findViewById(R.id.tabSemua);
        tabBerlangsung = findViewById(R.id.tabBerlangsung);
        tabSelesai = findViewById(R.id.tabSelesai);
        tabDibatalkan = findViewById(R.id.tabDibatalkan);
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(this, filteredBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(bookingAdapter);
    }

    private void setupTabs() {
        tabSemua.setOnClickListener(v -> {
            activeTab = "semua";
            updateTabStyle(tabSemua);
            filterBookings();
        });

        tabBerlangsung.setOnClickListener(v -> {
            activeTab = "berlangsung";
            updateTabStyle(tabBerlangsung);
            filterBookings();
        });

        tabSelesai.setOnClickListener(v -> {
            activeTab = "selesai";
            updateTabStyle(tabSelesai);
            filterBookings();
        });

        tabDibatalkan.setOnClickListener(v -> {
            activeTab = "dibatalkan";
            updateTabStyle(tabDibatalkan);
            filterBookings();
        });
    }

    private void updateTabStyle(TextView activeTabView) {
        // Reset semua tab
        TextView[] tabs = {tabSemua, tabBerlangsung, tabSelesai, tabDibatalkan};
        for (TextView tab : tabs) {
            tab.setBackgroundColor(Color.parseColor("#F0F0F0"));
            tab.setTextColor(Color.parseColor("#555555"));
            tab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        // Aktifkan tab yang diklik
        activeTabView.setBackgroundColor(Color.parseColor("#4CAF50"));
        activeTabView.setTextColor(Color.WHITE);
        activeTabView.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void filterBookings() {
        filteredBookings.clear();

        for (Booking booking : allBookings) {
            switch (activeTab) {
                case "semua":
                    filteredBookings.add(booking);
                    break;
                case "berlangsung":
                    if (Constants.BOOKING_PENDING.equals(booking.getStatus())
                            || Constants.BOOKING_READY.equals(booking.getStatus())) {
                        filteredBookings.add(booking);
                    }
                    break;
                case "selesai":
                    if (Constants.BOOKING_COMPLETED.equals(booking.getStatus())) {
                        filteredBookings.add(booking);
                    }
                    break;
                case "dibatalkan":
                    if (Constants.BOOKING_CANCELLED.equals(booking.getStatus())) {
                        filteredBookings.add(booking);
                    }
                    break;
            }
        }

        bookingAdapter.updateList(filteredBookings);
        showEmptyOrList();
    }

    private void loadBookings() {
        String uid = sessionManager.getUid();
        allBookings = dbHelper.getBookingsByUserId(uid);
        filterBookings();
    }

    private void showEmptyOrList() {
        if (filteredBookings.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvBookings.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
        }
    }

    private void setupBottomNav() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload setiap kembali ke halaman ini (status booking mungkin berubah)
        loadBookings();
    }
}