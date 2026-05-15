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
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        createMockPosts();

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

        registerForContextMenu(lvPosts);
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

    private void createMockPosts() {
        if (postList.isEmpty()) {
            postList.add(new Post(
                    "Alice",
                    "24/03/2026",
                    "This is test content.\nThis is test content."
            ));

            postList.add(new Post(
                    "Zack",
                    "01/05/2026",
                    "Đang cày Arknights IS6 ending 2, có ai qua được chưa cho xin ít tip với!"
            ));

            postList.add(new Post(
                    "Alice",
                    "15/04/2026",
                    "Vừa xử lý xong lỗi 'no space left on device' trên máy ảo Ubuntu. Mất cả buổi chiều!"
            ));

            postList.add(new Post(
                    "Nguyễn Xuân Vĩ",
                    "04/05/2026",
                    "Đang test tính năng Sort và Menu của Simple Social App. Mọi thứ hoạt động hoàn hảo!"
            ));

            postList.add(new Post(
                    "Bob",
                    "20/02/2026",
                    "Sáng nay pha cà phê bằng phin nhôm Trung Nguyên ngon bá cháy. Năng lượng tràn trề để code C++!"
            ));

            postList.add(new Post(
                    "Charlie",
                    "10/03/2026",
                    "Hóng sự kiện The Game Awards sắp tới quá, không biết năm nay có game nào đột phá không."
            ));

            savePosts();
        }
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
            intent.putExtra("CURRENT_USER", loggedInUser);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_sort_date) {
            Collections.sort(postList, new Comparator<Post>() {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                @Override
                public int compare(Post p1, Post p2) {
                    try {
                        Date date1 = format.parse(p1.getDate());
                        Date date2 = format.parse(p2.getDate());
                        return date2.compareTo(date1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã sắp xếp theo ngày đăng", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_sort_author) {
            Collections.sort(postList, new Comparator<Post>() {
                @Override
                public int compare(Post p1, Post p2) {
                    return p1.getName().compareToIgnoreCase(p2.getName());
                }
            });
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã sắp xếp theo tên tác giả", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_find_friends) {
            Intent intent = new Intent(MainActivity.this, FriendSuggestionActivity.class);
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
            intent.putExtra("POST_AUTHOR", selectedPost.getName());
            intent.putExtra("CURRENT_USER", loggedInUser);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_hide) {
            postList.remove(position);
            adapter.notifyDataSetChanged();
            savePosts();
            Toast.makeText(this, "Đã ẩn bài đăng", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}