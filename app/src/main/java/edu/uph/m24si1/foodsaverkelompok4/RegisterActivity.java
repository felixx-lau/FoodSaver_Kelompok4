package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvBack, tvGoToLogin, tvSwitchToPartner;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);


        role = getIntent().getStringExtra("role");
        if (role == null) {
            role = Constants.ROLE_USER;
        }

        initViews();
        setupClickListeners();
        updateUIBasedOnRole();
    }

    private void updateUIBasedOnRole() {
        if (Constants.ROLE_PARTNER.equals(role)) {
            TextView tvTitle = findViewById(R.id.tvTitle);
            if (tvTitle != null) tvTitle.setText("Daftar Akun Partner");
            tvSwitchToPartner.setVisibility(android.view.View.GONE);
        }
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBack = findViewById(R.id.tvBack);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvSwitchToPartner = findViewById(R.id.tvSwitchToPartner);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());

        tvBack.setOnClickListener(v -> finish());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        tvSwitchToPartner.setOnClickListener(v -> {
            role = Constants.ROLE_PARTNER;
            updateUIBasedOnRole();
        });
    }

    private void handleRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validasi satu per satu
        if (TextUtils.isEmpty(name)) {
            etName.setError("Nama tidak boleh kosong");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            etConfirmPassword.requestFocus();
            return;
        }

        // Buat objek User baru
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role);

        // Simpan ke database
        boolean success = dbHelper.registerUser(newUser);

        if (!success) {
            Toast.makeText(this, "Email sudah terdaftar, coba email lain",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Auto login setelah register berhasil
        User savedUser = dbHelper.getUserByEmail(email);
        if (savedUser != null) {
            sessionManager.saveSession(savedUser);
        }

        Toast.makeText(this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show();

        // Redirect based on role
        Intent intent;
        if (Constants.ROLE_PARTNER.equals(role)) {
            intent = new Intent(this, PartnerDashboardActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}