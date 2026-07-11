package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class PartnerFoodFormActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle;
    private EditText etName, etDescription, etOriginalPrice, etDiscountPrice;
    private EditText etQuantity, etAddress, etPhotoUrl;
    private ImageView imgPreview;
    private Button btnSave, btnPickPhoto;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private boolean isEdit = false;
    private String foodId = null;
    private Food existingFood = null;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_food_form);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        foodId = getIntent().getStringExtra("foodId");

        initViews();
        setupImagePicker();
        setupClickListeners();

        if (isEdit && foodId != null) {
            tvTitle.setText("Edit Makanan");
            loadExistingFood();
        } else {
            tvTitle.setText("Tambah Makanan");
        }
    }

    private void initViews() {
        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etOriginalPrice = findViewById(R.id.etOriginalPrice);
        etDiscountPrice = findViewById(R.id.etDiscountPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etAddress = findViewById(R.id.etAddress);
        etPhotoUrl = findViewById(R.id.etPhotoUrl);
        imgPreview = findViewById(R.id.imgPreview);
        btnPickPhoto = findViewById(R.id.btnPickPhoto);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            String savedPath = saveImageToInternalStorage(selectedImageUri);
                            if (savedPath != null) {
                                etPhotoUrl.setText(savedPath);
                                Glide.with(this).load(savedPath).into(imgPreview);
                            }
                        }
                    }
                }
        );
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            String fileName = "food_" + UUID.randomUUID().toString() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void loadExistingFood() {
        existingFood = dbHelper.getFoodById(foodId);
        if (existingFood == null) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName.setText(existingFood.getName());
        etDescription.setText(existingFood.getDescription());
        etOriginalPrice.setText(String.valueOf((int) existingFood.getOriginalPrice()));
        etDiscountPrice.setText(String.valueOf((int) existingFood.getDiscountPrice()));
        etQuantity.setText(String.valueOf(existingFood.getQuantity()));
        etAddress.setText(existingFood.getPartnerAddress());
        etPhotoUrl.setText(existingFood.getPhotoUrl());

        if (existingFood.getPhotoUrl() != null && !existingFood.getPhotoUrl().isEmpty()) {
            Glide.with(this).load(existingFood.getPhotoUrl()).into(imgPreview);
        }
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> handleSave());

        btnPickPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Update preview saat URL berubah
        etPhotoUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(PartnerFoodFormActivity.this)
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .into(imgPreview);
                } else {
                    imgPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        });
    }

    private void handleSave() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String originalPriceStr = etOriginalPrice.getText().toString().trim();
        String discountPriceStr = etDiscountPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String photoUrl = etPhotoUrl.getText().toString().trim();

        // Validasi field wajib
        if (TextUtils.isEmpty(name)) { etName.setError("Nama tidak boleh kosong"); etName.requestFocus(); return; }
        if (TextUtils.isEmpty(originalPriceStr)) { etOriginalPrice.setError("Harga asli wajib diisi"); etOriginalPrice.requestFocus(); return; }
        if (TextUtils.isEmpty(discountPriceStr)) { etDiscountPrice.setError("Harga diskon wajib diisi"); etDiscountPrice.requestFocus(); return; }
        if (TextUtils.isEmpty(quantityStr)) { etQuantity.setError("Stok wajib diisi"); etQuantity.requestFocus(); return; }
        if (TextUtils.isEmpty(address)) { etAddress.setError("Alamat wajib diisi"); etAddress.requestFocus(); return; }

        double originalPrice = Double.parseDouble(originalPriceStr);
        double discountPrice = Double.parseDouble(discountPriceStr);
        int quantity = Integer.parseInt(quantityStr);

        if (discountPrice >= originalPrice) {
            etDiscountPrice.setError("Harga diskon harus lebih kecil dari harga asli");
            etDiscountPrice.requestFocus();
            return;
        }

        if (quantity <= 0) {
            etQuantity.setError("Stok minimal 1");
            etQuantity.requestFocus();
            return;
        }

        Food food = new Food();
        food.setName(name);
        food.setDescription(description);
        food.setOriginalPrice(originalPrice);
        food.setDiscountPrice(discountPrice);
        food.setQuantity(quantity);
        food.setPartnerAddress(address);
        food.setPhotoUrl(photoUrl.isEmpty() ? null : photoUrl);
        food.setPartnerId(sessionManager.getUid());

        boolean success;

        if (isEdit && existingFood != null) {
            food.setFoodId(existingFood.getFoodId());
            food.setStatus(existingFood.getStatus());
            // Kalau stok di-update jadi > 0, ubah status kembali ke available
            if (quantity > 0) food.setStatus(Constants.FOOD_AVAILABLE);
            success = dbHelper.updateFood(food);
            if (success) Toast.makeText(this, "Makanan berhasil diperbarui", Toast.LENGTH_SHORT).show();
        } else {
            success = dbHelper.insertFood(food);
            if (success) Toast.makeText(this, "Makanan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        }

        if (success) finish();
        else Toast.makeText(this, "Gagal menyimpan, coba lagi", Toast.LENGTH_SHORT).show();
    }
}