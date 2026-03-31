package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvHeaderName;
    private ImageView ivAvatar;
    private EditText etName, etEmail, etAddress, etAvatarUrl, etDescription;
    private Button btnSave, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvHeaderName = findViewById(R.id.tvHeaderName);
        ivAvatar = findViewById(R.id.ivAvatar);
        etName = findViewById(R.id.etProfileName);
        etEmail = findViewById(R.id.etProfileEmail);
        etAddress = findViewById(R.id.etProfileAddress);
        etAvatarUrl = findViewById(R.id.etProfileAvatarUrl);
        etDescription = findViewById(R.id.etProfileDescription);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        android.content.SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedName = sharedPref.getString("Name", "");
        String savedEmail = sharedPref.getString("Email", "");
        String savedAddress = sharedPref.getString("Address", "");
        String savedAvatarUrl = sharedPref.getString("AvatarUrl", "");
        String savedDescription = sharedPref.getString("Description", "");
        tvHeaderName.setText(savedName);
        etName.setText(savedName);
        etEmail.setText(savedEmail);
        etAddress.setText(savedAddress);
        etAvatarUrl.setText(savedAvatarUrl);
        etDescription.setText(savedDescription);
        if (!savedAvatarUrl.isEmpty()) {
            com.bumptech.glide.Glide.with(ProfileActivity.this)
                    .load(savedAvatarUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivAvatar);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etName.getText().toString().trim();
                String newAddress = etAddress.getText().toString().trim();
                String newAvatarUrl = etAvatarUrl.getText().toString().trim();
                String newDescription = etDescription.getText().toString().trim();

                android.content.SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Name", newName);
                editor.putString("Address", newAddress);
                editor.putString("AvatarUrl", newAvatarUrl);
                editor.putString("Description", newDescription);
                editor.apply();

                tvHeaderName.setText(newName);

                if (!newAvatarUrl.isEmpty()) {
                    com.bumptech.glide.Glide.with(ProfileActivity.this)
                            .load(newAvatarUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_dialog_alert)
                            .into(ivAvatar);
                }

                Toast.makeText(ProfileActivity.this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().clear().apply();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}