package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import api.ApiClient;
import api.ApiService;
import models.LoginRequest;
import models.LoginResponse;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiService apiService = ApiClient.getApiService();
                LoginRequest loginRequest = new LoginRequest(email, password);
                apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();

                            if ("success".equals(loginResponse.getStatus()) && loginResponse.getUser() != null) {
                                User user = loginResponse.getUser();

                                SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPrefs.edit();
                                editor.putInt("Id", user.getId());
                                editor.putString("Name", user.getName());
                                editor.putString("Email", user.getEmail());
                                editor.putString("Phone", user.getPhone() != null ? user.getPhone() : "");
                                editor.putString("Address", user.getAddress() != null ? user.getAddress() : "");
                                editor.putString("AvatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
                                editor.putString("Description", user.getDescription() != null ? user.getDescription() : "");
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("LOGGED_IN_USER", user.getName());
                                intent.putExtra("LOGGED_IN_USER_ID", user.getId());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Lỗi: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Sai Email hoặc Mật khẩu!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}