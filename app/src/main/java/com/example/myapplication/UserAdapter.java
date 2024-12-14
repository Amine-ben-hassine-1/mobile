package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserClickListener listener;

    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText("Nom : " + user.getName());
        holder.roleTextView.setText("Rôle : " + user.getRole());
        holder.contactTextView.setText("Contact : " + user.getContact());

        holder.editButton.setOnClickListener(v -> listener.onEditUserClick(user));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserClickListener {
        void onEditUserClick(User user);
        void onDeleteUserClick(User user);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, roleTextView, contactTextView;
        Button editButton, deleteButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
