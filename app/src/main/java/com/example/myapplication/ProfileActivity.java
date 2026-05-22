package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import api.ApiClient;
import api.ApiService;
import models.ProfileResponse;
import models.UpdateProfile;
import models.UpdateProfileResponse;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvHeaderName;
    private ImageView ivAvatar;
    private EditText etName, etEmail, etAddress, etAvatarUrl, etDescription;
    private Button btnSave, btnLogout;
    private SharedPreferences sharedPref;
    
    private int currentUserId;
    private String currentUser;
    private int postAuthorId;
    private String postAuthorName;

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

        sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        currentUserId = getIntent().getIntExtra("CURRENT_USER_ID", -1);
        if (currentUserId == -1) {
            currentUserId = sharedPref.getInt("Id", -1);
        }
        
        currentUser = getIntent().getStringExtra("CURRENT_USER");
        if (currentUser == null) {
            currentUser = sharedPref.getString("Name", "");
        }

        postAuthorId = getIntent().getIntExtra("POST_AUTHOR_ID", -1);
        if (postAuthorId == -1) {
            postAuthorId = currentUserId;
        }

        postAuthorName = getIntent().getStringExtra("POST_AUTHOR_NAME");
        if (postAuthorName == null) {
            postAuthorName = currentUser;
        }

        boolean isSelf = (postAuthorId == currentUserId);

        if (isSelf) {
            enableEditing(true);
            btnSave.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            enableEditing(false);
            btnSave.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }

        loadProfile(postAuthorId, isSelf);

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newAddress = etAddress.getText().toString().trim();
            String newAvatarUrl = etAvatarUrl.getText().toString().trim();
            String newDescription = etDescription.getText().toString().trim();
            String currentPhone = sharedPref.getString("Phone", "");

            if (newName.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getApiService();
            UpdateProfile updatePayload = new UpdateProfile(newName, currentPhone, newAddress, newDescription, newAvatarUrl);

            apiService.patchUserProfile(currentUserId, updatePayload).enqueue(new Callback<UpdateProfileResponse>() {
                @Override
                public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UpdateProfileResponse updateResponse = response.body();
                        if ("success".equals(updateResponse.getStatus()) && updateResponse.getUser() != null) {
                            User updatedUser = updateResponse.getUser();
                            
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("Name", updatedUser.getName());
                            editor.putString("Address", updatedUser.getAddress() != null ? updatedUser.getAddress() : "");
                            editor.putString("AvatarUrl", updatedUser.getAvatarUrl() != null ? updatedUser.getAvatarUrl() : "");
                            editor.putString("Description", updatedUser.getDescription() != null ? updatedUser.getDescription() : "");
                            editor.apply();

                            tvHeaderName.setText(updatedUser.getName());
                            
                            String avatarUrl = updatedUser.getAvatarUrl();
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                com.bumptech.glide.Glide.with(ProfileActivity.this)
                                        .load(avatarUrl)
                                        .placeholder(android.R.drawable.ic_menu_gallery)
                                        .error(android.R.drawable.ic_dialog_alert)
                                        .into(ivAvatar);
                            }

                            Toast.makeText(ProfileActivity.this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Lưu thất bại: " + updateResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Lưu thất bại (Lỗi Server)!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile(int userId, boolean isSelf) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getUserProfile(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    if (user != null) {
                        tvHeaderName.setText(user.getName());
                        etName.setText(user.getName());
                        etEmail.setText(isSelf ? user.getEmail() : "Hided");
                        etAddress.setText(user.getAddress() != null ? user.getAddress() : "");
                        etAvatarUrl.setText(user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
                        etDescription.setText(user.getDescription() != null ? user.getDescription() : "");

                        String avatarUrl = user.getAvatarUrl();
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            com.bumptech.glide.Glide.with(ProfileActivity.this)
                                    .load(avatarUrl)
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .error(android.R.drawable.ic_dialog_alert)
                                    .into(ivAvatar);
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không thể tải thông tin cá nhân!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing(boolean isEnabled) {
        etName.setEnabled(isEnabled);
        etEmail.setEnabled(false);
        etAddress.setEnabled(isEnabled);
        etAvatarUrl.setEnabled(isEnabled);
        etDescription.setEnabled(isEnabled);

        float alpha = isEnabled ? 1.0f : 0.6f;
        etName.setAlpha(alpha);
        etEmail.setAlpha(0.6f);
        etAddress.setAlpha(alpha);
        etAvatarUrl.setAlpha(alpha);
        etDescription.setAlpha(alpha);
    }
}