package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import api.ApiClient;
import api.ApiService;
import models.FriendsResponse;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendSuggestionActivity extends AppCompatActivity {

    private ListView lvSuggestedFriends;
    private List<Contact> suggestionList;
    private ContactAdapter adapter;
    private int loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_suggestion);

        lvSuggestedFriends = findViewById(R.id.lvSuggestedFriends);
        suggestionList = new ArrayList<>();
        adapter = new ContactAdapter(this, suggestionList);
        lvSuggestedFriends.setAdapter(adapter);

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loggedInUserId = getIntent().getIntExtra("CURRENT_USER_ID", -1);
        if (loggedInUserId == -1) {
            loggedInUserId = userPrefs.getInt("Id", -1);
        }

        loadFriendsFromApi();
    }

    private void loadFriendsFromApi() {
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        apiService.getUserFriends(loggedInUserId).enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FriendsResponse friendsResponse = response.body();
                    suggestionList.clear();
                    List<User> friends = friendsResponse.getFriends();
                    if (friends != null) {
                        for (User user : friends) {
                            String phone = user.getPhone() != null ? user.getPhone() : "";
                            suggestionList.add(new Contact(user.getName(), phone));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (suggestionList.isEmpty()) {
                        Toast.makeText(FriendSuggestionActivity.this, "Bạn chưa có người bạn nào.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(FriendSuggestionActivity.this, "Không thể tải danh sách bạn bè!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                Toast.makeText(FriendSuggestionActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}