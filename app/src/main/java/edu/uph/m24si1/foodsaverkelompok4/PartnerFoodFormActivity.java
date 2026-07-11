package edu.uph.m24si1.foodsaverkelompok4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PartnerFoodFormActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle;
    private EditText etName, etDescription, etOriginalPrice, etDiscountPrice;
    private EditText etQuantity, etAddress;
    private ImageView imgPreview;
    private Button btnSave, btnPickPhoto;

    private String selectedPhotoPath = null;
    private Uri cameraImageUri = null;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private boolean isEdit = false;
    private String foodId = null;
    private Food existingFood = null;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_food_form);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        foodId = getIntent().getStringExtra("foodId");

        initViews();
        setupLaunchers();
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
        imgPreview = findViewById(R.id.imgPreview);
        btnPickPhoto = findViewById(R.id.btnPickPhoto);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupLaunchers() {
        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            String savedPath = saveImageToInternalStorage(selectedImageUri);
                            if (savedPath != null) {
                                selectedPhotoPath = savedPath;
                                Glide.with(this).load(savedPath).into(imgPreview);
                            }
                        }
                    }
                }
        );

        // Camera Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (cameraImageUri != null) {
                            String savedPath = saveImageToInternalStorage(cameraImageUri);
                            if (savedPath != null) {
                                selectedPhotoPath = savedPath;
                                Glide.with(this).load(savedPath).into(imgPreview);
                            }
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        tvBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> handleSave());

        // Permudah: klik tombol atau klik gambar pratinjau
        btnPickPhoto.setOnClickListener(v -> showImagePickerDialog());
        imgPreview.setOnClickListener(v -> showImagePickerDialog());
    }

    private void showImagePickerDialog() {
        String[] options = {"Ambil Foto (Kamera)", "Pilih dari Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Foto Makanan");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createTempImageFile();
        } catch (Exception ex) {
            Toast.makeText(this, "Gagal membuat file foto", Toast.LENGTH_SHORT).show();
        }

        if (photoFile != null) {
            cameraImageUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            cameraLauncher.launch(intent);
        }
    }

    private File createTempImageFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
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
        selectedPhotoPath = existingFood.getPhotoUrl();

        if (selectedPhotoPath != null && !selectedPhotoPath.isEmpty()) {
            Glide.with(this).load(selectedPhotoPath).into(imgPreview);
        }
    }

    private void handleSave() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String originalPriceStr = etOriginalPrice.getText().toString().trim();
        String discountPriceStr = etDiscountPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Nama tidak boleh kosong"); etName.requestFocus(); return; }
        if (TextUtils.isEmpty(originalPriceStr)) { etOriginalPrice.setError("Harga asli wajib diisi"); etOriginalPrice.requestFocus(); return; }
        if (TextUtils.isEmpty(discountPriceStr)) { etDiscountPrice.setError("Harga diskon wajib diisi"); etDiscountPrice.requestFocus(); return; }
        if (TextUtils.isEmpty(quantityStr)) { etQuantity.setError("Stok wajib diisi"); etQuantity.requestFocus(); return; }
        if (TextUtils.isEmpty(address)) { etAddress.setError("Alamat wajib diisi"); etAddress.requestFocus(); return; }
        if (TextUtils.isEmpty(selectedPhotoPath)) {
            Toast.makeText(this, "Silakan pilih foto makanan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

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
        food.setPhotoUrl(selectedPhotoPath);
        food.setPartnerId(sessionManager.getUid());

        boolean success;
        if (isEdit && existingFood != null) {
            food.setFoodId(existingFood.getFoodId());
            food.setStatus(existingFood.getStatus());
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
