package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(@NonNull Context context, @NonNull List<Contact> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
        }

        Contact contact = getItem(position);
        TextView tvName = convertView.findViewById(R.id.tvContactName);
        TextView tvPhone = convertView.findViewById(R.id.tvContactPhone);
        Button btnAddFriend = convertView.findViewById(R.id.btnAddFriend);

        if (contact != null) {
            tvName.setText(contact.getName());
            tvPhone.setText(contact.getPhoneNumber());

            btnAddFriend.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Đã gửi lời mời kết bạn đến " + contact.getName(), Toast.LENGTH_SHORT).show();
            });
        }
        return convertView;
    }
}