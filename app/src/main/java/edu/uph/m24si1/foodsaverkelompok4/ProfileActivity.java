package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvAvatar, tvName, tvEmail;
    private TextView tvTotalBooking, tvTotalSelamat;
    private Button btnEdit, btnLogout;
    private LinearLayout menuDataDiri, menuRiwayat, navHome, navHistory;

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);


        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        loadProfileData();
        setupClickListeners();
    }

    private void initViews() {
        tvAvatar = findViewById(R.id.tvAvatar);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvTotalBooking = findViewById(R.id.tvTotalBooking);
        tvTotalSelamat = findViewById(R.id.tvTotalSelamat);
        btnEdit = findViewById(R.id.btnEdit);
        btnLogout = findViewById(R.id.btnLogout);
        menuDataDiri = findViewById(R.id.menuDataDiri);
        menuRiwayat = findViewById(R.id.menuRiwayat);
        navHome = findViewById(R.id.navHome);
        navHistory = findViewById(R.id.navHistory);
    }

    private void loadProfileData() {
        String name = sessionManager.getName();
        String email = sessionManager.getEmail();
        String uid = sessionManager.getUid();


        if (name != null && !name.isEmpty()) {
            tvAvatar.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }

        tvName.setText(name);
        tvEmail.setText(email);


        int totalBooking = dbHelper.getBookingsByUserId(uid).size();
        int totalSelamat = dbHelper.countCompletedBookings(uid);

        tvTotalBooking.setText(String.valueOf(totalBooking));
        tvTotalSelamat.setText(String.valueOf(totalSelamat));
    }

    private void setupClickListeners() {
        // Tombol edit profil
        btnEdit.setOnClickListener(v -> {
            // Untuk sekarang tampilkan dialog edit nama sederhana
            showEditNameDialog();
        });

        // Menu riwayat booking
        menuRiwayat.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        // Tombol logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Bottom navigation
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        navHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }

    private void showEditNameDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Nama baru");
        input.setText(sessionManager.getName());

        new AlertDialog.Builder(this)
                .setTitle("Edit Nama")
                .setView(input)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        // Update di database
                        User user = dbHelper.getUserById(sessionManager.getUid());
                        if (user != null) {
                            user.setName(newName);
                            dbHelper.updateUser(user);
                            // Update juga di session
                            sessionManager.saveSession(user);
                            tvName.setText(newName);
                            tvAvatar.setText(String.valueOf(newName.charAt(0)).toUpperCase());
                        }
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Apakah kamu yakin ingin keluar?")
                .setPositiveButton("Keluar", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}