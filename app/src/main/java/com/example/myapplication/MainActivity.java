package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import api.ApiClient;
import api.ApiService;
import models.CreatePost;
import models.DeleteResponse;
import models.PostResponse;
import models.PostsResponse;
import models.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etWriteIdea;
    private Button btnPost;
    private ListView lvPosts;
    private List<Post> postList;
    private PostAdapter adapter;
    private String loggedInUser;
    private int loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loggedInUser = getIntent().getStringExtra("LOGGED_IN_USER");
        if (loggedInUser == null) {
            loggedInUser = userPrefs.getString("Name", "Người dùng");
        }
        loggedInUserId = getIntent().getIntExtra("LOGGED_IN_USER_ID", -1);
        if (loggedInUserId == -1) {
            loggedInUserId = userPrefs.getInt("Id", -1);
        }

        etWriteIdea = findViewById(R.id.etWriteIdea);
        btnPost = findViewById(R.id.btnPost);
        lvPosts = findViewById(R.id.lvPosts);

        postList = new ArrayList<>();
        adapter = new PostAdapter(this, postList);
        lvPosts.setAdapter(adapter);

        lvPosts.setOnItemClickListener((parent, view, position, id) -> {
            Post clickedPost = postList.get(position);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("POST_AUTHOR_ID", clickedPost.getUserId());
            if (clickedPost.getAuthor() != null) {
                intent.putExtra("POST_AUTHOR_NAME", clickedPost.getAuthor().getName());
            }
            intent.putExtra("CURRENT_USER", loggedInUser);
            intent.putExtra("CURRENT_USER_ID", loggedInUserId);
            startActivity(intent);
        });

        btnPost.setOnClickListener(v -> addNewPost());

        registerForContextMenu(lvPosts);

        loadPosts();
    }

    private void addNewPost() {
        String content = etWriteIdea.getText().toString().trim();
        if (content.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        CreatePost createPostRequest = new CreatePost(loggedInUserId, content);

        apiService.createPost(createPostRequest).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MainActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    etWriteIdea.setText("");
                    loadPosts();
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tạo bài đăng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        ApiService apiService = ApiClient.getApiService();
        apiService.getAllPosts().enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    List<Post> posts = response.body().getData();
                    if (posts != null) {
                        postList.addAll(posts);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải danh sách bài đăng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("POST_AUTHOR_ID", loggedInUserId);
            intent.putExtra("POST_AUTHOR_NAME", loggedInUser);
            intent.putExtra("CURRENT_USER", loggedInUser);
            intent.putExtra("CURRENT_USER_ID", loggedInUserId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_sort_date) {
            Collections.sort(postList, new Comparator<Post>() {
                @Override
                public int compare(Post p1, Post p2) {
                    if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) return 0;
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                }
            });
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã sắp xếp theo ngày đăng", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_sort_author) {
            Collections.sort(postList, new Comparator<Post>() {
                @Override
                public int compare(Post p1, Post p2) {
                    String name1 = (p1.getAuthor() != null) ? p1.getAuthor().getName() : "";
                    String name2 = (p2.getAuthor() != null) ? p2.getAuthor().getName() : "";
                    return name1.compareToIgnoreCase(name2);
                }
            });
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã sắp xếp theo tên tác giả", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_find_friends) {
            Intent intent = new Intent(MainActivity.this, FriendSuggestionActivity.class);
            intent.putExtra("CURRENT_USER_ID", loggedInUserId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvPosts) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Post selectedPost = postList.get(position);

        int id = item.getItemId();

        if (id == R.id.menu_detail) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("POST_AUTHOR_ID", selectedPost.getUserId());
            if (selectedPost.getAuthor() != null) {
                intent.putExtra("POST_AUTHOR_NAME", selectedPost.getAuthor().getName());
            }
            intent.putExtra("CURRENT_USER", loggedInUser);
            intent.putExtra("CURRENT_USER_ID", loggedInUserId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_hide) {
            if (selectedPost.getUserId() != loggedInUserId) {
                Toast.makeText(this, "Bạn không thể xóa bài đăng của người khác!", Toast.LENGTH_SHORT).show();
                return true;
            }
            ApiService apiService = ApiClient.getApiService();
            apiService.deletePost(selectedPost.getId()).enqueue(new Callback<DeleteResponse>() {
                @Override
                public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(MainActivity.this, "Đã xóa bài đăng!", Toast.LENGTH_SHORT).show();
                        loadPosts();
                    } else {
                        Toast.makeText(MainActivity.this, "Không thể xóa bài đăng!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DeleteResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return super.onContextItemSelected(item);
    }
}