package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister, tvGoToPartnerLogin, tvGoToPartnerRegister;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);


        if (sessionManager.isLoggedIn()) {
            redirectBasedOnRole();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvGoToPartnerLogin = findViewById(R.id.tvGoToPartnerLogin);
        tvGoToPartnerRegister = findViewById(R.id.tvGoToPartnerRegister);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        tvGoToPartnerLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, PartnerLoginActivity.class));
        });

        tvGoToPartnerRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("role", Constants.ROLE_PARTNER);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input tidak boleh kosong
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // Cek ke database
        User user = dbHelper.loginUser(email, password);

        if (user == null) {
            Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login berhasil — simpan sesi
        sessionManager.saveSession(user);
        redirectBasedOnRole();
    }

    private void redirectBasedOnRole() {
        if (sessionManager.isPartner()) {
            startActivity(new Intent(this, PartnerDashboardActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish(); // hapus LoginActivity dari back stack
    }
}