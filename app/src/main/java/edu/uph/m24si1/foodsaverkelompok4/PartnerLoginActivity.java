package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PartnerLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnPartnerLogin;
    private TextView tvBackToUser, tvGoToPartnerRegister;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_login);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn() && sessionManager.isPartner()) {
            startActivity(new Intent(this, PartnerDashboardActivity.class));
            finish();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnPartnerLogin = findViewById(R.id.btnPartnerLogin);
        tvBackToUser = findViewById(R.id.tvBackToUser);
        tvGoToPartnerRegister = findViewById(R.id.tvGoToPartnerRegister);
    }

    private void setupClickListeners() {
        btnPartnerLogin.setOnClickListener(v -> handleLogin());

        tvBackToUser.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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

        User user = dbHelper.loginUser(email, password);

        if (user == null) {
            Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pastikan yang login memang akun partner
        if (!Constants.ROLE_PARTNER.equals(user.getRole())) {
            Toast.makeText(this, "Akun ini bukan akun partner", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.saveSession(user);
        startActivity(new Intent(this, PartnerDashboardActivity.class));
        finish();
    }
}