package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText etWriteIdea;
    private Button btnPost;
    private ListView lvPosts;
    private List<Post> postList;
    private PostAdapter adapter;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loggedInUser = getIntent().getStringExtra("LOGGED_IN_USER");
        if(loggedInUser == null)
        {
            SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            loggedInUser = userPrefs.getString("Name", "Người dùng");
        }
        etWriteIdea = findViewById(R.id.etWriteIdea);
        btnPost = findViewById(R.id.btnPost);
        lvPosts = findViewById(R.id.lvPosts);

        postList = new ArrayList<>();

        loadPosts();

        adapter = new PostAdapter(this, postList);
        lvPosts.setAdapter(adapter);
        lvPosts.setOnItemClickListener((parent, view, position, id) -> {
            Post clickedPost = postList.get(position);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("POST_AUTHOR", clickedPost.getName());
            intent.putExtra("CURRENT_USER", loggedInUser);
            startActivity(intent);
        });
        btnPost.setOnClickListener(v -> addNewPost());
    }

    private void addNewPost() {
        String content = etWriteIdea.getText().toString().trim();
        if (content.isEmpty()){
            Toast.makeText(this, "Vui long nhap noi dung!",Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Post newPost = new Post(loggedInUser, currentDate, content);

        postList.add(0, newPost);
        adapter.notifyDataSetChanged();
        etWriteIdea.setText("");
        lvPosts.setSelection(0);
        savePosts();
    }

    private void savePosts() {
        SharedPreferences sharedPreferences = getSharedPreferences("PostData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            JSONArray jsonArray = new JSONArray();
            for (Post post : postList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", post.getName());
                jsonObject.put("date", post.getDate());
                jsonObject.put("content", post.getContent());
                jsonArray.put(jsonObject);
            }
            editor.putString("PostListString", jsonArray.toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadPosts() {
        SharedPreferences sharedPreferences = getSharedPreferences("PostData", MODE_PRIVATE);
        String jsonString = sharedPreferences.getString("PostListString", "[]");

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String date = jsonObject.getString("date");
                String content = jsonObject.getString("content");

                postList.add(new Post(name, date, content));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}