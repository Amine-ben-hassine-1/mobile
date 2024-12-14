package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private Button addUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        addUserButton = findViewById(R.id.buttonAddUser);

        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivity(intent);
        });

        loadUsersFromFirestore();
    }

    private void loadUsersFromFirestore() {
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        user.setUserId(document.getId()); 
                        userList.add(user);
                    }
                    userAdapter = new UserAdapter(userList, new UserAdapter.OnUserClickListener() {
                        @Override
                        public void onEditUserClick(User user) {
                            Intent intent = new Intent(ManageUsersActivity.this, EditUserActivity.class);
                            intent.putExtra("userId", user.getUserId());
                            intent.putExtra("cin", user.getCin());
                            intent.putExtra("name", user.getName());
                            intent.putExtra("contact", user.getContact());
                            intent.putExtra("role", user.getRole());
                            startActivity(intent);
                        }

                        @Override
                        public void onDeleteUserClick(User user) {
                            deleteUser(user);
                        }
                    });
                    recyclerView.setAdapter(userAdapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur de récupération des utilisateurs.", Toast.LENGTH_SHORT).show());
    }

    private void deleteUser(User user) {
        db.collection("users").document(user.getUserId()).delete()
                .addOnSuccessListener(aVoid -> {
                    userList.remove(user);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Utilisateur supprimé avec succès.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur de suppression.", Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsersFromFirestore(); // Recharge les utilisateurs après retour de l'écran d'édition
    }
}
