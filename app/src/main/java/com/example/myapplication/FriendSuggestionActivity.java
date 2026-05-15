package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class FriendSuggestionActivity extends AppCompatActivity {

    private static final int CONTACTS_PERMISSION_CODE = 100;
    private ListView lvSuggestedFriends;
    private List<Contact> suggestionList;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_suggestion);

        lvSuggestedFriends = findViewById(R.id.lvSuggestedFriends);
        suggestionList = new ArrayList<>();
        adapter = new ContactAdapter(this, suggestionList);
        lvSuggestedFriends.setAdapter(adapter);

        checkPermissionAndLoadContacts();
    }

    private void checkPermissionAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Xin quyền nếu chưa có
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_CODE);
        } else {
            // Đã có quyền
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền danh bạ để tìm bạn bè!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    private void loadContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Lọc cơ bản: Giả lập việc Backend trả về những liên hệ đã dùng app
                // Để test, ứng dụng sẽ chỉ gợi ý nếu danh bạ có tên khớp với "Alice", "Bob", "Zack", "Charlie" hoặc tên bạn
                if (isMockUserOnApp(name)) {
                    // Kiểm tra trùng lặp để không add 1 người 2 lần
                    if (!isAlreadyAdded(phone)) {
                        suggestionList.add(new Contact(name, phone));
                    }
                }
            }
            cursor.close();
            adapter.notifyDataSetChanged();

            if (suggestionList.isEmpty()) {
                Toast.makeText(this, "Không có ai trong danh bạ của bạn đang dùng app này.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Hàm giả lập kiểm tra xem người trong danh bạ có đăng ký app chưa
    private boolean isMockUserOnApp(String contactName) {
        String nameLower = contactName.toLowerCase();
        return nameLower.contains("alice") || nameLower.contains("bob") ||
                nameLower.contains("zack") || nameLower.contains("charlie") ||
                nameLower.contains("nguyễn") || nameLower.contains("xanghai");
        // Nếu bạn tạo 1 số điện thoại ảo trong danh bạ điện thoại test có tên "Zack", nó sẽ hiện lên!
    }

    private boolean isAlreadyAdded(String phone) {
        for (Contact c : suggestionList) {
            if (c.getPhoneNumber().equals(phone)) return true;
        }
        return false;
    }
}